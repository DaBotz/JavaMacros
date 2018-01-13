package sgi.javaMacros.model.macros.execution.executors;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Field;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import sgi.clipboarder.ClipBoarder;
import sgi.gui.ConfigPanelCreator;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.macros.execution.ExecutesOnEventRaiseOnly_WithTimeout;
import sgi.javaMacros.model.macros.execution.Executor;
import sgi.javaMacros.model.macros.execution.NonDecoratingTextSaver;
import sgi.javaMacros.model.macros.sendkeys.LuaMacrosScriptScanner;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.LuaMacrosScriptEditorTextPane;

public class CLIPBOARD extends ExecutesOnEventRaiseOnly_WithTimeout {

	private String content;

	public void setContent(String content) {
		String old = this.content;
		this.content = content;
		notifyPropertyChange("content", old, content);
	}

	private String pastekeys;
	private boolean pasteAutomatically = true;
	private boolean useCRLF = false;
	// public static final String CRLF= new String(new char[] {13, 10});

	public CLIPBOARD() {
		super(true);
		pastekeys = "^v";
	}

	private transient LuaMacrosScriptScanner luaMacrosScriptParser = new LuaMacrosScriptScanner(false);

	protected boolean isPastingAutomatically() {
		return pasteAutomatically;
	}

	protected boolean isUseCRLF() {
		return useCRLF;
	}

	protected String getPastekeys() {
		return pastekeys;
	}

	protected String getContent() {

		return content;
	}

	private transient Component inputUI = null;

	@Override
	public Component getInputGUI() {

		if (inputUI != null)
			return inputUI;

		ConfigPanelCreator creator = new ConfigPanelCreator(Messages.M, getClass().getCanonicalName()) {
			@Override
			protected JComponent createTextField(IAwareOfChanges obj, Field field)

					throws IllegalArgumentException, IllegalAccessException {
				if ("pastekeys".equals(field.getName())) {
					final LuaMacrosScriptEditorTextPane jtp = new LuaMacrosScriptEditorTextPane(luaMacrosScriptParser);
					new NonDecoratingTextSaver(obj, field, jtp) {
						protected void updateComponent(IAwareOfChanges obj2, //
								Field field2, JTextComponent component)
								throws IllegalArgumentException, IllegalAccessException {
							super.updateComponent(obj2, field2, component);
							jtp.checkText();
						};
					}.updateComponent();
					return jtp;

				} else if (!"content".equals(field.getName()))
					return super.createTextField(obj, field);

				JTextArea jArea = new JTextArea();
				new NonDecoratingTextSaver(obj, field, jArea).updateComponent();

				Dimension preferredSize = new Dimension(getComboSize().width, getOccupiedRowHeight() * 8);
				jArea.setPreferredSize(preferredSize);
				jArea.setMinimumSize(preferredSize);
				jArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				return jArea;
			}

		};
		creator.setAddingEndButtons(false);
		creator.setUseFieldSeparators(true);

		return inputUI = creator.createConfigPanel(this);

	}

	@Override
	public int executeOnKeyUp(LuaEvent event) {

		ClipBoarder boarder = new ClipBoarder();
		String content2 = getContent();
		if (content2 != null && isUseCRLF())
			content2 = content2.replaceAll("\n", new String(new char[] { 13, 10 }));
		boarder.setClipboardContents(content2);
		if (isPastingAutomatically())
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						RobotAction.execute(luaMacrosScriptParser.parse(getPastekeys()));
					} catch (AWTException e) {

						e.printStackTrace();
					}
				}
			});

		{
		}

		return COMPLETE;
	}

	@Override
	public Executor copyMe() {
		CLIPBOARD clipboard = new CLIPBOARD();
		clipboard.content = content;
		clipboard.pasteAutomatically = pasteAutomatically;
		clipboard.pastekeys = pastekeys;
		clipboard.useCRLF = useCRLF;

		return clipboard;
	}

}

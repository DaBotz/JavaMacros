package sgi.javaMacros.model.macros.execution.executors;

import java.awt.AWTException;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import sgi.clipboarder.ClipBoarder;
import sgi.gui.ConfigPanelCreator;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.macros.execution.ExecutesOnEventRaiseOnly_WithTimeout;
import sgi.javaMacros.model.macros.execution.Executor;
import sgi.javaMacros.model.macros.sendkeys.LuaMacrosScriptScanner;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;

public class REVERSE_CASE extends ExecutesOnEventRaiseOnly_WithTimeout {

	public REVERSE_CASE() {
		super(true);
	}

	private String pasteKeySequence = "^v";
	private String cutOrCopyKeySequence = "^x";
	private String selectSAllKeySequence = "";
	private transient LuaMacrosScriptScanner scanner;

	@Override
	public int executeOnKeyUp(LuaEvent event) {
		if (scanner == null)
			scanner = new LuaMacrosScriptScanner(true);

		try {
			keyAct(selectSAllKeySequence);
			keyAct(cutOrCopyKeySequence);

			// if( true) return COMPLETE;

			ClipBoarder boarder = new ClipBoarder();
			String ct = boarder.getClipboardTextContents();
			if (!(ct == null || ct.isEmpty())) {
				char[] cArray = ct.toCharArray();
				for (int i = 0; i < cArray.length; i++) {
					char c = cArray[i];
					if (Character.isLowerCase(c)) {
						cArray[i] = Character.toUpperCase(c);
					} else if (Character.isUpperCase(c)) {
						cArray[i] = Character.toLowerCase(c);
					}
				}
				ct = new String(cArray);
				boarder.setClipboardContents(ct);
			}
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						keyAct(pasteKeySequence);
					} catch (AWTException e) {

					}
				}
			});

		} catch (AWTException e) {
			e.printStackTrace();
			return FAIL;
		}
		return COMPLETE;
	}

	protected void keyAct(String sequence) throws AWTException {
		if (!(sequence == null || sequence.isEmpty())) {
			ArrayList<RobotAction> parse = scanner.parse(sequence);
			RobotAction.execute(parse);
		}
	}

	@Override
	public Executor copyMe() {
		REVERSE_CASE reverseCase = new REVERSE_CASE();
		reverseCase.cutOrCopyKeySequence = cutOrCopyKeySequence;
		reverseCase.pasteKeySequence = pasteKeySequence;
		reverseCase.selectSAllKeySequence = selectSAllKeySequence;
		return reverseCase;
	}

	@Override
	public Component getInputGUI() {
		ConfigPanelCreator creator = getPanelCreator();
		creator.DefaultSetters.setDefaultTextFieldColumnWidth(5);
		creator.DefaultSetters.setDefaultComboBoxWidth(40);
		creator.setUseFieldSeparators(true);
		creator.setAddingEndButtons(false);

		return creator.createConfigPanel(this);

	}

}

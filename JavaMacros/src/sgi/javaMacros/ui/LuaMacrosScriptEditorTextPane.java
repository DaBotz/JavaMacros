package sgi.javaMacros.ui;

import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.basic.BasicTextUI.BasicCaret;

import sgi.gui.ComponentWalker;
import sgi.gui.IComponentModifier;
import sgi.javaMacros.model.JavaMacrosConfiguration;
import sgi.javaMacros.model.macros.sendkeys.KeyStrokeRecorder;
import sgi.javaMacros.model.macros.sendkeys.LuaMacrosScriptScanner;

public class LuaMacrosScriptEditorTextPane extends StyledTextPane {

	private boolean convertingKeyEventsToScript = false;

	private KeyStrokeRecorder strokeRecorder = new KeyStrokeRecorder();

	public KeyStrokeRecorder getStrokeRecorder() {
		return strokeRecorder;
	}

	public boolean isConvertingKeyEventsToScript() {
		return convertingKeyEventsToScript;
	}

	public void setConvertingKeyEventsToScript(boolean convertToScript) {
		this.convertingKeyEventsToScript = convertToScript;
		strokeRecorder.preLoad(this.luaMacrosScriptScanner.parse(getText()));
		setBackground(convertToScript
				? JavaMacrosConfiguration.instance().getColorsSettings().getLuamacrosEditorCTSmodeBackground()
				: Color.WHITE);
	}

	private class MacrosScriptSuggestor extends KeyAdapter {
		String lastText = "";

		@Override
		public void keyPressed(KeyEvent e) {
			if (isConvertingKeyEventsToScript()) {
				strokeRecorder.keyStroke(e, true);
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// checkText();
		}

		@Override
		public void keyReleased(KeyEvent e) {

			int mark = getCaretPosition();
			int markSStart=getSelectionStart(); 
			int markSEnd=getSelectionEnd(); 
			char keyChar = e.getKeyChar();
			int keyCode = e.getKeyCode();
		//	int modifiers = e.getModifiers();

		
			boolean cTS = isConvertingKeyEventsToScript();

			if (keyCode == 76 && e.isControlDown()) {
				setConvertingKeyEventsToScript(!cTS);
				return;
			}

			KeyEvent transEv = new KeyEvent((Component) e.getSource(), e.getID(), e.getWhen(), 0, keyCode,
					(char) keyCode, e.getKeyLocation());
			keyChar = transEv.getKeyChar();

			if (!cTS && keyChar == KeyEvent.CHAR_UNDEFINED)
				return;

			checkText();
			String text = getText();

			if (keyChar == ' ' && e.isControlDown())
				showSuggestions(text);
			else if (cTS) { // && !isModiferKey(keyCode)
				strokeRecorder.keyStroke(e, false);
				setText(strokeRecorder.getActionsAsText());
				// recordKeys(keyChar, keyCode, modifiers);
			} else if (Character.isUpperCase(keyChar)//
					&& lastText.lastIndexOf('{') > lastText.lastIndexOf('}')) {
				setText(lastText + Character.toLowerCase(keyChar));
			}

			lastText = getText();

			setCaretPosition(Math.min(lastText.length() , mark));
			setSelectionStart(Math.min(lastText.length() , markSStart));
			setSelectionEnd(Math.min(lastText.length() , markSEnd));
		
		}

		public void showSuggestions(String text) {
			{
				int last = text.lastIndexOf('{');
				last = Math.max(last, text.lastIndexOf('}'));
				last = Math.max(last, text.lastIndexOf(' '));
				last = Math.max(last, text.lastIndexOf('\n'));

				if (last >= 0) {
					hint = text.substring(last).toLowerCase();
					pre_hint = text.substring(0, last);
				} else {
					hint = "";
					pre_hint = text;
				}

				String[][] suggestions = getSuggestions(hint.trim());
				if (suggestions.length > 0) {

					final JDialog suggestionDialog = new JDialog(getOwner(), "Suggestions", ModalityType.MODELESS);
					suggestionDialog.setUndecorated(true);
					suggestionDialog.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

					final JTable view = new JTable(suggestions, new String[] { "" });

					view.setTableHeader(null);
					view.setForeground(Color.GRAY);

					view.setSelectionBackground(new Color(220, 220, 255));
					view.setFont(view.getFont().deriveFont(16f));
					view.setBackground(new Color(255, 255, 233));

					view.setEditingColumn(0);

					JScrollPane cp2 = new JScrollPane(view);
					cp2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					cp2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
					suggestionDialog.setContentPane(cp2);
					suggestionDialog.getContentPane().setPreferredSize(new Dimension(200, 200));
					suggestionDialog.pack();

					// Point location = LuaMacrosScriptEditorTextPane.this.getLocation();
					BasicCaret caret = (BasicCaret) getCaret();

					Point l2 = LuaMacrosScriptEditorTextPane.this.getLocationOnScreen();

					suggestionDialog.setLocation(// location.x +
							caret.x + l2.x + 4//
							, // location.y +
							caret.y + +l2.y + 18);

					view.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

					suggestionDialog.addWindowListener(new WindowAdapter() {
						@Override
						public void windowDeactivated(WindowEvent e) {
							Window window = e.getWindow();
							window.setVisible(false);
							window.dispose();

						}
					});

					final KeyAdapter keyInterceptor = new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent e) {
							int keyCode = e.getKeyCode();
							switch (keyCode) {
							case VK_ESCAPE:
								suggestionDialog.setVisible(false);
								return;
							case VK_ENTER:
								System.out.println(e);
								String txt = suggestionData[view.getSelectedRow()][0];
								setText(pre_hint + txt);
								checkText();
								suggestionDialog.setVisible(false);
								return;

							}

						}
					};

					new ComponentWalker(new IComponentModifier() {

						@Override
						public void modify(Component component) {
							if (component instanceof JComponent) {
								JComponent new_name = (JComponent) component;
								new_name.addKeyListener(keyInterceptor);
							}

						}
					}).walk(suggestionDialog);

					suggestionDialog.addKeyListener(keyInterceptor);

					suggestionDialog.setVisible(true);
					return;
				}
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2403075706913500L;

	private LuaMacrosScriptScanner luaMacrosScriptScanner;

	/**
	 * Create the frame.
	 */
	public LuaMacrosScriptEditorTextPane(LuaMacrosScriptScanner luaMacrosScriptScanner) {
		super(luaMacrosScriptScanner);
		this.luaMacrosScriptScanner = luaMacrosScriptScanner;

	}

	String newline = "\n";

	private String hint;

	protected String pre_hint;

	private String[][] suggestionData;

	@Override
	public KeyAdapter getKeyAdapter() {
		return new MacrosScriptSuggestor();
	}

	public String[][] getSuggestions(String strippedHint) {
		ArrayList<String> scripNames = luaMacrosScriptScanner.getCodes().getScripNames();
		ListIterator<String> li = scripNames.listIterator();

		ArrayList<String[]> pa1 = new ArrayList<>();

		while (li.hasNext()) {
			String nm = (String) li.next();
			if (nm.startsWith(strippedHint) && !nm.trim().isEmpty())
				pa1.add(new String[] { nm });
		}
		final String[][] data = new String[pa1.size()][1];
		pa1.toArray(data);
		return suggestionData = data;
	}

}

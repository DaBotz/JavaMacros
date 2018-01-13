package sgi.javaMacros.ui;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import sgi.generic.debug.Debug;
import sgi.javaMacros.model.JavaMacrosColors;
import sgi.javaMacros.model.JavaMacrosConfiguration;
import sgi.javaMacros.model.macros.execution.IScanner;
import sgi.javaMacros.model.macros.execution.ScriptSegment;
import sgi.javaMacros.model.macros.sendkeys.LuaMacrosScriptScanner;

public class StyledTextPane extends JTextPane {

	private static final long serialVersionUID = -2403072935706913500L;

	private Style[] styles;
	private IScanner scanner;

	public StyledTextPane(IScanner scanner) {
		super();
		this.scanner = scanner;
		styleUp();
		addKeyListener(getKeyAdapter());

	}

	protected Window getOwner() {
		Container parent = getParent();
		while (parent != null && !(parent instanceof Window)) {
			parent = parent.getParent();
		}
		
		return (Window) parent;
	}

	public KeyAdapter getKeyAdapter() {
		return new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				
				
				if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
					return;
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						checkText();
					}
				});
			}

		};
	}

	public void styleUp() {
		JTextPane textPane = this;
		StyledDocument docs = textPane.getStyledDocument();
		addStylesToDocument(docs);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	public void checkText() {
		String text = getText();
		scanner.scan(text);
		ArrayList<ScriptSegment> segments = scanner.getSegmentation();

		int mark = getCaretPosition();
		int markSStart=getSelectionStart(); 
		int markSEnd=getSelectionEnd(); 
		
		try {
			StyledDocument doc2 = (StyledDocument) getDocument();
			doc2.remove(0, doc2.getLength());
			for (ScriptSegment seg : segments) {
				doc2.insertString(doc2.getLength(), seg.getPart(), styles[seg.getStatus()]);
			}
			
			text = getText();
			setCaretPosition(Math.min(text.length() , mark));
			setSelectionStart(Math.min(text.length() , markSStart));
			setSelectionEnd(Math.min(text.length() , markSEnd));

		} catch (Exception e1) {
			Debug.info(e1, 4);
		}
	}

	protected void addStylesToDocument(StyledDocument doc) {
		// Initialize some styles.
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(def, "SansSerif");
		JavaMacrosColors cSet = JavaMacrosConfiguration.instance().getColorsSettings();

		styles = new Style[5];

		Style s = doc.addStyle("error", def);
		styles[LuaMacrosScriptScanner.ERROR] = s;
		StyleConstants.setItalic(s, true);
		StyleConstants.setForeground(s, cSet.getErrorsColor());
		StyleConstants.setUnderline(s, true);
		// StyleConstants.setUnderline(s, true);
		// StyleConstants.setStrikeThrough(s, true);

		Style regular = doc.addStyle("NORMAL", def);

		StyleConstants.setForeground(regular, cSet.getNormalScriptColor());
		styles[LuaMacrosScriptScanner.NORMAL] = regular;

		s = doc.addStyle("GROUP", def);
		styles[LuaMacrosScriptScanner.GROUP] = s;
		StyleConstants.setForeground(s, cSet.getGroupsColor());

		s = doc.addStyle("MODIFIER", def);
		styles[LuaMacrosScriptScanner.MODIFIER] = s;
		StyleConstants.setForeground(s, cSet.getModifiersColor());
		StyleConstants.setBold(s, true);

		s = doc.addStyle("ESCAPE", def);
		styles[LuaMacrosScriptScanner.ESCAPE] = s;
		StyleConstants.setForeground(s, cSet.getEscapesColor());
		StyleConstants.setBold(s, true);

	}

}

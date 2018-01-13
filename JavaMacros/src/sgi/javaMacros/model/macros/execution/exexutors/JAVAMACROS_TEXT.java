package sgi.javaMacros.model.macros.execution.executors;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import sgi.gui.ComponentWalker;
import sgi.gui.ConfigPanelCreator;
import sgi.gui.IComponentModifier;
import sgi.gui.RenderingType;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.enums.KeyStrokeRecorderModes;
import sgi.javaMacros.model.macros.execution.ErrorCode;
import sgi.javaMacros.model.macros.execution.ExecutesOnEventRaiseOnly_WithTimeout;
import sgi.javaMacros.model.macros.execution.Executor;
import sgi.javaMacros.model.macros.execution.NonDecoratingTextSaver;
import sgi.javaMacros.model.macros.sendkeys.KeyCodeChecker;
import sgi.javaMacros.model.macros.sendkeys.KeyStrokeRecorder;
import sgi.javaMacros.model.macros.sendkeys.LuaMacrosScriptScanner;
import sgi.javaMacros.model.macros.sendkeys.ScanEvent;
import sgi.javaMacros.model.macros.sendkeys.ScannerListener;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.LuaMacrosScriptEditorTextPane;
import sgi.javaMacros.ui.icons.Icons;

public class JAVAMACROS_TEXT extends ExecutesOnEventRaiseOnly_WithTimeout implements ScannerListener {

	public boolean isJavaMode() {
		return true;
	}

	private String errorsText;
	private String sendKeysText;
	private KeyStrokeRecorderModes recorderMode;

	private transient ArrayList<RobotAction> parsedActions;
	private transient boolean recording;
	private transient KeyListener allAdapter;
	private transient LuaMacrosScriptScanner luaMacrosScriptParser;
	private transient KeyStrokeRecorder keyStrokeRecorder = null;
	// private transient JPanel inputUI;
	private transient ImageIcon rec;
	private transient ImageIcon stop;

	protected void record(int eKeyCode, boolean down) {
		if (isRecording()) {
			switch (recorderMode) {
			case CONDENSED:
			case NORMAL:
				keyStrokeRecorder.setCompressedRecording(//
						recorderMode == KeyStrokeRecorderModes.CONDENSED);
				keyStrokeRecorder.keyStroke(eKeyCode, down);
				break;
			case RAW:
				keyStrokeRecorder.keyStrokeRaw(eKeyCode, down);
				break;
			default:
				break;
			}
			sendKeysTextPane.setText(keyStrokeRecorder.getActionsAsText());
			sendKeysTextPane.checkText();
		}
	}

	protected transient LuaMacrosScriptEditorTextPane sendKeysTextPane;
	protected transient JTextPane errorsTextArea;
	// private transient JPanel inputUI;

	public JAVAMACROS_TEXT() {
		super(true);
		luaMacrosScriptParser = new LuaMacrosScriptScanner(isJavaMode());
		luaMacrosScriptParser.addScannerListener(this);
		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				parsedActions = null;
			}
		});
	}

	public String getSendKeysText() {
		return sendKeysText;
	}

	public void setSendKeysText(String sendKeysText) {
		String oldText = this.sendKeysText;
		this.sendKeysText = sendKeysText;
		notifyPropertyChange("sendKeysText", oldText, sendKeysText);

	}

	@Override
	public Component getInputGUI() {

		if (rec == null)
			rec = Icons.getIcon("keystrokerecorder_rec");
		if (stop == null)
			stop = Icons.getIcon("keystrokerecorder_stop");
		if (recorderMode == null)
			recorderMode = KeyStrokeRecorderModes.NORMAL;

		if (keyStrokeRecorder == null)
			keyStrokeRecorder = new KeyStrokeRecorder();

		if (allAdapter == null)
			allAdapter = new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					record(KeyCodeChecker.getAcceptableKeyCode(e), false);
				}

				@Override
				public void keyPressed(KeyEvent e) {
					record(KeyCodeChecker.getAcceptableKeyCode(e), true);
				}
			};

		// if (inputUI != null)
		// return inputUI;

		ConfigPanelCreator creator = new ConfigPanelCreator(Messages.M, getClass().getCanonicalName()) {

			@Override
			protected JComponent getLabel(String name) {

				if ("errorsText".equals(name))
					return new JLabel();

				JComponent label = super.getLabel(name);
				if ("recorderMode".equals(name)) {

					JPanel panel = new JPanel(new BorderLayout(0, 0));

					panel.add(label, BorderLayout.WEST);
					final JButton btn = createRecorderButton();
					panel.add(btn, BorderLayout.EAST);
					return panel;
				}

				return label;
			}
			
//			protected JComponent createEnumEditor(IAwareOfChanges anObject, Field aField) throws IllegalArgumentException ,IllegalAccessException {
//				
//				JComponent selector = super.createEnumEditor(anObject, aField);
//				
//				
//				
//				
//
//				JButton btn = createRecorderButton();
//
//				GridBagLayout layout = new GridBagLayout();
//				JPanel panel = new JPanel(layout);
//				GridBagConstraints gbc = new GridBagConstraints();
//				gbc.gridx = 0;
//				gbc.fill = GridBagConstraints.NONE;
//				gbc.anchor = GridBagConstraints.NORTHWEST;
//
//				panel.add(btn, gbc);
//				gbc.gridx += 2;
//				gbc.fill = GridBagConstraints.HORIZONTAL;
//				gbc.anchor = GridBagConstraints.EAST;
//				panel.add(selector, gbc.clone());
//				layout.columnWeights = new double[] { 0, 0, 1 };
//				layout.columnWidths = new int[] { 36, 10, 200 };
//				layout.rowHeights = new int[] { 36, 3 };
//				layout.rowWeights = new double[] { 0, 0, 1 };
//				
//				
//				
//				
//				
//				return panel;
//			}
			
			

			public JButton createRecorderButton() {
				final JButton btn = new JButton(rec);

				btn.setContentAreaFilled(false);
				btn.setBorderPainted(false);
				btn.setPreferredSize(new Dimension(36, 36));
				ActionListener recorderLauncher = new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						setRecording(!isRecording());
						if (isRecording()) {
							sendKeysTextPane.setBackground(new Color(255, 255, 220));
							ArrayList<RobotAction> parsed = luaMacrosScriptParser//
									.parse(sendKeysTextPane.getText());
							keyStrokeRecorder.preLoad(parsed);
							if (stop == null)
								btn.setText("Stop");

							btn.setIcon(stop);

						} else {
							if (rec == null)
								btn.setText("Rec.");
							sendKeysTextPane.setBackground(Color.WHITE);

							btn.setIcon(rec);
						}
					}
				};
				btn.addActionListener(recorderLauncher);
				return btn;
			}

			@Override
			protected JComponent createTextField(IAwareOfChanges obj, Field field)

					throws IllegalArgumentException, IllegalAccessException {

				if ("errorsText".equals(field.getName())) {
					final JScrollPane pane = new JScrollPane();
					final Dimension preferredSize = new Dimension(getComboSize().width, getOccupiedRowHeight());
					final Dimension bigSize = new Dimension(getComboSize().width, getOccupiedRowHeight() * 6);
					errorsTextArea = new JTextPane() {
						/**
						 * 
						 */
						private static final long serialVersionUID = -3625576460387396986L;

						public void setText(String t) {
							super.setText(t);
							boolean aFlag = t != null && !t.trim().isEmpty();
							if (aFlag) {
								pane.setPreferredSize(bigSize);
								pane.setSize(bigSize);

							} else {
								pane.setPreferredSize(preferredSize);
								pane.setSize(preferredSize);

							}
							pane.setVisible(aFlag);
							pane.revalidate();

						};
					};
					pane.setPreferredSize(preferredSize);
					errorsTextArea.setPreferredSize(preferredSize);
					errorsTextArea.setMinimumSize(preferredSize);
					// errorsTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					errorsTextArea.setBackground(null);
					errorsTextArea.setForeground(Color.RED);
					errorsTextArea.setFont(errorsTextArea.getFont().deriveFont(Font.ITALIC));
					errorsTextArea.setText("");
					errorsTextArea.setEditable(false);
					pane.setViewportView(errorsTextArea);
					pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
					return pane;

				}

				if ("sendKeysText".equals(field.getName())) {
					sendKeysTextPane = new LuaMacrosScriptEditorTextPane(luaMacrosScriptParser);
					new NonDecoratingTextSaver(obj, field, sendKeysTextPane).updateComponent();
					sendKeysTextPane.addPropertyChangeListener(new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							try {
								keyStrokeRecorder.preLoad(//
										luaMacrosScriptParser.getLastParsedActions());
							} catch (Throwable e) {
							}
						}
					});

					Dimension preferredSize = new Dimension(getComboSize().width, getOccupiedRowHeight() * 8);
					sendKeysTextPane.setPreferredSize(preferredSize);
					sendKeysTextPane.setMinimumSize(preferredSize);
					sendKeysTextPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					
					

					return sendKeysTextPane;
				}

				return super.createTextField(obj, field);
			}

		};
		creator.setAddingEndButtons(false);
		// creator.FieldsSettings.setInvisibleFields("parsedActions");
		creator.DefaultSetters.setDefaultEnumRenderingType(//
				RenderingType.HORIZONTAL_RADIOBUTTONS);

		creator.setUseFieldSeparators(false);

		JPanel basePanel = creator.createConfigPanel(this);

		new ComponentWalker(new IComponentModifier() {
			@Override
			public void modify(Component component) {
				if (component instanceof JComponent) {
					JComponent c = (JComponent) component;
					// if (c != sendKeysTextPane)
					c.addKeyListener(allAdapter);
				}
			}
		}).walk(basePanel);

		return // inputUI =
		basePanel;

	}

	/*
	 * (non-Javadoc)
	 * @see sgi.javaMacros.model.macros.executtest  scrittura
	 *  ion.ExecutesOnEventRaiseOnly_WithTimeout#executeOnKeyUp(sgi.javaMacros.controller.LuaEvent)
	 */
	
	@Override
	protected int executeOnKeyUp(LuaEvent event) {
		if (parsedActions == null)
			parsedActions = luaMacrosScriptParser.parse(getSendKeysText());

		try {
			RobotAction.execute(parsedActions);
		} catch (AWTException e) {

			e.printStackTrace();
			return FAIL;
		}
		return COMPLETE;
	}

	@Override
	public Executor copyMe() {
		JAVAMACROS_TEXT copyBase = new JAVAMACROS_TEXT();
		if (this.parsedActions != null)
			copyBase.parsedActions = new ArrayList<>(this.parsedActions);

		copyBase.sendKeysText = sendKeysText;

		return copyBase;
	}

	@Override
	public void readScanEvent(ScanEvent evt) {

		StringBuffer buffer = new StringBuffer();
		ArrayList<ErrorCode> errorcodes = luaMacrosScriptParser.getErrorcodes();
		for (ErrorCode ec : errorcodes) {
			buffer.append(ec.getMessage());
			buffer.append("\n");

		}
		errorsTextArea.setText(buffer.toString());

	}

	public boolean isRecording() {
		return recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	public String getErrorsText() {
		return errorsText;
	}

	public void setErrorsText(String errorsText) {
	}

}

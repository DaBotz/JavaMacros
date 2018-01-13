package sgi.javaMacros.model.macros.execution.executors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;

import sgi.gui.ComponentWalker;
import sgi.gui.ConfigPanelCreator;
import sgi.gui.IComponentModifier;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.model.macros.execution.Executor;
import sgi.javaMacros.model.macros.execution.NonDecoratingTextSaver;
import sgi.javaMacros.model.macros.sendkeys.DumbScanner;
import sgi.javaMacros.model.macros.sendkeys.LmcKeyStrokeRecorder;
import sgi.javaMacros.model.macros.sendkeys.LuaMacrosScriptScanner;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.LuaMacrosScriptEditorTextPane;
import sgi.javaMacros.ui.StyledTextPane;
import sgi.javaMacros.ui.icons.Icons;

public class LUAMACROS_DIRECT_CODE extends Executor {

	private transient LmcKeyStrokeRecorder keyStrokeRecorder;
	private static ImageIcon rec = Icons.getIcon("keystrokerecorder_rec");//// $NON-NLS-N$ //$NON-NLS-1$
	private static ImageIcon stop = Icons.getIcon("keystrokerecorder_stop");//// $NON-NLS-N$ //$NON-NLS-1$
	private static ImageIcon closed = Icons.getIcon("code_locked");//// $NON-NLS-N$ //$NON-NLS-1$
	private static ImageIcon open = Icons.getIcon("code_open");//// $NON-NLS-N$ //$NON-NLS-1$

	public static final String START_STRING = "[[";//// $NON-NLS-N$ //$NON-NLS-1$
	public static final String END_STRING = "]]";//// $NON-NLS-N$ //$NON-NLS-1$
	public static final String START_CODE = "--[[ delay > ]] ";//// $NON-NLS-N$ //$NON-NLS-1$
	public static final String END_CODE = " --[[ < delay ]] ";//// $NON-NLS-N$ //$NON-NLS-1$

	private static boolean secondTime;

	private long delay = 0;

	private static String baseCode = //
			"\n" // $NON-NLS-N$ //$NON-NLS-1$
					+ "      function( device, scanCode, direction, modifiers, deviceType)\n" // $NON-NLS-N$ //$NON-NLS-1$
					+ "\n" // $NON-NLS-N$ //$NON-NLS-1$
					+ "                         if direction == 0 then \n" //// $NON-NLS-N$ //$NON-NLS-1$
					+ "                              return \n" //// $NON-NLS-N$ //$NON-NLS-1$
					+ "                         end \n" //// $NON-NLS-N$ //$NON-NLS-1$
					+ "\n" // $NON-NLS-N$ //$NON-NLS-1$
					+ "                         lmc_send_keys( " //// $NON-NLS-N$ //$NON-NLS-1$
					+ START_STRING//// $NON-NLS-N$
					+ "text here" // $NON-NLS-N$ //$NON-NLS-1$
					+ END_STRING // $NON-NLS-N$
					+ " ,\n                                        " // $NON-NLS-N$ //$NON-NLS-1$
					+ START_CODE // $NON-NLS-N$
					+ "0" // $NON-NLS-N$ //$NON-NLS-1$
					+ END_CODE // $NON-NLS-N$
					+ " )\n" /// $NON-NLS-N$ //$NON-NLS-1$
					// + "\n" // $NON-NLS-N$
					// + " -- Note: return false only IF you want to propagate\n" // $NON-NLS-N$
					// + " -- anyway the event from LuaMacros to JavaMacros\n" // $NON-NLS-N$
					+ "\n" // $NON-NLS-N$ //$NON-NLS-1$
					+ "                      return false\n" // $NON-NLS-N$ //$NON-NLS-1$
					+ "\n" // $NON-NLS-N$ //$NON-NLS-1$
					+ "      end"; // $NON-NLS-N$ //$NON-NLS-1$

	private String functionCode;

	public String get_code() {
		if (functionCode == null)
			functionCode = baseCode;
		return functionCode;
	}

	private String lmcSendkeysText = "text here"; //$NON-NLS-1$

	public String getLmcSendkeysText() {
		return lmcSendkeysText;
	}

	public void setLmcSendkeysText(String lmcSendkeysText) {
		String oldCode = this.lmcSendkeysText;
		this.lmcSendkeysText = lmcSendkeysText;
		notifyPropertyChange("lmcSendkeysText", oldCode, lmcSendkeysText); //$NON-NLS-1$

	}

	private transient LuaMacrosScriptScanner luaMacrosScriptParser;
	private transient Component gui;

	public void setFunctionCode(String _code) {

		String oldCode = this.functionCode;
		this.functionCode = _code;
		notifyPropertyChange("functionCode", oldCode, _code); //$NON-NLS-1$
	}

	@Override
	public int execute(LuaEvent event) {
		return PASS;
	}

	protected void record(int eKeyCode, boolean down) {
		if (isRecording()) {

			keyStrokeRecorder.keyStroke(eKeyCode, down);
			String actionsAsText = keyStrokeRecorder.getActionsAsText();
			sendKeysTextPane.setText(actionsAsText);
			sendKeysTextPane.checkText();
			setLmcSendkeysText(actionsAsText);
		}
	}

	@Override
	public Executor copyMe() {
		return null;
	}

	@Override
	public boolean usesDirectCode() {
		return true;
	}

	@Override
	public String getDirectCode() {
		// StringBuffer buffer = new StringBuffer();
		// Macro macro = getMacro();
		// int scanCode = macro.getScanCode();
		// CompoundDevice cpDevice = macro.getDevice();
		// if (cpDevice != null) {
		//
		// HashSet<Device> devices = cpDevice.getDevices();
		// buffer.append(" local funct;\n funct = "); //// $NON-NLS-N$ //$NON-NLS-1$
		// buffer.append(get_code().trim());
		// buffer.append("\n\n");
		//
		// for (Device device : devices) {
		// buffer.append(" JMA.addDirect('" + device.getLuaMacrosId() + "', " + scanCode
		// + ", funct ) \n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// }
		//
		// return buffer.toString();
		// }
		if (get_code() != null)
			return get_code().trim();

		return null;
	}

	@Override
	public Component getInputGUI() {
		if (gui != null)
			return gui;

		Component inputGUI = gui = super.getInputGUI();

		new ComponentWalker(new IComponentModifier() {
			@Override
			public void modify(Component component) {
				if (component == mainCodeArea)
					return;

				if (component instanceof JComponent) {
					JComponent c = (JComponent) component;
					// if (c != sendKeysTextPane)
					c.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
							// record(e.getKeyCode(), false);
						}

						@Override
						public void keyReleased(KeyEvent e) {

							record(e.getKeyCode(), false);
						}

						@Override
						public void keyPressed(KeyEvent e) {
							record(e.getKeyCode(), true);

						}
					});
				}
			}
		}).walk(inputGUI);

		return inputGUI;
	}

	private transient LuaMacrosScriptEditorTextPane sendKeysTextPane;
	private transient boolean recording;
	private transient StyledTextPane mainCodeArea;

	protected ConfigPanelCreator getPanelCreator() {

		if (keyStrokeRecorder == null) {
			keyStrokeRecorder = new LmcKeyStrokeRecorder();
		}

		if (luaMacrosScriptParser == null)
			luaMacrosScriptParser = new LuaMacrosScriptScanner(false);

		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("lmcSendkeysText".equalsIgnoreCase(evt.getPropertyName()) //$NON-NLS-1$
						|| "delay".equalsIgnoreCase(evt.getPropertyName()) //$NON-NLS-1$

				) {
					String text = mainCodeArea.getText();
					String newValue = lmcSendkeysText;
					text = insertValue(START_STRING, END_STRING, text, newValue);
					text = insertValue(START_CODE, END_CODE, text, "" + delay);//// $NON-NLS-N$ //$NON-NLS-1$

					mainCodeArea.setText(text);
					setFunctionCode(text);
					mainCodeArea.checkText();

				}

			}

			public String insertValue(String startSeq, String endSequence, String text, String newValue) {
				int indexOf;
				indexOf = text.indexOf(startSeq);

				if (indexOf >= 0) {
					int _end = text.indexOf(endSequence, indexOf);
					text = text.substring(0, indexOf + startSeq.length()) //
							+ incapsulate(newValue) //
							+ text.substring(_end);

				}
				return text;
			}
		});

		ConfigPanelCreator creator = new ConfigPanelCreator(Messages.M, getClass().getName()) {

			public JButton getRecordButton() {
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
								btn.setText("Stop"); //$NON-NLS-1$
							else
								btn.setIcon(stop);

						} else {
							if (rec == null)
								btn.setText("Rec."); //$NON-NLS-1$
							else
								btn.setIcon(rec);
							sendKeysTextPane.setBackground(Color.WHITE);
						}
					}
				};
				btn.addActionListener(recorderLauncher);
				return btn;
			}

			@Override
			protected JComponent createTextField(IAwareOfChanges obj, Field field)
					throws IllegalArgumentException, IllegalAccessException {
				if ("lmcSendkeysText".equals(field.getName())) { //$NON-NLS-1$
					JButton btn = getRecordButton();
					buildSenDeystextPane(obj, field);
					StyledTextPane sendKeysTextPane2 = sendKeysTextPane;

					GridBagLayout layout = new GridBagLayout();
					JPanel panel = new JPanel(layout);
					GridBagConstraints gbc = new GridBagConstraints();
					gbc.gridx = 0;
					gbc.fill = GridBagConstraints.NONE;
					gbc.anchor = GridBagConstraints.NORTHWEST;

					panel.add(btn, gbc);
					gbc.gridx += 2;
					gbc.fill = GridBagConstraints.HORIZONTAL;
					gbc.anchor = GridBagConstraints.EAST;
					panel.add(sendKeysTextPane2, gbc.clone());
					layout.columnWeights = new double[] { 0, 0, 1 };
					layout.columnWidths = new int[] { 36, 10, 200 };
					layout.rowHeights = new int[] { 36, 3 };
					layout.rowWeights = new double[] { 0, 0, 1 };

					return panel;

					// return sendKeysTextPane;

				} else

				if (!"functionCode".equals(field.getName())) //$NON-NLS-1$
					return super.createTextField(obj, field);

				buildCodeArea(field);

				final JButton btn = new JButton(closed);
				if (closed == null)
					btn.setText("unlock"); //$NON-NLS-1$
				else {
					btn.setContentAreaFilled(false);
					btn.setBorderPainted(false);

					btn.setPreferredSize(new Dimension(36, 36));

				}
				btn.setToolTipText("Click to unlock"); //$NON-NLS-1$

				btn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						boolean enabled = !mainCodeArea.isEnabled();
						mainCodeArea.setEnabled(enabled);

						if (enabled) {
							if (open == null)
								btn.setText("unlock"); //$NON-NLS-1$
							else
								btn.setIcon(open);

							btn.setToolTipText("Click to unlock"); //$NON-NLS-1$
							if (!secondTime) {
								secondTime = true;

								int q = JOptionPane.showConfirmDialog(null, //
										Messages.htmlSwing(//
												Messages.M.getString("LUAMACROS_DIRECT_CODE.Warning")), // //$NON-NLS-1$
										"JavaMacros - WARNING!", JOptionPane.OK_CANCEL_OPTION, //$NON-NLS-1$
										JOptionPane.WARNING_MESSAGE, new ImageIcon(Messages.M.getIcon()

										));

								switch (q) {
								case JOptionPane.CANCEL_OPTION:
								case JOptionPane.NO_OPTION:
									btn.doClick();
								}
							}

							btn.setToolTipText("Click to lock"); //$NON-NLS-1$
						} else {
							if (closed == null)
								btn.setText("unlock"); //$NON-NLS-1$

							else
								btn.setIcon(closed);

							btn.setToolTipText("Click to unlock"); //$NON-NLS-1$
						}
					}
				});

				GridBagLayout layout = new GridBagLayout();
				JPanel panel = new JPanel(layout);
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.fill = GridBagConstraints.NONE;
				gbc.anchor = GridBagConstraints.NORTHWEST;

				panel.add(btn, gbc);
				gbc.gridx += 2;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.anchor = GridBagConstraints.EAST;
				panel.add(new JScrollPane(mainCodeArea), gbc.clone());

				layout.columnWeights = new double[] { 0, 0, 1 };
				layout.columnWidths = new int[] { 36, 10, 200 };
				layout.rowHeights = new int[] { 36, 3 };
				layout.rowWeights = new double[] { 0, 0, 1 };

				return panel;

			}

			public void buildCodeArea(Field field) {
				get_code();
				mainCodeArea = new StyledTextPane(new DumbScanner());
				new NonDecoratingTextSaver(LUAMACROS_DIRECT_CODE.this, field, mainCodeArea).updateComponent();

				Dimension preferredSize = new Dimension(getComboSize().width, getOccupiedRowHeight() * 11);
				mainCodeArea.setPreferredSize(preferredSize);
				mainCodeArea.setMinimumSize(preferredSize);
				mainCodeArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				mainCodeArea.setEnabled(false);
				mainCodeArea.checkText();
			}

			public void buildSenDeystextPane(IAwareOfChanges obj, Field field) {
				sendKeysTextPane = new LuaMacrosScriptEditorTextPane(luaMacrosScriptParser);
				new NonDecoratingTextSaver(obj, field, sendKeysTextPane) {
					protected void updateComponent(IAwareOfChanges obj2, //
							Field field2, JTextComponent component)
							throws IllegalArgumentException, IllegalAccessException {
						super.updateComponent(obj2, field2, component);
						sendKeysTextPane.checkText();
					};
				}.updateComponent();
			}

		};
		return creator;
	}

	protected String incapsulate(String newValue) {
		return // "'" +
		newValue; // .replaceAll("'", "\\\\'") + "'";
	}

	public boolean isRecording() {
		return recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	@Override
	public void mount() {
		Macro macro = getMacro();
		macro.setDirectCode(getDirectCode());

	}

	@Override
	public void unmount() {
		Macro macro = getMacro();
		macro.setDirectCode(null);
	}

}

package sgi.javaMacros.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import sgi.configuration.ConfigAtomTreeSet;
import sgi.generic.debug.Debug;
import sgi.gui.ConfigPanelCreator;
import sgi.gui.GUIMemory2;
import sgi.gui.RichFileIconer;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.JavaMacrosConfiguration;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.KeyNameProvider;
import sgi.javaMacros.model.StringUtils;
import sgi.javaMacros.model.enums.ActionType;
import sgi.javaMacros.model.enums.UseCaseMatchMode;
import sgi.javaMacros.model.enums.UseCaseType;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.internal.AutomaticUseCase;
import sgi.javaMacros.model.internal.AutomaticUseCaseOnTitle;
import sgi.javaMacros.model.internal.CompoundDevice;
import sgi.javaMacros.model.internal.IDevice;
import sgi.javaMacros.model.internal.Key;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.model.internal.ManualUseCase;
import sgi.javaMacros.model.internal.UseCase;
import sgi.javaMacros.model.internal.WindowClass;
import sgi.javaMacros.model.lists.ApplicationSet;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.LuaEventPanel.EventUser;
import sgi.javaMacros.ui.internal.AbstractButtonablePanel;
import sgi.javaMacros.ui.internal.FixedGridBagConstraints;
import sgi.localization.AbstractMsgs;
import sgi.os.WindowData;

public class MacroEditPanel extends AbstractButtonablePanel implements EventUser {

	static class DecamelizingListCellRender extends DefaultListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 986733914342221266L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			JComponent lrcr = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			String txt = StringUtils.decamelizeExe(getText(), true);
			setText(txt);
			setToolTipText(txt);
			return lrcr;
		}
	}

	private abstract class AbstractComboListener implements ItemListener, ActionListener {

		@Override
		final public void actionPerformed(ActionEvent e) {
			execute();
		}

		public void execute() {
			execute0();

			// SwingUtilities.invokeLater(new Runnable() {
			//
			// @Override
			// public void run() {
			// }
			// });
		}

		public abstract void execute0();

		@Override
		final public void itemStateChanged(ItemEvent e) {
			execute();
		}

	}

	class DeviceComboBoxListener extends AbstractComboListener {

		public void execute0() {
			CompoundDevice d = (CompoundDevice) deviceComboBox.getSelectedItem();
			if (d == null)
				return;

			fillKeysComboBox(d);
			Macro macro2 = getMacro();
			if (macro2 != null && d != null)
				macro2.set___device(d);
		}

	}

	class ApplicationComboBoxListener extends AbstractComboListener {

		public void execute0() {
			ApplicationForMacros a = (ApplicationForMacros) appLicationComboBox.getSelectedItem();
			if (a == null)
				return;

			fillUseCaseComboBox(a);
			Macro macro2 = getMacro();
			if (macro2 != null && a != null) {
				macro2._setApplication(a);
				setSelectedInBox(useCaseComboBox, macro2.getUseCaseCreationTime());
			}
		}

	}

	protected void fillUseCaseComboBox(ApplicationForMacros a) {
		if (a == null)
			return;

		UseCase k0 = (UseCase) useCaseComboBoxModel.getSelectedItem();

		useCaseComboBoxModel.removeAllElements();

		for (UseCase k1 : a.getUseCases()) {
			useCaseComboBoxModel.addElement(k1);
			if (k0 != null && k1.getCreationTime() == k0.getCreationTime())
				useCaseComboBoxModel.setSelectedItem(k1);
		}

		useCaseComboBoxModel.addElement(UseCase.ADD_CASE);
	}

	class UseCaseComboBoxListener extends AbstractComboListener {

		class NewUseCaseCreator implements Runnable {
			int __row = 0;

			private GridBagConstraints row() {
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = __row++;

				gridBagConstraints.fill = GridBagConstraints.NONE;
				gridBagConstraints.anchor = GridBagConstraints.WEST;
				return gridBagConstraints;
			}

			@Override
			public void run() {

				try {
					createNewuseCase();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					askingNewCase = false;
				}
			}

			public void createNewuseCase() {

				final UseCaseType[] choice = new UseCaseType[] { null };

				final ApplicationForMacros application = appLicationComboBox
						.getItemAt(appLicationComboBox.getSelectedIndex());
				if (application == null)
					return;

				final ModalConfigDialog dialog0 = new ModalConfigDialog(getOwner(), ModalityType.APPLICATION_MODAL);

				dialog0.setName("selectionofusecasetype"); //$NON-NLS-1$
				dialog0.setTitle(Messages.M._$(MacroEditPanel.class, "newUseCaseTypeDialog.title")); //$NON-NLS-1$
				dialog0.setIconImage(Messages.M.getIcon());

				final JPanel cp = new JPanel();
				dialog0.setContentPane(cp);
				cp.setLayout(new BorderLayout(6, 3));

				// cp.add(new JLabel("Chose the type of the usecase"), BorderLayout.NORTH);
				UseCaseType[] useCases = UseCaseType.values();

				GridBagLayout layout = new GridBagLayout();

				JPanel middle = new JPanel(layout);
				middle.add(new JPanel(), row());
				cp.add(middle, BorderLayout.CENTER);

				middle.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
						Messages.M.getString("sgi.javaMacros.model.enums.UseCaseType.available"))); //$NON-NLS-1$

				for (UseCaseType useCaseType : useCases) {
					addButton(dialog0, choice, middle, useCaseType);
				}

				middle.add(new JPanel(), row());
				layout.rowHeights = new int[row().gridy];

				for (int i = 0; i < layout.rowHeights.length; i++) {
					layout.rowHeights[i] = 20;
				}

				dialog0.pack();
				GUIMemory2.add(dialog0);
				dialog0.autoPosition();
				dialog0.showAndLock();
				UseCase uCase = null;

				UseCaseType useCaseType = choice[0];

				if (useCaseType == null) {
					return;
				}

				switch (useCaseType) {
				case MANUAL:
					uCase = new ManualUseCase("Manual Use Case"); //$NON-NLS-1$
					break;
				case WINDOWS_CLASS:
					AutomaticUseCase auto1 = new AutomaticUseCase(lastWindowClass);
					auto1.setRule(lastWindowClass);
					uCase = auto1;
					break;
				case WINDOWS_TITLE:
					AutomaticUseCaseOnTitle auto2 = new AutomaticUseCaseOnTitle(lastWindowTitle);
					auto2.setRule(lastWindowTitle);
					auto2.setMatchMode(UseCaseMatchMode.EQUAL_IGNORE_CASE);
					uCase = auto2;
					break;

				}

				if (uCase != null) {
					uCase.setEnabled(true);
					final ModalConfigDialog dialog1 = new ModalConfigDialog(getOwner(), ModalityType.APPLICATION_MODAL);

					ConfigPanelCreator creator = new ConfigPanelCreator(Messages.M,
							"sgi.javaMacros.ui.MacroEditPanel.newUseCase") { //$NON-NLS-1$
						protected javax.swing.JComponent createTextField(IAwareOfChanges obj, Field field)
								throws IllegalArgumentException, IllegalAccessException {

							if (obj instanceof AutomaticUseCase && !(obj instanceof AutomaticUseCaseOnTitle)
									&& Messages.M.getString("MacroEditPanel.5").equalsIgnoreCase(field.getName())) //$NON-NLS-1$

								return createWinClassesComboBox((AutomaticUseCase) obj);

							return super.createTextField(obj, field);
						}

						public JComponent createWinClassesComboBox(final AutomaticUseCase dev) {

							ConfigAtomTreeSet<WindowClass> windowClasses = application.getWindowClasses();
							String[] list = new String[windowClasses.size()];
							int i = 0;
							for (WindowClass windowClass : windowClasses) {
								list[i++] = windowClass.getName();
							}

							String oN = dev.getRule();

							final JComboBox<String> box = new JComboBox<>(list);
							if (oN != null) {
								box.setSelectedItem(oN);
							}
							box.addActionListener(

									new ActionListener() {

										@Override
										public void actionPerformed(ActionEvent e) {
											Object selectedItem = box.getSelectedItem();
											String rule = Messages.M.getString("MacroEditPanel.6") + selectedItem; //$NON-NLS-1$
											dev.setRule(rule);

										}
									}

							);

							return box;
						}

					};

					creator.setTargetWindow(dialog1);
					creator.FieldsSettings.setInvisibleFields("creationTime"); //$NON-NLS-1$

					dialog1.setContentPane(//
							creator.createConfigPanel(uCase));

					dialog1.setName("New usecase " + useCaseType.name());
					dialog1.setTitle(Messages.M
							._$("sgi.javaMacros.ui.MacroEditPanel.newUseCase." + useCaseType.name() + ".title")); //$NON-NLS-1$ //$NON-NLS-2$

					dialog1.pack();
					GUIMemory2.add(dialog0);
					dialog1.autoPosition();
					dialog1.showAndLock();
					if (dialog1.isSaved()) {
						application.getUseCases().add(uCase);
					}
				}
			}

			protected void addButton(final ModalConfigDialog dialog0, final UseCaseType[] choice, JPanel middle,
					final UseCaseType useCaseType) {
				JButton button;
				Messages M = Messages.M;

				String text = M._$(useCaseType, useCaseType.name());
				text = AbstractMsgs.htmlSwing("<u>" + text + "</u>"); //$NON-NLS-1$ //$NON-NLS-2$
				button = new JButton(text);

				middle.add(button, row());
				styleButton(button);
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						choice[0] = useCaseType;
						dialog0.setSaved(true);
						dialog0.setVisible(false);
					}
				});
			}

			protected void styleButton(JButton button) {
				button.setBackground(new Color(255, 255, 255, 0));
				button.setOpaque(false);
				button.setBorderPainted(false);
				button.setHorizontalTextPosition(SwingConstants.LEFT);
			}
		}

		private boolean askingNewCase = false;

		public void execute0() {

			final Macro macro2 = getMacro();
			UseCase selectedItem = (UseCase) useCaseComboBox.getSelectedItem();
			if (selectedItem == UseCase.ADD_CASE) {
				askNewCase();
			} else if (macro2 != null && selectedItem != null) {
				macro2._setUseCase(selectedItem);
			}

		}

		private void askNewCase() {

			if (askingNewCase)
				return;
			askingNewCase = true;

			new Thread(new NewUseCaseCreator()).start();

		}

	}

	class DeviceKeyComboBoxListener extends AbstractComboListener {
		private transient boolean askingNewKey = false;

		public void execute0() {

			Key selectedItem = (Key) deviceKeyComboBox.getSelectedItem();

			if (selectedItem == Key.ADD_KEY) {
				askNewKey();

			} else {

				Macro macro2c = getMacro();
				if (macro2c != null)
					macro2c.setKey(selectedItem);

			}
		}

		protected void askNewKey() {
			if (askingNewKey)
				return;
			askingNewKey = true;
			if (!MacroEditPanel.this.isDisplayable())
				return;

			new Thread(new Runnable() {

				@Override
				public void run() {
					@SuppressWarnings("deprecation")
					final Key key = new Key();
					key.addPropertyChangeListener(new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("scanCode".equals(evt.getPropertyName())) { //$NON-NLS-1$
								Integer scan = (Integer) evt.getNewValue();
								key.setName(KeyNameProvider.getKeyName(scan));
							}
						}
					});

					ModalConfigDialog dialog = new ModalConfigDialog(getOwner(), ModalityType.APPLICATION_MODAL);
					dialog.setTitle(Messages.M._$(MacroEditPanel.class, Messages.M.getString("MacroEditPanel.14"))); //$NON-NLS-1$
					dialog.setIconImage(Messages.M.getIcon());
					ConfigPanelCreator creator = new ConfigPanelCreator(Messages.M,
							Messages.M.getString("MacroEditPanel.15")) { //$NON-NLS-1$
						@Override
						protected JPanel createEndButtons() {

							JPanel endButtons = super.createEndButtons();
							JPanel encloser = new JPanel(new BorderLayout(5, 0));
							JTextArea area = new JTextArea();
							area.setBorder(BorderFactory.createLineBorder(Color.black));
							area.setBackground(Color.ORANGE);
							area.setMinimumSize(new Dimension(getOccupiedRowHeight(), getOccupiedRowHeight() - 2));
							area.addKeyListener(new KeyAdapter() {
								@Override
								public void keyReleased(KeyEvent e) {
									key.setScanCode(e.getKeyCode());
									((JTextArea) e.getSource()).setText(Messages.M.getString("MacroEditPanel.16")); //$NON-NLS-1$
								}
							});
							encloser.add(area, BorderLayout.NORTH);
							encloser.add(new JPanel(), BorderLayout.CENTER);

							encloser.add(endButtons, BorderLayout.SOUTH);
							return encloser;
						}
					};

					creator.setTargetWindow(dialog);

					dialog.setContentPane(creator.createConfigPanel(key));
					dialog.pack();

					Point locationOnScreen = deviceKeyComboBox.getLocationOnScreen();
					locationOnScreen.y += deviceKeyComboBox.getHeight();
					dialog.setLocation(locationOnScreen);
					dialog.showAndLock();
					if (dialog.isSaved()) {
						deviceKeysComboModel.removeElement(Key.ADD_KEY);
						deviceKeysComboModel.addElement(key);
						deviceKeysComboModel.addElement(Key.ADD_KEY);

						deviceKeysComboModel.setSelectedItem(key);
					}
					Macro macro2c = getMacro();
					if (macro2c != null)
						macro2c.setKey(key);

					askingNewKey = false;

				}
			}).start();
		}

	}

	private class NameFieldListener extends KeyAdapter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			readText(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// readText(e);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			readText(e);
		}

		public void readText(final AWTEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					JTextField source = (JTextField) e.getSource();
					String text = source.getText();
					setName(getMacro(), text);
				}
			});
			// setName(getOriginalMacro(),text);
		}

		public void setName(Macro macro2, String text) {
			if (macro2 != null)
				macro2.setName(text);
		}

	}

	private static final long serialVersionUID = -4707792221537349074L;

	private JComboBox<ApplicationForMacros> appLicationComboBox;
	protected DefaultComboBoxModel<ApplicationForMacros> applicationsComboBoxModel;

	private JComboBox<CompoundDevice> deviceComboBox;

	protected DefaultComboBoxModel<CompoundDevice> deviceComboBoxModel;
	protected DefaultComboBoxModel<UseCase> useCaseComboBoxModel = new DefaultComboBoxModel<UseCase>(new UseCase[0]);
	protected DefaultComboBoxModel<Key> deviceKeysComboModel = new DefaultComboBoxModel<>(new Key[] {});

	private ItemListener deviceComboListener;

	private JComboBox<Key> deviceKeyComboBox;

	private LuaEvent lastUsedEvent;

	public LuaEvent getLastUsedEvent() {
		return lastUsedEvent;
	}

	private Macro macro;

	// private Macro originalMacro;

	private JScrollPane payloadPortView;

	private PayLoadSelectorsGroup payLoadSelectorsGroup;

	private JTextField textFieldMacroName;
	private JComboBox<UseCase> useCaseComboBox;

	private static MacroEditPanel instance;

	public static MacroEditPanel getInstance() {
		return instance;
	}

	public static void setInstance(MacroEditPanel lastInstance) {
		MacroEditPanel.instance = lastInstance;
	}

	public MacroEditPanel() {
		super();

		instance = this;
		createEditorPanel();
	}

	public MacroEditPanel(Macro macro) {
		super();
		createEditorPanel();
		setMacro(macro);
		instance = this;
	}

	NameFieldListener nameFieldListener = new NameFieldListener();
	DeviceComboBoxListener dListener = new DeviceComboBoxListener();
	DeviceKeyComboBoxListener kListener = new DeviceKeyComboBoxListener();
	ApplicationComboBoxListener aListener = new ApplicationComboBoxListener();

	private String lastWindowClass;

	private String lastWindowTitle;

	private ModifierMasksGroup modifierMasksGroup;

	private PropertyChangeListener modifiersMaskGroupListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Macro m2 = getMacro();
			if (m2 != null) {
				m2.setModifiersMask((modifierMasksGroup.getModifierMask()));
			}
		}
	};

	private UseCaseComboBoxListener useCaseComboBoxListener = new UseCaseComboBoxListener();

	private class OSDReminderListener extends KeyAdapter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			this.execute();
		}

		private void execute() {
			Macro macro2 = getMacro();
			if (macro2 != null)
				macro2.setOSDReminder(_OSDReminderText.getText());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			this.execute();
		}

	}

	private void addListeners() {

		textFieldMacroName.addActionListener(nameFieldListener);
		textFieldMacroName.addKeyListener(nameFieldListener);

		deviceComboBox.addActionListener(dListener);
		// deviceComboBox.addItemListener(dListener);

		deviceKeyComboBox.addActionListener(kListener);
		// deviceKeyComboBox.addItemListener(kListener);

		appLicationComboBox.addActionListener(aListener);
		// appLicationComboBox.addItemListener(aListener);

		useCaseComboBox.addActionListener(useCaseComboBoxListener);
		// useCaseComboBox.addItemListener(useCaseComboBoxListener);

		modifierMasksGroup.addPropertyChangeListener(modifiersMaskGroupListener);

		useOSDCheckBoxs.addActionListener(_OSDCheckBoxListener);
		useDeferredOSDCheckBox.addActionListener(_deferredOSDCheckBoxListener);

		_OSDReminderText.addActionListener(_OSDRemindrListener);
		_OSDReminderText.addKeyListener(_OSDRemindrListener);

	}

	private void removeListeners() {

		textFieldMacroName.removeActionListener(nameFieldListener);
		textFieldMacroName.removeKeyListener(nameFieldListener);

		deviceComboBox.removeActionListener(dListener);
		// deviceComboBox.removeItemListener(dListener);

		deviceKeyComboBox.removeActionListener(kListener);
		// deviceKeyComboBox.removeItemListener(kListener);

		appLicationComboBox.removeActionListener(aListener);
		// appLicationComboBox.removeItemListener(aListener);

		useCaseComboBox.removeActionListener(useCaseComboBoxListener);
		// useCaseComboBox.removeItemListener(useCaseComboBoxListener);

		modifierMasksGroup.removePropertyChangeListener(modifiersMaskGroupListener);

		useOSDCheckBoxs.removeActionListener(_OSDCheckBoxListener);
		useDeferredOSDCheckBox.removeActionListener(_deferredOSDCheckBoxListener);

		_OSDReminderText.removeActionListener(_OSDRemindrListener);

	}

	public JRadioButton createActionTypeRadioButton(final ActionType actionType) {
		String key = "macros.action.types." + actionType.name(); //$NON-NLS-1$
		String key2 = key + ".tootip"; //$NON-NLS-1$

		final JRadioButton newRadio = new PayloadSelectorRadioButton(Messages.M._$(key), actionType);

		if (Messages.M.isDefined(key2)) {
			String _$ = Messages.M._$(key2);
			newRadio.setToolTipText(Messages.htmlSwing(_$));
		}

		newRadio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (newRadio.isSelected() && getMacro() != null) {

					getMacro().setActionType(actionType);
					try {

						payloadPortView.setViewportView(getMacro().getExecutor().getInputGUI());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					boolean aFlag = actionType != ActionType.VIRTUAL_MODIFIER;
					modifierMasksGroup.getPanel().setVisible(aFlag);
					aFlag = actionType != ActionType.CHANGE_USE_CASE;
					useCaseComboBox.setVisible(aFlag);

					switch (actionType) {
					case ADOBE_OPACITY:
					case KEY_ALIAS:
					case LUAMACROS_DIRECT_CODE:
						aFlag = false;
						break;
					case CHANGE_USE_CASE:
					case CLIPBOARD:
					case JAVAMACROS_TEXT:
					case REVERSE_CASE:
					case RUN_APPLICATION:
					case VIRTUAL_MODIFIER:
						aFlag = true;
						break;
					}

					for (JComponent jCo : oSDSettingsComponents) {
						jCo.setVisible(aFlag);
					}
				}
			}
		});

		return newRadio;
	}

	public void createEditorPanel() {
		initModels();

		this.setLayout(new BorderLayout(0, 0));

		JPanel editMacroPanel = new JPanel();
		editMacroPanel.setBorder(new TitledBorder(null, Messages.M.getString("MacroEditPanel.19"), TitledBorder.LEADING, //$NON-NLS-1$
				TitledBorder.TOP, null, null));
		this.add(editMacroPanel);
		GridBagLayout gridBagLayout_editMacroPanel = new GridBagLayout();
		gridBagLayout_editMacroPanel.columnWidths = new int[] { 600 };
		gridBagLayout_editMacroPanel.rowHeights = new int[] { 100, 5, 400, 3 };
		gridBagLayout_editMacroPanel.columnWeights = new double[] { 1.0 };
		gridBagLayout_editMacroPanel.rowWeights = new double[] { 0.0, 1.0, 1.0, 0.0 };
		editMacroPanel.setLayout(gridBagLayout_editMacroPanel);

		JPanel macroHeaderPanel = createHeader();

		GridBagConstraints gbc_macroHeaderPanel = new GridBagConstraints();
		gbc_macroHeaderPanel.gridheight = 2;
		gbc_macroHeaderPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_macroHeaderPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_macroHeaderPanel.insets = new Insets(0, 0, 5, 0);
		gbc_macroHeaderPanel.weighty = 5.0;
		gbc_macroHeaderPanel.gridx = 0;
		gbc_macroHeaderPanel.gridy = 0;

		editMacroPanel.add(macroHeaderPanel, gbc_macroHeaderPanel);
		JPanel editMacroActionPanel = new JPanel();
		editMacroActionPanel.setBorder(new TitledBorder(null, Messages.M.getString("MacroEditPanel.20"), //$NON-NLS-1$
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_editMacroActionPanel = new GridBagConstraints();
		gbc_editMacroActionPanel.weighty = 10.0;
		gbc_editMacroActionPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_editMacroActionPanel.fill = GridBagConstraints.BOTH;
		gbc_editMacroActionPanel.gridx = 0;
		gbc_editMacroActionPanel.gridy = 2;
		editMacroPanel.add(editMacroActionPanel, gbc_editMacroActionPanel);
		editMacroActionPanel.setLayout(new BorderLayout(5, 0));

		JPanel panelSelectActionTypeRadioButtons = new JPanel();
		editMacroActionPanel.add(panelSelectActionTypeRadioButtons, BorderLayout.WEST);
		GridBagLayout gbl_panelSelectActionTypeRadioButtons = new GridBagLayout();
		gbl_panelSelectActionTypeRadioButtons.columnWidths = new int[] { 50 };
		gbl_panelSelectActionTypeRadioButtons.columnWeights = new double[] { 1.0 };

		panelSelectActionTypeRadioButtons.setLayout(gbl_panelSelectActionTypeRadioButtons);

		ActionType[] actionTypes = ActionType.values();
		payLoadSelectorsGroup = new PayLoadSelectorsGroup();

		ArrayList<Integer> rows = new ArrayList<>();

		int row = 0;
		for (int i = 0; i < actionTypes.length; i++) {
			ActionType actionType = actionTypes[i];

			JRadioButton rdbtnNewRadioButton = createActionTypeRadioButton(actionType);
			rdbtnNewRadioButton.setSelected(getMacro() != null && getMacro().getActionType() == actionType);
			panelSelectActionTypeRadioButtons.add(rdbtnNewRadioButton, new FixedGridBagConstraints(0, row++));

			payLoadSelectorsGroup.add(rdbtnNewRadioButton);
			rows.add(22);
		}
		rows.add(0);

		int[] rowHeights = gbl_panelSelectActionTypeRadioButtons.rowHeights = new int[rows.size()];
		for (int i = 0; i < rowHeights.length; i++) {
			rowHeights[i] = rows.get(i);
		}

		double[] rowWeights = gbl_panelSelectActionTypeRadioButtons.rowWeights = //
				new double[rows.size()];
		rowWeights[rowWeights.length - 1] = 1;

		payloadPortView = new JScrollPane();
		executorPlaceHolder = new JPanel();
		executorPlaceHolder.setBackground(Color.white);
		payloadPortView.setViewportView(executorPlaceHolder);
		editMacroActionPanel.add(payloadPortView, BorderLayout.CENTER);

		addListeners();
	}

	private ArrayList<JComponent> oSDSettingsComponents = new ArrayList<>();

	protected JPanel createHeader() {
		JPanel macroHeaderPanel = new JPanel();
		int comboWidth = 130;

		GridBagLayout gbl_macroHeaderPanel = new GridBagLayout();
		gbl_macroHeaderPanel.columnWidths = new int[] { 51, 120, 120, 120, 120 };
		gbl_macroHeaderPanel.rowHeights = new int[] { 23, 3, 23, 23, 3, 23, 3 };
		gbl_macroHeaderPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0 };
		gbl_macroHeaderPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		macroHeaderPanel.setLayout(gbl_macroHeaderPanel);

		JLabel lblNewLabel_1 = new JLabel(Messages.M.getString("MacroEditPanel.21")); //$NON-NLS-1$
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		macroHeaderPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);

		textFieldMacroName = new JTextField();

		GridBagConstraints gbc_textFieldMacroName = new GridBagConstraints();
		gbc_textFieldMacroName.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldMacroName.anchor = GridBagConstraints.NORTHEAST;
		gbc_textFieldMacroName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMacroName.gridx = 1;
		gbc_textFieldMacroName.gridwidth = 4;
		gbc_textFieldMacroName.gridy = 0;
		macroHeaderPanel.add(textFieldMacroName, gbc_textFieldMacroName);

		int row = 1;

		GridBagConstraints gbc_Separator0 = new GridBagConstraints();
		gbc_Separator0.gridheight = 1;
		gbc_Separator0.anchor = GridBagConstraints.NORTHEAST;
		gbc_Separator0.fill = GridBagConstraints.HORIZONTAL;
		gbc_Separator0.gridx = 0;
		gbc_Separator0.gridwidth = 5;
		gbc_Separator0.gridy = row++;

		macroHeaderPanel.add(new JSeparator(), gbc_Separator0);

		JLabel lblNewLabel_2 = new JLabel(Messages.M.getString("MacroEditPanel.22")); //$NON-NLS-1$
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = row;
		macroHeaderPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);

		GridBagConstraints gbc_macroComboBoxDevice = new GridBagConstraints();
		gbc_macroComboBoxDevice.fill = GridBagConstraints.BOTH;
		gbc_macroComboBoxDevice.insets = new Insets(0, 0, 5, 5);
		gbc_macroComboBoxDevice.gridx = 1;
		gbc_macroComboBoxDevice.gridy = row;

		deviceComboBox = new JComboBox<>();
		deviceComboBox.addItemListener(deviceComboListener);
		deviceComboBox.setPreferredSize(new Dimension(comboWidth, 20));

		macroHeaderPanel.add(deviceComboBox, gbc_macroComboBoxDevice);
		deviceComboBox.setModel(deviceComboBoxModel);

		deviceKeyComboBox = new JComboBox<>(deviceKeysComboModel);
		GridBagConstraints gbc_keysComboBox = new GridBagConstraints();
		gbc_keysComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_keysComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_keysComboBox.gridx = 2;
		gbc_keysComboBox.gridy = row;
		macroHeaderPanel.add(deviceKeyComboBox, gbc_keysComboBox);

		deviceKeyComboBox.setRenderer(new DecamelizingListCellRender());

		appLicationComboBox = new JComboBox<ApplicationForMacros>();
		appLicationComboBox.setPreferredSize(new Dimension(comboWidth, 20));
		appLicationComboBox.setModel(applicationsComboBoxModel);
		appLicationComboBox.setRenderer(new DecamelizingListCellRender());

		GridBagConstraints gbc_macroApplicationComboBox = new GridBagConstraints();
		gbc_macroApplicationComboBox.fill = GridBagConstraints.BOTH;
		gbc_macroApplicationComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_macroApplicationComboBox.gridx = 3;
		gbc_macroApplicationComboBox.gridy = row;

		macroHeaderPanel.add(appLicationComboBox, gbc_macroApplicationComboBox);

		useCaseComboBox = new JComboBox<UseCase>();
		useCaseComboBox.setModel(useCaseComboBoxModel);

		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 4;
		gbc_comboBox.gridy = row;
		macroHeaderPanel.add(useCaseComboBox, gbc_comboBox);

		modifierMasksGroup = new ModifierMasksGroup();
		JPanel panel = modifierMasksGroup
				.getPanel(new JLabel(Messages.htmlSwing(Messages.M.getString("MacroEditPanel.23")))); //$NON-NLS-1$

		row++;

		GridBagConstraints gbc_modifiersPanel = new GridBagConstraints();
		gbc_modifiersPanel.gridheight = 1;
		gbc_modifiersPanel.anchor = GridBagConstraints.NORTHEAST;
		gbc_modifiersPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_modifiersPanel.gridx = 0;
		gbc_modifiersPanel.gridwidth = 5;
		gbc_modifiersPanel.gridy = row++;

		macroHeaderPanel.add(panel, gbc_modifiersPanel);

		GridBagConstraints gbc_Separator = new GridBagConstraints();
		gbc_Separator.gridheight = 1;
		gbc_Separator.anchor = GridBagConstraints.NORTHEAST;
		gbc_Separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_Separator.gridx = 0;
		gbc_Separator.gridwidth = 5;
		gbc_Separator.gridy = row++;

		macroHeaderPanel.add(new JSeparator(), gbc_Separator);

		GridBagConstraints gbc_OSDPanelLabel = new GridBagConstraints();
		gbc_OSDPanelLabel.gridheight = 1;
		gbc_OSDPanelLabel.anchor = GridBagConstraints.NORTHEAST;
		gbc_OSDPanelLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_OSDPanelLabel.gridx = 0;
		gbc_OSDPanelLabel.gridwidth = 1;
		gbc_OSDPanelLabel.gridy = row;
		gbc_OSDPanelLabel.insets = new Insets(0, 0, 5, 0);
		JLabel osdLabel = new JLabel(Messages.M.getString("MacroEditPanel.24"));
		macroHeaderPanel.add(osdLabel, gbc_OSDPanelLabel); // $NON-NLS-1$
		oSDSettingsComponents.add(osdLabel);

		GridBagConstraints gbc_OSDPanel = new GridBagConstraints();
		gbc_OSDPanel.gridheight = 1;
		gbc_OSDPanel.anchor = GridBagConstraints.NORTHEAST;
		gbc_OSDPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_OSDPanel.gridx = 1;
		gbc_OSDPanel.gridwidth = 4;
		gbc_OSDPanel.gridy = row++;
		gbc_OSDPanel.insets = new Insets(0, 0, 5, 0);

		JPanel osdSettingsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		oSDSettingsComponents.add(osdSettingsPanel);
		macroHeaderPanel.add(osdSettingsPanel, gbc_OSDPanel);

		JComponent[] subs = createIconFileChooser();

		useOSDCheckBoxs = new JCheckBox(Messages.M.getString("MacroEditPanel.25")); //$NON-NLS-1$
		useOSDCheckBoxs.setToolTipText(Messages.htmlSwing(Messages.M.getString("MacroEditPanel.26"))); //$NON-NLS-1$

		osdSettingsPanel.add(useOSDCheckBoxs);

		_OSDReminderText = new JTextField();
		_OSDReminderText.setColumns(25);
		osdSettingsPanel.add(_OSDReminderText);

		osdSettingsPanel.add(new JLabel(Messages.M.getString("MacroEditPanel.27"))); //$NON-NLS-1$

		for (JComponent jc : subs) {

			osdSettingsPanel.add(jc);
		}
		useDeferredOSDCheckBox = new JCheckBox(Messages.M.getString("MacroEditPanel.28")); //$NON-NLS-1$
		useDeferredOSDCheckBox.setToolTipText(Messages.htmlSwing(Messages.M.getString("MacroEditPanel.29"))); //$NON-NLS-1$

		osdSettingsPanel.add(useDeferredOSDCheckBox);

		GridBagConstraints gbc_Separator2 = new GridBagConstraints();
		gbc_Separator2.gridheight = 1;
		gbc_Separator2.anchor = GridBagConstraints.NORTHEAST;
		gbc_Separator2.fill = GridBagConstraints.HORIZONTAL;
		gbc_Separator2.gridx = 0;
		gbc_Separator2.gridwidth = 5;
		gbc_Separator2.gridy = row++;

		macroHeaderPanel.add(new JSeparator(), gbc_Separator2);

		return macroHeaderPanel;
	}

	protected void styleOrangeButtons(final JButton button) {
		button.setBorder(BorderFactory.createLineBorder(Color.black));
		button.setBackground(Color.orange);
		Dimension minS = new Dimension(30, 20);
		button.setMinimumSize(minS);
		button.setPreferredSize(minS);
	}

	String lastIconFilePath = "."; //$NON-NLS-1$

	private JLabel iconFilePathLabel;

	private JTextField _OSDReminderText;

	private JCheckBox useOSDCheckBoxs;
	private JCheckBox useDeferredOSDCheckBox;

	private OSDReminderListener _OSDRemindrListener = new OSDReminderListener();

	private ActionListener _OSDCheckBoxListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Macro macro2 = getMacro();
			if (macro2 != null)
				macro2.setUsingOSDReminder(useOSDCheckBoxs.isSelected());
		}
	};
	private ActionListener _deferredOSDCheckBoxListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Macro macro2 = getMacro();
			if (macro2 != null)
				macro2._setUseDeferredOSD(useDeferredOSDCheckBox.isSelected());
		}
	};

	private JPanel executorPlaceHolder;

	private Timer executionHolderWhiteWasher = new Timer(3000, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			executorPlaceHolder.setBackground(Color.WHITE);

		}
	});

	protected JComponent[] createIconFileChooser() {
		final JButton button = new JButton("  ...  "); //$NON-NLS-1$
		styleOrangeButtons(button);

		iconFilePathLabel = new JLabel(".");//$NON-NLS-1$
		iconFilePathLabel.setFont(getFont().deriveFont(14f));

		MouseAdapter ml = new MouseAdapter() {
			boolean in;
			private JWindow preview;

			@Override
			public void mouseEntered(MouseEvent e) {
				in = true;
				Timer timer = new Timer(2000, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (!in)
							return;
						if (preview == null && getMacro().getIconFile() != null && getMacro().getIconFile().isFile()) {

							preview = new JWindow(getOwner());
							ImageIcon icon = getMacro().getIcon();
							Image image = icon.getImage();
							Dimension preferredSize = new Dimension(image.getWidth(null), image.getHeight(null));
							JLabel contentPane = new JLabel(icon);
							preview.setContentPane(contentPane);
							preview.setPreferredSize(preferredSize);
							contentPane.setPreferredSize(preferredSize);
							preview.pack();
							Point location = button.getLocationOnScreen();
							location.x = location.x + button.getWidth() - preview.getWidth();
							location.y += button.getHeight();
							preview.setLocation(location);
							preview.setVisible(true);
						}
					}
				});
				timer.setRepeats(false);
				timer.start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				in = false;
				if (preview != null) {
					preview.setVisible(false);
					preview.dispose();
					preview = null;
				}

			}
		};
		button.addMouseListener(ml);
		button.addMouseMotionListener(ml);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (getMacro() == null)
						return;
					File oldValue = getMacro().getIconFile();
					if (oldValue != null)
						lastIconFilePath = oldValue.getPath();

					File file = new File(lastIconFilePath);

					JFileChooser chooser = new JFileChooser(file.getParentFile());
					chooser.setFileView(new RichFileIconer(0));
					chooser.setCurrentDirectory(file.getParentFile());
					chooser.setSelectedFile(file);
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					FileFilter iconFileFilter = new FileFilter() {

						@Override
						public String getDescription() {
							return Messages.M._$(MacroEditPanel.class.getName() + ".IconFile.fileFilterDescription"); //$NON-NLS-1$
						}

						@Override
						public boolean accept(File f) {
							if (f.isDirectory())
								return true;

							return f.isFile() && (//
							f.getName().toLowerCase().endsWith(".png")// //$NON-NLS-1$
									|| f.getName().toLowerCase().endsWith(".gif")// //$NON-NLS-1$
									|| f.getName().toLowerCase().endsWith(".jpg")// //$NON-NLS-1$
									|| f.getName().toLowerCase().endsWith(".jpeg")// //$NON-NLS-1$

							);
						}
					};
					chooser.setFileFilter(iconFileFilter);

					int v = chooser.showOpenDialog((Component) e.getSource());
					File selectedFile = chooser.getSelectedFile();

					switch (v) {
					case JFileChooser.APPROVE_OPTION:
						try {

							if (selectedFile != null && iconFileFilter.accept(selectedFile)) {
								int iconSize = JavaMacrosConfiguration.instance().get_osdSettings().getIconSize();
								File ffile = internalize(selectedFile, iconSize);
								getMacro().setIconFile(ffile);
								if (!selectedFile.equals(ffile)) {
									getMacro().setSmallIconFile(internalize(selectedFile, 16));

								}
								lastIconFilePath = ffile.getPath();
								setIconFilePathLabelText(ffile);

							}
						} catch (Exception e2) {
							Debug.info(e2, 4);
						}
						break;
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});

		JComponent[] p2 = new JComponent[2];
		p2[0] = iconFilePathLabel;
		p2[1] = button;
		return p2;
	}

	protected File internalize(File f, int iconSize) {

		try {
			JavaMacrosConfiguration jmc = JavaMacrosConfiguration.instance();

			Image image = new ImageIcon(f.getPath()).getImage();
			BufferedImage capture2 = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = capture2.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(image, 0, 0, iconSize, iconSize, 0, 0, image.getWidth(null), image.getHeight(null), null);
			g.dispose();

			File saveFile = jmc.getSaveFile().getParentFile();
			saveFile = new File(saveFile, "icons"); //$NON-NLS-1$
			if (!saveFile.isDirectory())
				saveFile.mkdirs();

			File[] lfs = saveFile.listFiles(new java.io.FileFilter() {

				@Override
				public boolean accept(File f) {
					return f.isFile();
				}
			});

			int index = 1;

			if (lfs != null)
				index = lfs.length + 1;
			String trail = String.valueOf(index);
			while (trail.length() < 5)
				trail = "0" + trail; //$NON-NLS-1$

			saveFile = new File(saveFile, "icon_" + trail + "[" + iconSize + "px].png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			FileOutputStream outFile = new FileOutputStream(saveFile);
			ImageIO.write(capture2, "png", outFile); //$NON-NLS-1$
			outFile.close();

			return saveFile;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return f;
	}

	protected Macro getMacro() {
		// if (macro == null) {
		// setMacro(new Macro(true));
		// }
		return macro;
	}

	public Window getOwner() {
		Container parent = getParent();
		while (!(parent == null || parent instanceof Window))
			parent = parent.getParent();

		return (Window) parent;

	}

	public PayLoadSelectorsGroup getPayLoadSelectorsGroup() {
		return payLoadSelectorsGroup;
	}

	public void initModels() {
		final JavaMacrosMemory memory = JavaMacrosMemory.instance();

		ApplicationSet applicationSet = memory.getApplicationSet();
		applicationSet.loadAppsFromSystem();
		applicationsComboBoxModel = new DefaultComboBoxModel<>(applicationSet.asArray(null, false));
		deviceComboBoxModel = new DefaultComboBoxModel<>(memory.getDeviceSet().getDevicesByName().asArray());

		memory.getApplications().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().startsWith("transient")) //$NON-NLS-1$
					return;
				Object selected = applicationsComboBoxModel.getSelectedItem();
				applicationsComboBoxModel.removeAllElements();
				ApplicationForMacros[] applicationSet2 = memory.getApplicationSet().asArray(null, false);

				boolean reSet = false;
				for (ApplicationForMacros app : applicationSet2) {
					applicationsComboBoxModel.addElement(app);
					reSet |= app.equals(selected);
				}
				if (reSet)
					applicationsComboBoxModel.setSelectedItem(selected);
			}
		});

		memory.getDevices().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().startsWith("transient")) //$NON-NLS-1$
					return;

				Object selected = deviceComboBoxModel.getSelectedItem();
				deviceComboBoxModel.removeAllElements();
				CompoundDevice[] deviceSet = memory.getDeviceSet().getDevicesByName().asArray(); // ).asArray()
				boolean reSet = false;
				for (CompoundDevice device : deviceSet) {
					deviceComboBoxModel.addElement(device);
					reSet |= device.equals(selected);
				}
				if (reSet)
					deviceComboBoxModel.setSelectedItem(selected);

			}
		});

	}

	public void setActionInputUI(Component actionUI) {
		payloadPortView.add(actionUI, BorderLayout.CENTER);
	}

	@SuppressWarnings("rawtypes")
	public void setSelectedInBox(JComboBox cb, Object obj) {
		if (obj == null && cb.getItemCount() > 0)
			cb.setSelectedIndex(0);
		for (int i = 0, end = cb.getItemCount(); i < end; i++) {
			Object itemAt = cb.getItemAt(i);
			if (obj == itemAt) {
				cb.setSelectedIndex(i);
				return;
			}

			String valueOf = String.valueOf(obj);

			if (itemAt instanceof Key) {
				Key k = (Key) itemAt;
				if (String.valueOf(k.getScanCode()).equals(valueOf)) {
					cb.setSelectedIndex(i);
					return;
				}

			} else

			if (itemAt instanceof IDevice) {
				CompoundDevice k = (CompoundDevice) itemAt;
				if (String.valueOf(k.getName()).equals(valueOf)) {
					cb.setSelectedIndex(i);
					return;
				}

			} else

			if (itemAt instanceof UseCase) {
				UseCase k = (UseCase) itemAt;
				if (String.valueOf(k.getCreationTime()).equals(valueOf)) {
					cb.setSelectedIndex(i);
					return;
				}

			} else

			if (itemAt instanceof ApplicationForMacros) {
				ApplicationForMacros k = (ApplicationForMacros) itemAt;

				if (String.valueOf(k.getExeFile()).equals(valueOf)) {
					cb.setSelectedIndex(i);
					return;
				}
			}

		}

		try {
			if (cb.getItemCount() > 0)
				cb.setSelectedIndex(0);
		} catch (Exception e) {

			Debug.info(e, 3);
		}
	}

	public void setMacro(Macro macro) {
		// this.originalMacro = macro;
		if (macro == this.macro)
			return;

		removeListeners();

		Macro oldMacro = this.macro;
		this.macro = macro;// .workingCopy();
		if (macro == null) {
			cleanUpEditor();
			firePropertyChange("macro", oldMacro, macro); //$NON-NLS-1$
			return;
		}

		macro.preserveOriginal();
		setIconFilePathLabelText(macro.getIconFile());

		textFieldMacroName.setText(macro.getName());

		final int scanCode = macro.getScanCode();
		final long useCaseCreationTime = macro.getUseCaseCreationTime();

		setSelectedInBox(deviceComboBox, macro.getDeviceName());
		setSelectedInBox(appLicationComboBox, macro.getExeFile());

		fillKeysComboBox(macro.getDevice());
		fillUseCaseComboBox(macro.getApplication());

		setSelectedInBox(deviceKeyComboBox, scanCode);
		setSelectedInBox(useCaseComboBox, useCaseCreationTime);

		modifierMasksGroup.setModifierMask(macro.getModifiersMask());

		addListeners();
		payLoadSelectorsGroup
				.setSelected(macro.getActionType() == null ? ActionType.values()[0] : macro.getActionType())
				.doClick(10);

		useOSDCheckBoxs.setSelected(macro.isUsingOSDReminder());
		useDeferredOSDCheckBox.setSelected(macro.usesDeferredOSD());

		_OSDReminderText.setText(macro.getOSDReminder());

		firePropertyChange("macro", oldMacro, macro); //$NON-NLS-1$
	}

	public void cleanUpEditor() {
		payloadPortView.getViewport().setView(executorPlaceHolder);
		executorPlaceHolder.setBackground(Color.ORANGE);
		executorPlaceHolder.repaint();
		executionHolderWhiteWasher.restart();
		_OSDReminderText.setText(""); //$NON-NLS-1$
		textFieldMacroName.setText(""); //$NON-NLS-1$
		useOSDCheckBoxs.setSelected(false);
		useDeferredOSDCheckBox.setSelected(false);
		iconFilePathLabel.setText(""); //$NON-NLS-1$
		modifierMasksGroup.setModifierMask(0);
	}

	@Override
	public void useEvent(LuaEvent luaEvent) {

		setSelectedInBox(deviceComboBox, luaEvent.getDeviceName());
		setSelectedInBox(deviceKeyComboBox, luaEvent.getScanCode());
		WindowData activeWindow = luaEvent.getActiveWindow();
		this.lastWindowClass = activeWindow.getWindowClasses().get(0);
		this.lastWindowTitle = activeWindow.getWindowTitle();
		UseCase desiredUseCase = luaEvent.getDesiredUseCase();
		modifierMasksGroup
				.setModifierMask(luaEvent.getSystemModifiersMask() | luaEvent.getApplicationModifiersMaskPreset());

		if (desiredUseCase != null) {

			setSelectedInBox(appLicationComboBox, desiredUseCase.getApplication());
			setSelectedInBox(useCaseComboBox, desiredUseCase);

		} else
			setSelectedInBox(appLicationComboBox, activeWindow.getExeFile());

	}

	protected void fillKeysComboBox(CompoundDevice d) {
		if (d == null)
			return;
		Key k0 = (Key) deviceKeysComboModel.getSelectedItem();
		deviceKeysComboModel.removeAllElements();
		int i = 0, s = -1;
		for (Key k1 : d.getKeys()) {
			deviceKeysComboModel.addElement(k1);
			if (k0 != null && k1.getScanCode() == k0.getScanCode())
				s = i;
			i++;
		}
		deviceKeysComboModel.addElement(Key.ADD_KEY);

		if (s > -1)
			deviceKeyComboBox.setSelectedIndex(s);
	}

	public void setIconFilePathLabelText(File selectedFile) {

		iconFilePathLabel.setText(selectedFile == null ? "." : selectedFile.getName()); //$NON-NLS-1$
	}

}

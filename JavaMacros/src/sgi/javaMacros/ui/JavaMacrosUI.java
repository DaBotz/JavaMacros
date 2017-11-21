package sgi.javaMacros.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import sgi.generic.ui.GUIMemory;
import sgi.javaMacros.ui.internal.FixedGridBagConstraints;
import sgi.javaMacros.ui.internal.HorizontalGridBagConstraints;
import sgi.javaMacros.ui.internal.SettingsPanelGridbagLayout;
import sgi.javaMacros.ui.viewmodels.ApplicationTableModel;
import sgi.javaMacros.ui.viewmodels.DeviceTableModel;
import sgi.javaMacros.ui.viewmodels.EventsTableModel;
import sgi.javaMacros.ui.viewmodels.MacrosTableModel;

public class JavaMacrosUI extends JFrame {

	public class ZButton extends JButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4208847141086637736L;

		public ZButton(String text, ActionCommands actionCommand) {
			super(text);
			configureButtonActionCommand(this, actionCommand);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4048382608357613710L;
	protected ActionListener actionsImplementer = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			String act = e.getActionCommand();
			String subCommand = null;
			int iOf = act.indexOf("+");
			if (iOf > 0) {
				subCommand = act.substring(iOf + 1);
				act = act.substring(0, iOf);
			}

			ActionCommands com = ActionCommands.valueOf(act);

			switch (com) {
			case ADD_APPLICATION:
				break;
			case CREATE_NEW_MACRO:
				break;
			case ERASE_MACRO:
				break;
			case EXIT_APPLICATION:
				break;
			case FIND_MACRO:
				break;
			case FIX_ALIASES:
				break;
			case LOAD_VARIANT:
				break;
			case LOOKFOR_BACKUP_DIRECTORY:
				break;
			case PAYLOAD:
				break;
			case SAVE_CONFIG:
				break;
			case SAVE_VARIANT:
				break;
			case SCAN_FOR_NEW_EVENT:
				break;
			case SCAN_FOR_NEW_EVENT_IN_MACRO:
				break;
			case SELECT_MODIFIER:
				subCommand = subCommand + "";

				break;
			case SET_INTERFACE_LANGUAGE:
				break;
		

			}

		}
	};
	protected ListSelectionListener applicationsSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub

		}
	};
	private JTable applicationsTable;
	private JTextField bufferResetIntervalTextField;

	private JTextField configBackupDirectoryTextField;
	private JPanel contentPane;
	private DeviceTableModel devicesDataModel;

	protected ListSelectionListener devicesSelectionListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
		
		}
	};
	private JTable devicesTable;
	private EventsTableModel eventsDataModel;
	protected ListSelectionListener eventsSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub

		}
	};
	private JTable eventsTable;
	private JTable macrosTable;

	private MacrosTableModel macrosTableModel;
	protected ListSelectionListener macrosTableSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub

		}
	};

	private JTabbedPane mainTabbedPanel;
	private JTextField numberOfBackupFilesTextField;
	private JTextField scriptProcedureBeginTextField;

	private JTextField testTextFieldDevice;
	private JTextField testTextFieldEvent;
	private JTextField textFieldProcedureEnd;

	private ApplicationTableModel windowsTableModel;

	// private void configurex(ArrayList<AbstractButton> buttons) {
	// Iterator<AbstractButton> it = buttons.iterator();
	// while(it.hasNext()){
	// configurex(it.next());
	// }
	// }

	/**
	 * Create the frame.
	 */
	public JavaMacrosUI() {

		setTitle("HID Macros (java)");
		setIconImage(Toolkit.getDefaultToolkit().getImage(JavaMacrosUI.class.getResource("icon_16x16.gif")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 798, 578);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);

		JMenuItem mntmNewMenuItem_Save = createMenuItem("Save", ActionCommands.SAVE_CONFIG);
		mntmNewMenuItem_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmNewMenuItem_Save);

		JMenuItem mntmNewMenuItem_SaveVariant = createMenuItem("Save as Variant...", ActionCommands.SAVE_VARIANT);
		mntmNewMenuItem_SaveVariant.setToolTipText(
				"<html>\r\n<div>Save the current macros set as a \"variant\"</div>\r\n<div>Variant sets may be useful when one alterns between different<br />  but recurring workflows.<br /></div>\r\n<div>For example, to use shortcuts on a keyboard that is usually left in \"natural state\".<br /></div>\r\n<div>Variants may be assigned a duration, so that JavaMacros will return to use the standard set of macros after a given time (for example, eight hours/ a workday).<br /></div>\r\n</html>");
		mnFile.add(mntmNewMenuItem_SaveVariant);

		JMenuItem mntmNewMenuItem_LoadVariant = createMenuItem("Load Variant Set...", ActionCommands.LOAD_VARIANT);
		mntmNewMenuItem_LoadVariant.setToolTipText(
				"<html>\r\n<div>Load an existing variant set of macros.<br /><br /></div>\r\n<div>Variant sets may be useful when one alterns between different<br />  but recurring workflows.<br /></div>\r\n<div>For example, to use shortcuts on a keyboard that is usually left in \"natural state\".<br /></div>\r\n<div>Variants may be assigned a duration, so that JavaMacros will return to use the standard set of macros after a given time (for example, eight hours/ a workday).<br /></div>\r\n</html>");
		mnFile.add(mntmNewMenuItem_LoadVariant);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmNewMenuItem_1 = createMenuItem("Exit", ActionCommands.EXIT_APPLICATION);
		mntmNewMenuItem_1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		mnFile.add(mntmNewMenuItem_1);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel statusPanel = new JPanel();
		contentPane.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JLabel statusOutput = new JLabel("Status output");
		statusPanel.add(statusOutput);

		mainTabbedPanel = new JTabbedPane(JTabbedPane.TOP) ;
				
//				;{
//			/**
//				 * 
//				 */
//			private static final long serialVersionUID = -5394200068258872823L;
//
//			@Override
//			public void insertTab(String title, Icon icon, Component component, String tip, int index) {
//
//				super.insertTab(title, icon, new JScrollPane(component), tip, index);
//			}
//
//		};

		contentPane.add(mainTabbedPanel, BorderLayout.CENTER);

		DevicesPanel devicesPanel = new DevicesPanel(new DeviceTableModel());
		devicesTable = devicesPanel.getTable();
		devicesTable.getSelectionModel().addListSelectionListener(devicesSelectionListener);
		// configure (devicesPanel.getButtons());

		mainTabbedPanel.addTab("Devices", null, devicesPanel,
				"All the devices appearing in the system that have associated macros");

		ApplicationsPanel applicationsPanel = new ApplicationsPanel(new ApplicationTableModel());
		applicationsTable = applicationsPanel.getTable();
		applicationsTable.getSelectionModel().addListSelectionListener(applicationsSelectionListener);
		// configure (applicationsPanel.getButtons());

		mainTabbedPanel.addTab("Applications", null, applicationsPanel, null);

		EventsPanel eventsPanel = new EventsPanel(new EventsTableModel());
		eventsTable = eventsPanel.getTable();
		eventsTable.getSelectionModel().addListSelectionListener(eventsSelectionListener);
		// configure (eventsPanel.getButtons());

		mainTabbedPanel.addTab("Events", null, eventsPanel, null);

		JSplitPane macrosMainPanel = new JSplitPane();
		macrosMainPanel.setResizeWeight(0.4);
		macrosMainPanel.setContinuousLayout(true);
		mainTabbedPanel.addTab("Macros", null, macrosMainPanel, null);
		macrosMainPanel.setLayout(new BorderLayout(0, 0));

		JPanel macrosTableAndButtonsPanel = new JPanel();
		macrosTableAndButtonsPanel.setPreferredSize(new Dimension(250, 10));

		macrosMainPanel.add(macrosTableAndButtonsPanel, BorderLayout.WEST);
		macrosTableAndButtonsPanel.setLayout(new BorderLayout(0, 0));

		JPanel macrosButtonsPanel = new JPanel();
		macrosTableAndButtonsPanel.add(macrosButtonsPanel, BorderLayout.NORTH);
		macrosButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

		JButton btnNewButton = new ZButton("New", ActionCommands.CREATE_NEW_MACRO);
		nullSpace(btnNewButton);
		macrosButtonsPanel.add(btnNewButton);

		JButton btnNewButton2 = new ZButton("Erase", ActionCommands.ERASE_MACRO);
		nullSpace(btnNewButton2);
		macrosButtonsPanel.add(btnNewButton2);

		JButton btnFindMacro = new ZButton("Find", ActionCommands.FIND_MACRO);
		nullSpace(btnFindMacro);
		macrosButtonsPanel.add(btnFindMacro);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBorder(new LineBorder(new Color(127, 157, 185)));
		macrosTableAndButtonsPanel.add(scrollPane_1);
		// scrollPane_1.setMaximumSize(new Dimension(300, 300));

		macrosTable = new JTable();
		macrosTable.setModel(new MacrosTableModel());
		macrosTable.createDefaultColumnsFromModel();
		TableColumnModel cmn = macrosTable.getColumnModel();
		for (int i = 0; i < cmn.getColumnCount(); i++) {
			cmn.getColumn(i).setMaxWidth(90);
			cmn.getColumn(i).setResizable(true);
		}
		TableColumn eventColumn = cmn.getColumn(2);
		eventColumn.setMaxWidth(70);

		addTableSorter(macrosTable);

		macrosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		macrosTable.getSelectionModel().addListSelectionListener(macrosTableSelectionListener);

		scrollPane_1.setViewportView(macrosTable);

		EditMacroPanel generalSingleMacroPanel = new EditMacroPanel();
		macrosMainPanel.add(generalSingleMacroPanel, BorderLayout.CENTER);

		JPanel TriggerIncerceptPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) TriggerIncerceptPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		TriggerIncerceptPanel.setBorder(
				new TitledBorder(null, "Test - Last input event", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		TriggerIncerceptPanel.setName("Test Area");
		JLabel lblNewLabel = new JLabel("Device");
		TriggerIncerceptPanel.add(lblNewLabel);

		testTextFieldDevice = new JTextField();
		testTextFieldDevice.setName("Testd_Device_field");
		TriggerIncerceptPanel.add(testTextFieldDevice);
		testTextFieldDevice.setColumns(10);

		JLabel labeTestlEvent = new JLabel("Event");
		TriggerIncerceptPanel.add(labeTestlEvent);

		testTextFieldEvent = new JTextField();
		TriggerIncerceptPanel.add(testTextFieldEvent);
		testTextFieldEvent.setColumns(10);

		generalSingleMacroPanel.add(TriggerIncerceptPanel, BorderLayout.NORTH);

		JPanel mainSettingsPanel = new JPanel();
		mainTabbedPanel.addTab("Settings", null, mainSettingsPanel, null);
		mainSettingsPanel.setLayout(new BorderLayout());

		JPanel generalettingsPanel = new JPanel();
		generalettingsPanel.setBorder(
				new TitledBorder(null, "General settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		mainSettingsPanel.add(generalettingsPanel, BorderLayout.NORTH);

		GridBagLayout gbl_settingsPanel = new SettingsPanelGridbagLayout(8);

		generalettingsPanel.setLayout(gbl_settingsPanel);

		JLabel lblNewLabel_3 = new JLabel("Language: ");
		lblNewLabel_3.setVerticalAlignment(SwingConstants.BOTTOM);
		generalettingsPanel.add(lblNewLabel_3, new FixedGridBagConstraints(1, 1));

		JComboBox<String> interfaceLanguageBox = new JComboBox<String>();
		interfaceLanguageBox.setModel(new DefaultComboBoxModel<>(new String[] { "Default", "Italiano" }));

		interfaceLanguageBox.setActionCommand(ActionCommands.SET_INTERFACE_LANGUAGE.name());

		interfaceLanguageBox.setPreferredSize(new Dimension(28, 18));

		generalettingsPanel.add(interfaceLanguageBox, new HorizontalGridBagConstraints(2, 1));

		JCheckBox chckbxMinimizeToTray = new JCheckBox("Minimize to tray");
		chckbxMinimizeToTray.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxMinimizeToTray.setSelected(true);

		generalettingsPanel.add(chckbxMinimizeToTray, new FixedGridBagConstraints(2, 2));

		JCheckBox chckbxStartMinimized = new JCheckBox("Start minimized");
		chckbxStartMinimized.setSelected(true);

		generalettingsPanel.add(chckbxStartMinimized, new FixedGridBagConstraints(2, 3));

		JCheckBox chckbxShowBufferContent = new JCheckBox("Show buffer content");
		chckbxShowBufferContent.setSelected(true);

		generalettingsPanel.add(chckbxShowBufferContent, new FixedGridBagConstraints(2, 4));

		generalettingsPanel.add(new JLabel("Buffer Reset (ms)"), new FixedGridBagConstraints(1, 5));

		bufferResetIntervalTextField = new JTextField();
		bufferResetIntervalTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		bufferResetIntervalTextField.setText("2000");

		InputMethodListener onlyNumbers = new InputMethodListener() {

			private String lastValidValue;

			public void caretPositionChanged(InputMethodEvent event) {
			}

			public void inputMethodTextChanged(InputMethodEvent event) {
				JTextField tf = (JTextField) event.getSource();
				int ok = -1;
				String text = tf.getText();

				try {
					ok = Integer.parseInt(text);
				} catch (NumberFormatException e) {
				}
				if (ok > 1) {
					lastValidValue = text;
				} else {
					if (lastValidValue == null)
						lastValidValue = "2000";
					tf.setText(lastValidValue);
				}

			}
		};
		bufferResetIntervalTextField.addInputMethodListener(onlyNumbers);

		bufferResetIntervalTextField.setColumns(4);

		generalettingsPanel.add(bufferResetIntervalTextField, new FixedGridBagConstraints(2, 5));

		generalettingsPanel.add(new JLabel("Backups: keep"), new FixedGridBagConstraints(1, 6));

		JPanel backupsMiddle = new JPanel();

		FlowLayout fl_backupsMiddle = new FlowLayout();
		fl_backupsMiddle.setHgap(0);
		fl_backupsMiddle.setVgap(0);
		fl_backupsMiddle.setAlignment(FlowLayout.LEFT);
		fl_backupsMiddle.setAlignOnBaseline(true);
		backupsMiddle.setLayout(fl_backupsMiddle);

		generalettingsPanel.add(backupsMiddle, new HorizontalGridBagConstraints(2, 6));

		numberOfBackupFilesTextField = new JTextField();
		numberOfBackupFilesTextField.setText("2");
		numberOfBackupFilesTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		numberOfBackupFilesTextField.setMinimumSize(new Dimension(6, 18));
		numberOfBackupFilesTextField.setPreferredSize(new Dimension(50, 18));
		numberOfBackupFilesTextField.setColumns(3);
		numberOfBackupFilesTextField.addInputMethodListener(onlyNumbers);

		backupsMiddle.add(numberOfBackupFilesTextField);

		JLabel lblNewLabel_6 = new JLabel(" ");
		backupsMiddle.add(lblNewLabel_6);

		JLabel lblNewLabel_7 = new JLabel("configuration sets in directory");
		backupsMiddle.add(lblNewLabel_7);

		JLabel lblNewLabel_8 = new JLabel(" ");
		backupsMiddle.add(lblNewLabel_8);

		configBackupDirectoryTextField = new JTextField();
		backupsMiddle.add(configBackupDirectoryTextField);
		configBackupDirectoryTextField.setColumns(10);

		JLabel lblNewLabel_9 = new JLabel(" ");
		backupsMiddle.add(lblNewLabel_9);

		JButton btnNewButton_2 = new ZButton("...", ActionCommands.LOOKFOR_BACKUP_DIRECTORY);
		btnNewButton_2.setBorder(null);
		backupsMiddle.add(btnNewButton_2);

		JPanel scriptSettingPanel = new JPanel();
		scriptSettingPanel.setBorder(BorderFactory.createTitledBorder("Script settings"));
		mainSettingsPanel.add(scriptSettingPanel, BorderLayout.CENTER);

		scriptSettingPanel.setLayout(new SettingsPanelGridbagLayout(4));

		JLabel lblNewLabel_10 = new JLabel("Language");

		FixedGridBagConstraints.setOffsetY(1);

		scriptSettingPanel.add(lblNewLabel_10, new FixedGridBagConstraints(1, 0));

		JComboBox<String> selectlanguageComboBox = new JComboBox<>();
		selectlanguageComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "JScript", "VbScript" }));

		scriptSettingPanel.add(selectlanguageComboBox, new HorizontalGridBagConstraints(2, 0));

		JLabel lblNewLabel_11 = new JLabel("Procedure begins");
		GridBagConstraints gbc_lblNewLabel_11 = new FixedGridBagConstraints(1, 1);
		scriptSettingPanel.add(lblNewLabel_11, gbc_lblNewLabel_11);

		scriptProcedureBeginTextField = new JTextField();
		scriptSettingPanel.add(scriptProcedureBeginTextField, new HorizontalGridBagConstraints(2, 1));
		scriptProcedureBeginTextField.setColumns(10);

		JLabel lblNewLabel_12 = new JLabel("Procedure end");
		scriptSettingPanel.add(lblNewLabel_12, new FixedGridBagConstraints(1, 2));

		textFieldProcedureEnd = new JTextField();
		scriptSettingPanel.add(textFieldProcedureEnd, new HorizontalGridBagConstraints(2, 2));
		textFieldProcedureEnd.setColumns(10);

		new ButtonConfigurer(actionsImplementer).configure(this);
		
		
		GUIMemory.add(this); 
		
		
		
		
		
		
	}

	public void addTableSorter(JTable devicesTable2) {
		devicesTable2.setRowSorter(new TableRowSorter<>(devicesTable2.getModel()));
	}

	public void configureButtonActionCommand(AbstractButton btnNewButton, ActionCommands action) {
		if (action != null) {
			btnNewButton.setActionCommand(action.name());
		}
	}

	public JMenuItem createMenuItem(String text, ActionCommands action) {
		JMenuItem mntmNewMenuItem_Save = new JMenuItem(text);
		mntmNewMenuItem_Save.setHorizontalTextPosition(SwingConstants.LEFT);
		mntmNewMenuItem_Save.setVerticalTextPosition(SwingConstants.TOP);
		configureButtonActionCommand(mntmNewMenuItem_Save, action);
		return mntmNewMenuItem_Save;
	}

	public DeviceTableModel getDevicesDataModel() {

		if (devicesDataModel == null)
			devicesDataModel = new DeviceTableModel();
		return devicesDataModel;
	}

	public EventsTableModel getEventsDataModel() {
		if (eventsDataModel == null)
			eventsDataModel = new EventsTableModel();
		return eventsDataModel;
	}

	public MacrosTableModel getMacrosTableModel() {
		if (macrosTableModel == null)
			macrosTableModel = new MacrosTableModel();
		return macrosTableModel;
	}

	public JTabbedPane getMainTabbedPanel() {
		return mainTabbedPanel;
	}

	public TableModel getWindowsTableModel() {
		if (windowsTableModel == null)
			windowsTableModel = new ApplicationTableModel();
		return this.windowsTableModel;
	}

	
	public void nullSpace(JButton btnNewButton) {
		btnNewButton.setBackground(null);
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setBorder(null);
	}

	public void setEventsDataModel(EventsTableModel eventsDataModel) {
		this.eventsDataModel = eventsDataModel;
	}

	public void setMacrosTableModel(MacrosTableModel macrosTableModel) {
		this.macrosTableModel = macrosTableModel;
	}

	public void setMainTabbedPanel(JTabbedPane mainTabbedPanel) {
		this.mainTabbedPanel = mainTabbedPanel;
	}



	public void setWindowsTableModel(ApplicationTableModel windowsTableModel) {
		this.windowsTableModel = windowsTableModel;
	}
}

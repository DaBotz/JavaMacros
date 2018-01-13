package sgi.javaMacros.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;

import sgi.generic.debug.Debug;
import sgi.gui.ConfigPanelCreator;
import sgi.gui.GUIMemory2;
import sgi.gui.configuration.BooleanJMenuItemSaver;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.controller.JavaMacrosController;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.controller.LuaMacrosListener;
import sgi.javaMacros.model.JavaMacrosConfiguration;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.deserializeHidMacros.HidMacrosDeserializer;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.internal.IDevice;
import sgi.javaMacros.model.internal.Key;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.model.internal.UseCase;
import sgi.javaMacros.model.lists.MacrosList;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.icons.Icons;
import sgi.javaMacros.ui.tray.JavaMacros_System_Tray;
import sgi.javaMacros.ui.tray.TrayEvent;
import sgi.javaMacros.ui.tray.TrayEventType;
import sgi.javaMacros.ui.tree.ConfigAtomTree;
import sgi.javaMacros.ui.viewmodels.ApplicationTableModel;
import sgi.javaMacros.ui.viewmodels.DeviceTableModel;
import sgi.javaMacros.ui.viewmodels.EventsTableModel;
import sgi.javaMacros.ui.viewmodels.MacrosTableModel; 

public class JavaMacrosUI extends JFrame {

	static class TreeToMacrosFilterListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {

			DefaultMutableTreeNode lpc = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
			Object[] userObjectPath = lpc.getUserObjectPath();
			long creationTime = 0;
			String exeFile = null;
			int scanCode = 0;
			String deviceName = null;

			for (int i = 0; i < userObjectPath.length; i++) {
				Object object = userObjectPath[i];
				if (object instanceof UseCase) {
					UseCase uc = (UseCase) object;
					creationTime = uc.getCreationTime();
				}

				else if (object instanceof ApplicationForMacros) {
					ApplicationForMacros uc = (ApplicationForMacros) object;
					exeFile = uc.getExeFile();
				}

				else if (object instanceof Key) {
					Key uc = (Key) object;
					scanCode = uc.getScanCode();
				}

				else if (object instanceof IDevice) {
					IDevice uc = (IDevice) object;
					deviceName = uc.getName();
				}

			}

			JavaMacrosMemory.instance().getMacros().setFilter(deviceName, scanCode, exeFile, creationTime);
		}
	}

	public class ZButton extends JButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4208847141086637736L;

		public ZButton(String text, ActionCommands actionCommand) {
			super(text);

			ImageIcon icon = Icons.getIcon(actionCommand.name().toLowerCase());
			if (icon != null)
				setIcon(icon);
			setBorder(BorderFactory.createLineBorder(Color.black));
			configureButtonActionCommand(this, actionCommand);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4048382608357613710L;
	protected ActionListener actionsImplementer = new ActionListener() {

		JavaMacrosConfiguration mainConfiguration = JavaMacrosConfiguration.instance();

		@Override
		public void actionPerformed(ActionEvent e) {
			String act = e.getActionCommand();
			// String subCommand = null;
			// int iOf = act.indexOf("+");
			// if (iOf > 0) {
			// subCommand = act.substring(iOf + 1);
			// act = act.substring(0, iOf);
			// }

			ActionCommands com = ActionCommands.valueOf(act);

			switch (com) {
			case OPEN_SETTINGS:
				JavaMacrosController.instance().getTrayListener()
						.handleTrayEvent(new TrayEvent(this, TrayEventType.CHANGELUACONFIG));
				break;
			case CREATE_NEW_MACRO:
				MacrosList list = massMemory.getMacros().getList();
				Macro macro = new Macro(true, "Macro " + (list.size() + 1));
				editSingleMacroPanel.setMacro(macro);
				list.add(macro);

				macrosTree.repaint();

				break;
			case ERASE_MACRO:
				Macro macro2 = editSingleMacroPanel.getMacro();
				removeMacrosTreeListener();
				try {
					massMemory.eraseMacro(macro2);
					macro2.setErased(true);
					editSingleMacroPanel.setMacro(null);
				} catch (Throwable e1) {
					Debug.info(e1);
				}
				addMacrosTreeListener();
				break;
			case EXIT_APPLICATION:
				System.exit(0);
			case FIND_MACRO:
				break;
			case LOOKFOR_BACKUP_DIRECTORY:
				break;
			case SAVE:
				mainConfiguration.storeToFile();
				massMemory.storeToFile();
				break;
			case SAVE_MACROS_SNAPSHOT:
				massMemory.getMacros().saveSnapshot();
				break;
			case SCAN_FOR_NEW_EVENT:
				break;
			case MANAGE_DEVICES:
				break;
			case LOAD_MACROS:
				break;
			case LOAD_FROM_HIDMACROS:
				JFileChooser chooser= new JFileChooser(); 
				int val = chooser.showOpenDialog(JavaMacrosUI.this); 
				MacrosList macrosLista = JavaMacrosMemory.instance().getMacros().getList(); 
				if( val== JFileChooser.APPROVE_OPTION ) {
					try {
						MacrosList[] translateToJavaMacros = new HidMacrosDeserializer().translateToJavaMacros(chooser.getSelectedFile());
						for (int i = 0; i < translateToJavaMacros.length; i++) {
							MacrosList macrosList = translateToJavaMacros[i];
							macrosLista.addAll(macrosList);
							
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
					
				}
				break;
			default:
				break;


			}

		}
	};

	// private ConfigAtomTree applicationsTree;

	private JSplitPane contentPane;
	private DeviceTableModel devicesDataModel;

	private EventsTableModel eventsDataModel;
	// private JTable eventsTable;
	// private JTable macrosTable;

	private MacrosTableModel macrosTableModel;
	protected ListSelectionListener macrosTableSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub

		}
	};

	private JTabbedPane mainTabbedPanel;

	private ApplicationTableModel windowsTableModel;

	private WindowAdapter windowListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			GUIMemory2.remove(JavaMacrosUI.this);
			super.windowClosing(e);
		}

		@Override
		public void windowIconified(WindowEvent e) {

			GUIMemory2.remove(JavaMacrosUI.this);

			boolean jmcToTray = JavaMacrosConfiguration.instance().isJavaMacrosMinimizesToTray();
			if (jmcToTray) {

				dispose();
			}
			super.windowIconified(e);
		}
	};
	private MacroEditPanel editSingleMacroPanel;
	private JavaMacrosMemory massMemory;
	private ConfigAtomTree devicesTree;
	private ConfigAtomTree applicationsTree;
	private ConfigAtomTree macrosTree;
	private DevicesPanel devicesPanel;
	private ApplicationsPanel applicationsPanel;

	/**
	 * Create the frame.
	 */
	public JavaMacrosUI() {

		massMemory = JavaMacrosMemory.instance();

		setTitle(Messages.M.getString(this, "Title"));
		setIconImage(Messages.M.getIcon());
		boolean jmcToTray = JavaMacrosConfiguration.instance().isJavaMacrosMinimizesToTray();
		setDefaultCloseOperation(jmcToTray ? JFrame.DISPOSE_ON_CLOSE : JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 1033, 770);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		addFileMenuEntry(menuBar);
		addOptionsMenuEntry(menuBar);

		contentPane = new JSplitPane();

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));

		setContentPane(contentPane);

		mainTabbedPanel = new JTabbedPane(JTabbedPane.TOP);

		devicesPanel = new DevicesPanel();
		devicesTree = devicesPanel.getTree();

		mainTabbedPanel.addTab("Devices", Icons.getIcon("deviceset"), devicesPanel,
				"All the devices appearing in the system that have associated macros");

		applicationsPanel = new ApplicationsPanel();
		mainTabbedPanel.addTab("Applications", null, applicationsPanel, null);
		applicationsTree = applicationsPanel.getTree();

		JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainTabbedPanel, createMacrosPanel());
		jSplitPane.setResizeWeight(0);
		jSplitPane.setContinuousLayout(true);
		contentPane.add(jSplitPane, BorderLayout.CENTER);
		jSplitPane.setOneTouchExpandable(true);
		jSplitPane.setDividerLocation(250);
		// GUIMemory2.add(this);
		addTreeListeners();
		addWindowStateListener(windowListener);
	}

	TreeSelectionListener macrosTreeListener = new TreeSelectionListener() {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
			Object[] userObjects = node.getUserObjectPath();

			if (userObjects.length < 2) {
				JavaMacrosMemory.instance().getMacros().setFilter(null, 0, null, 0);
				editSingleMacroPanel.setMacro(null);
			}

			for (Object userObject : userObjects) {

				if (userObject instanceof Macro) {
					Macro macro = (Macro) userObject;
					editSingleMacroPanel.setMacro(macro);
					devicesPanel.expandMacrosPath(macro);
					applicationsPanel.expandMacrosPath(macro);

					return;
				}
			}
		}
	};

	private void addTreeListeners() {

		addMacrosTreeListener();

		TreeToMacrosFilterListener tsl = new TreeToMacrosFilterListener();
		applicationsTree.addTreeSelectionListener(tsl);
		devicesTree.addTreeSelectionListener(tsl);

	}

	public void addMacrosTreeListener() {
		macrosTree.addTreeSelectionListener(macrosTreeListener);
	}

	public void removeMacrosTreeListener() {
		macrosTree.removeTreeSelectionListener(macrosTreeListener);
	}

	protected void addFileMenuEntry(JMenuBar menuBar) {
		JMenu mnFile = new JMenu(Messages.M._$(this, "file"));
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);

		JMenuItem mntmNewMenuItem_LoadFromHidMacros = createMenuItem(Messages.M._$(this, "loadFDromHidMacros"),
				ActionCommands.LOAD_FROM_HIDMACROS);
		mntmNewMenuItem_LoadFromHidMacros.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmNewMenuItem_LoadFromHidMacros.addActionListener(actionsImplementer);
		mnFile.add(mntmNewMenuItem_LoadFromHidMacros);

		JMenuItem mntmNewMenuItem_Save = createMenuItem(Messages.M._$(this, "save"), ActionCommands.SAVE);
		mntmNewMenuItem_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmNewMenuItem_Save.addActionListener(actionsImplementer);
		mnFile.add(mntmNewMenuItem_Save);

		mntmNewMenuItem_Save = createMenuItem(Messages.M._$(this, "saveSnapshots"),
				ActionCommands.SAVE_MACROS_SNAPSHOT);
		mntmNewMenuItem_Save
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmNewMenuItem_Save.addActionListener(actionsImplementer);

		mnFile.add(mntmNewMenuItem_Save);

		mnFile.add(new JSeparator());
		TrayEventType[] trayEvents = TrayEventType.values();
		for (TrayEventType trayEventType : trayEvents) {
			switch (trayEventType) {
			case CHANGELUACONFIG:
			case DIE:
			case OPEN_GUI:
				break;
			default:
				mnFile.add(createTraySystemOption(trayEventType));

			}
		}

		mnFile.add(new JSeparator());
		JMenuItem mntmNewMenuItem_2 = createMenuItem(Messages.M._$(this, "quit"), ActionCommands.EXIT_APPLICATION);
		mntmNewMenuItem_2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		mntmNewMenuItem_2.addActionListener(actionsImplementer);
		mnFile.add(mntmNewMenuItem_2);

		JMenuItem mntmNewMenuItem_1 = createMenuItem(Messages.M._$(this, "exit"), ActionCommands.EXIT_APPLICATION);
		mntmNewMenuItem_1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		mntmNewMenuItem_1.addActionListener(actionsImplementer);
		mnFile.add(mntmNewMenuItem_1);

	}

	protected JMenuItem createTraySystemOption(final TrayEventType evtype) {

		String eventLabel = Messages.M//
				.getString(//
						JavaMacros_System_Tray.class.getCanonicalName() //
								+ ".TrayEvent." //
								+ evtype.name());

		JMenuItem defaultItem = new JMenuItem(eventLabel);
		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JavaMacrosController.instance()//
						.getTrayListener()//
						.handleTrayEvent(new TrayEvent(e.getSource(), evtype));

			}
		});

		// defaultItem.setFont(getFont().deriveFont(Font.BOLD));
		return defaultItem;
	}

	private static final int NOGO = Modifier.STATIC | Modifier.TRANSIENT | Modifier.FINAL;
	private final ConfigPanelCreator configPanelCreator = new ConfigPanelCreator(Messages.M);
	{
		configPanelCreator.setRecursionPackageLimit("sgi.");
	}

	protected void addOptionsMenuEntry(JMenuBar menuBar) {
		JMenu mnOptions = new JMenu(Messages.M._$(this, "options"));
		mnOptions.setMnemonic('O');
		menuBar.add(mnOptions);

		JavaMacrosConfiguration instance = JavaMacrosConfiguration.instance();

		instance.getlUaMacrosSettings();

		addBooleanOptionsFromConfigObject(mnOptions, instance.getlUaMacrosSettings());
		mnOptions.addSeparator();
		addBooleanOptionsFromConfigObject(mnOptions, instance);
		mnOptions.addSeparator();

		JMenuItem mntmSettings = createMenuItem(Messages.M._$(this, "settings"), ActionCommands.OPEN_SETTINGS);
		mntmSettings.addActionListener(actionsImplementer);
		mnOptions.add(mntmSettings);

		mntmSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
	}

	public void addBooleanOptionsFromConfigObject(JMenu mnOptions, IAwareOfChanges instance) {
		Field[] fields = configPanelCreator.getFields(instance.getClass());
		for (int i = 0; fields != null && i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			if ((NOGO & field.getModifiers()) == 0 &&

					boolean.class.isAssignableFrom(field.getType())) {
				JMenuItem cfgBoolOpt = createConfigurationBooleanOption(instance, field);
				mnOptions.add(cfgBoolOpt);
			}
		}
	}

	// protected JMenuItem createConfigurationBooleanOption(final String property) {
	// String label = Messages.M._$(this, property);
	// final JCheckBoxMenuItem checkBoxItem = new JCheckBoxMenuItem(label);
	//
	// JavaMacrosConfiguration instance = JavaMacrosConfiguration.instance();
	// Field field = instance.getField(property);
	//
	// if (field == null) {
	// Debug.print(property + " is not defined ", 0, 10);
	// checkBoxItem.setText("Missing " + property);
	// return checkBoxItem;
	// }
	// field.setAccessible(true);
	// new BooleanJMenuItemSaver(instance, field, checkBoxItem).updateComponent();
	// return checkBoxItem;
	// }

	protected JMenuItem createConfigurationBooleanOption(IAwareOfChanges instance, Field field) {
		field.setAccessible(true);
		String label = Messages.M._$(this, field.getName());
		JCheckBoxMenuItem checkBoxItem = new JCheckBoxMenuItem(label);
		label = Messages.M.getTooltip(this, field.getName());
		if (!label.isEmpty())
			checkBoxItem.setToolTipText(label);
		new BooleanJMenuItemSaver(instance, field, checkBoxItem).updateComponent();
		return checkBoxItem;
	}

	public Container createMacrosPanel() {
		JSplitPane macrosMainPanel = new JSplitPane();
		macrosMainPanel.setResizeWeight(0.4);
		macrosMainPanel.setContinuousLayout(true);
		// mainTabbedPanel.addTab("MacrosList", null, macrosMainPanel, null);
		macrosMainPanel.setLayout(new BorderLayout(0, 0));

		createMacrosButtonsPanel();

		//

		editSingleMacroPanel = new MacroEditPanel();
		macrosMainPanel.add(editSingleMacroPanel, BorderLayout.CENTER);

		JPanel TriggerIncerceptPanel = createEventShowPanel();

		editSingleMacroPanel.add(TriggerIncerceptPanel, BorderLayout.NORTH);

		MacrosTreePanel macrosPanel = new MacrosTreePanel();
		macrosTree = macrosPanel.getTree();

		editSingleMacroPanel.addPropertyChangeListener("macro", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				macrosTree.setAlwaysHiglhlighted(evt.getNewValue());
			}
		});

		// macrosMainPanel.add(macrosPanel, BorderLayout.WEST);

		JSplitPane compoundMacroPane = new JSplitPane();
		compoundMacroPane.setContinuousLayout(true);
		JPanel pippo = new JPanel(new BorderLayout());
		pippo.add(createMacrosButtonsPanel(), BorderLayout.NORTH);
		pippo.add(macrosPanel, BorderLayout.CENTER);

		compoundMacroPane.setLeftComponent(pippo);

		compoundMacroPane.setRightComponent(macrosMainPanel);
		compoundMacroPane.setDividerLocation(250);
		// compoundMacroPane.setOneTouchExpandable(true);

		return compoundMacroPane;
	}

	protected JPanel createMacrosButtonsPanel() {
		JPanel macrosButtonsPanel = new JPanel();
		macrosButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

		JButton btnNewButton = new ZButton("New", ActionCommands.CREATE_NEW_MACRO);
		nullSpace(btnNewButton);
		macrosButtonsPanel.add(btnNewButton);

		JButton btnNewButton2 = new ZButton("Erase", ActionCommands.ERASE_MACRO);
		nullSpace(btnNewButton2);
		macrosButtonsPanel.add(btnNewButton2);

		// JButton btnFindMacro = new ZButton("Find",
		// ActionCommands.FIND_MACRO);
		// nullSpace(btnFindMacro);
		// macrosButtonsPanel.add(btnFindMacro);
		return macrosButtonsPanel;
	}

	protected JPanel createEventShowPanel() {
		final LuaEventPanel luaEventPanel = new LuaEventPanel();
		JavaMacrosController.instance().addLuaMacrosListener(new LuaMacrosListener() {

			@Override
			public void processLuaEvent(LuaEvent event) {
				luaEventPanel.updateFields(event);
			}
		});

		luaEventPanel.setEventUser(editSingleMacroPanel);

		return luaEventPanel;
	}

	public void addTableSorter(JTable devicesTable2) {
		devicesTable2.setRowSorter(new TableRowSorter<>(devicesTable2.getModel()));
	}

	public void configureButtonActionCommand(AbstractButton btnNewButton, ActionCommands action) {
		if (action != null) {
			btnNewButton.setActionCommand(action.name());
			btnNewButton.addActionListener(actionsImplementer);
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
		btnNewButton.setBorder(BorderFactory.createMatteBorder(1, 1, 2, 2, Color.DARK_GRAY));
		btnNewButton.setText(" " + btnNewButton.getText() + " ");
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

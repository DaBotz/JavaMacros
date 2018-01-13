package sgi.javaMacros.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import sgi.configuration.ConfigurationAtom;
import sgi.gui.GUIMemory2;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.StringUtils;
import sgi.javaMacros.model.enums.ModifierMasks;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.model.internal.UseCase;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.viewmodels.AbstractConfigAtomArrayTableModel;

public class LuaEventPanel extends JPanel {

	public static void main(String[] args) throws InterruptedException {
		final JFrame jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		WindowAdapter adapter = new WindowAdapter() {
			public void windowDeactivated(WindowEvent e) {
				gBye();
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				gBye();
			}

			public void gBye() {
				System.out.println("Good Bye.");
				System.exit(0);
			}
		};

		jFrame.addWindowFocusListener(adapter);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				jFrame.add(new LuaEventPanel());
				jFrame.pack();
				GUIMemory2.add(jFrame);
				jFrame.setVisible(true);

			}
		});

		do {
			Thread.sleep(500);

		} while (jFrame.isVisible() && jFrame.isActive());

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {

				GUIMemory2.instance().storeToFile();
			}
		}));

	}

	private static final long serialVersionUID = -4190519838251710317L;

	private JTextField jtextDevice;
	private JTextField jtextEvent;
	private transient LuaEvent lastEvent;
	private transient EventUser eventUser;
	private JTextField jtextWindowExe;
	private JTextField jtextWindowTitle;
	private JButton useTheseButton;

	private JButton filterByTheseButton;

	private UseCaseTableModel usCaseTableModel;

	private ModifierMasksGroup systemModifierMasksGroup;

	private ModifierMasksGroup applicationModifierMasksGroup;

	public EventUser getEventUser() {
		return eventUser;
	}

	public void setEventUser(EventUser eventUser) {
		this.eventUser = eventUser;
	}

	static class UseCaseTableHeaderCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5680125157706562533L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			setHorizontalAlignment(LEFT);
			setHorizontalTextPosition(LEFT);
			setFont(getFont().deriveFont(Font.BOLD));
			setBorder(new LineBorder(SystemColor.controlShadow));
			setOpaque(true);
			setBackground(SystemColor.control);

			return tableCellRendererComponent;
		}
	}

	static class UseCaseTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5680125157706562533L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Component me = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof UseCase) {
				UseCase uc = (UseCase) value;

				ApplicationForMacros app = (ApplicationForMacros) uc.getParent().getParent();
				Color color = null;

				if (isSelected) {

					setForeground(Color.YELLOW);
					setBackground(new Color(0));

				} else {
					int k = 0;
					if (app.isReal())
						k = 1;
					if (app.isEnabled())
						k |= 2;
					setForeground(Color.black);
					switch (k) {
					case 0:
						setForeground(Color.YELLOW);
					case 1:
						color = ConfigurationAtom.getDisabledColor();
						color = ConfigurationAtom.getDisabledColor();
						break;
					case 2:
					case 3:
						color = ConfigurationAtom.getEnabledColor();
					}
					setBackground(color);
				}

				String txt = "";

				switch (column) {
				case 0:
					txt = (uc.getName());
					break;
				case 1:
					txt = (StringUtils.decamelizeExe("" + uc.getPriority(), true));
					break;
				case 2:
					txt = StringUtils.decamelizeExe(app.getName(), true);

					break;
				}
				setText(txt);
				setToolTipText(txt);

			}

			return me;
		}
	}

	public static interface EventUser {

		public void useEvent(LuaEvent luaEvent);
	}

	public static class UseCaseTableModel extends AbstractConfigAtomArrayTableModel<UseCase> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4806661797386269961L;

		public UseCaseTableModel(UseCase[] collection, Class<UseCase> class1, String[] names,
				boolean[] columnEditables) {
			super(collection, class1, names, columnEditables);

		}

		public UseCaseTableModel() {
			this(new UseCase[] {}, UseCase.class, new String[] { "name", "priority", "priority" },
					new boolean[] { false, false, false, false, false, false });

		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return getObjectAt(rowIndex);
		}
	}

	public LuaEventPanel() {
		super();
		GridBagLayout mgr = new GridBagLayout();
		setLayout(mgr);
		GridBagConstraints gbcLabels = new GridBagConstraints();
		GridBagConstraints gbcFields = new GridBagConstraints();
		gbcFields.fill = GridBagConstraints.HORIZONTAL;
		gbcLabels.anchor = GridBagConstraints.EAST;

		gbcLabels.gridx = 1;
		gbcFields.gridx = 2;
		gbcLabels.gridy = 1;
		gbcFields.gridy = 1;

		mgr.columnWeights = new double[13];
		mgr.rowWeights = new double[13];

		mgr.columnWidths = new int[mgr.columnWeights.length];
		mgr.columnWidths[mgr.columnWidths.length - 1] = 10;

		mgr.columnWeights[0] = 1;

		mgr.rowHeights = new int[mgr.rowWeights.length];

		int sum = 0;
		for (int i = 0; i < mgr.rowHeights.length - 1; i++) {
			sum += mgr.rowHeights[i] = (i % 2 == 0) ? 3 : 22;
			sum++;
		}
		mgr.rowHeights[0] = 5;
		mgr.rowHeights[mgr.rowHeights.length - 1] = 15;

		mgr.rowWeights[mgr.rowWeights.length - 1] = 1;
		sum += 30;

		setPreferredSize(new Dimension(400, sum));
		setMaximumSize(new Dimension(400, sum));
		setSize(new Dimension(400, sum));

		// addGridIdentifiers(mgr);

		setBorder(new TitledBorder(null, Messages.M.getString("LuaEventPanel.title"), TitledBorder.LEADING, //$NON-NLS-1$
				TitledBorder.TOP, null, null));
	
		jtextWindowTitle = addJTextField(Messages.M.getString("LuaEventPanel.Window"), gbcLabels, gbcFields);//// $NON-NLS-N$
		jtextWindowExe = addJTextField(Messages.M.getString("LuaEventPanel.Application"), gbcLabels, gbcFields);//// $NON-NLS-N$
		jtextDevice = addJTextField(Messages.M.getString("LuaEventPanel.Device"), gbcLabels, gbcFields);//// $NON-NLS-N$
		jtextEvent = addJTextField(Messages.M.getString("LuaEventPanel.Event"), gbcLabels, gbcFields);//// $NON-NLS-N$

		systemModifierMasksGroup= addModifiersMaskCheckersGroup(gbcLabels, gbcFields, Messages.M.getString("LuaEventPanel.systemModifiers"));
		applicationModifierMasksGroup= addModifiersMaskCheckersGroup(gbcLabels, gbcFields, Messages.M.getString("LuaEventPanel.applicationModifiers"));


		gbcFields.gridy = 1;
		gbcFields.gridheight = 3;
		add(new JPanel(), gbcFields.clone());

		useTheseButton = new JButton(Messages.htmlSwing(Messages.M.getString("LuaEventPanel.UseThis"))); //$NON-NLS-1$
		useTheseButton.setToolTipText(Messages.htmlSwing(Messages.M.getString("LuaEventPanel.UseThis.tooltip"))); //$NON-NLS-1$

		addVerticalDivider(gbcFields);

		GridBagConstraints g2 = (GridBagConstraints) gbcFields.clone();
		g2.gridheight = 11;

		usCaseTableModel = new UseCaseTableModel();
		JTable useCaseTable = new JTable(usCaseTableModel);
		// useCaseTable.setTableHeader(null);
		useCaseTable.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("Use Cases");
		useCaseTable.getTableHeader().getColumnModel().getColumn(1).setHeaderValue("Priority");
		useCaseTable.getTableHeader().getColumnModel().getColumn(2).setHeaderValue("Application");
		Enumeration<TableColumn> columns = useCaseTable.getTableHeader().getColumnModel().getColumns();

		DefaultTableCellRenderer headerRenderer = new UseCaseTableHeaderCellRenderer();

		while (columns.hasMoreElements()) {
			TableColumn tableColumn = (TableColumn) columns.nextElement();
			tableColumn.setHeaderRenderer(headerRenderer);
		}

		DefaultTableCellRenderer cellRenderer = new UseCaseTableCellRenderer();
		useCaseTable.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
		useCaseTable.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);
		useCaseTable.getColumnModel().getColumn(2).setCellRenderer(cellRenderer);

		useCaseTable.setFillsViewportHeight(true);
		useCaseTable.getSelectionModel();
		useCaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		useCaseTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				UseCase uCase = (UseCase) usCaseTableModel.getValueAt(e.getFirstIndex(), 0);
				lastEvent.setDesiredUseCase(uCase);
			}
		});

		JScrollPane scroll = new JScrollPane(useCaseTable);
		scroll.setMinimumSize(new Dimension(220, 50));

		add(scroll, g2);

		addVerticalDivider(gbcFields);

		add(useTheseButton, gbcFields.clone());
		useTheseButton.setEnabled(false);

		useTheseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (eventUser != null)
					eventUser.useEvent(lastEvent);
			}
		});

		gbcFields.gridy += 4;

		filterByTheseButton = new JButton(Messages.htmlSwing(Messages.M.getString("LuaEventPanel.filterOnThis"))); //$NON-NLS-1$
		filterByTheseButton
				.setToolTipText(Messages.htmlSwing(Messages.M.getString("LuaEventPanel.filterOnThis.tooltip"))); //$NON-NLS-1$

		add(filterByTheseButton, gbcFields.clone());
		filterByTheseButton.setEnabled(false);

		filterByTheseButton.addActionListener(new ActionListener() {

			private String lastFilter;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (lastEvent == null)
					return;

				String exeFile = null;
				String deviceName = lastEvent.getDeviceName();
				int scanCode = lastEvent.getScanCode();
				String check = deviceName + "::" + scanCode;

				if (check.equals(lastFilter)) {

					if (String.valueOf(lastFilter).indexOf("\\\\") < 0)
						check += "\\\\" + (exeFile = lastEvent.getActiveWindow().getExeFile());
					else {

						check = "";
						deviceName = null;
						scanCode = 0;

					}
				}
				lastFilter = check;

				JavaMacrosMemory.instance().getMacros().setFilter(deviceName, scanCode, exeFile, 0);
			}
		});

	}

	public ModifierMasksGroup addModifiersMaskCheckersGroup(GridBagConstraints gbcLabels, GridBagConstraints gbcFields,
			String string) {
		ModifierMasksGroup modifierMasksGroup = new ModifierMasksGroup(true);
		modifierMasksGroup.setEnabled(false);
		JPanel panel = modifierMasksGroup.getPanel();
		panel.setSize(new Dimension(200, 16));
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(1, ModifierMasks.values(true).length));
		addComponent(new JLabel(string), panel, gbcLabels, gbcFields);
		return modifierMasksGroup;
	}

	public void addVerticalDivider(GridBagConstraints gbcFields) {
		gbcFields.gridx++;
		gbcFields.fill = GridBagConstraints.HORIZONTAL;
		add(new JPanel(), gbcFields.clone());
		gbcFields.gridx++;

		gbcFields.fill = GridBagConstraints.BOTH;
	}

	public void addGridIdentifiers(GridBagLayout mgr) {
		double[] cWs = mgr.columnWeights;
		double[] rws = mgr.rowWeights;
		for (int i = 0; i < cWs.length; i++) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = i;
			JLabel comp = new JLabel("" + i);
			comp.setFont(comp.getFont().deriveFont(7f));
			add(comp, gbc);

		}

		for (int i = 1; i < rws.length; i++) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = cWs.length - 1;
			gbc.gridy = i;
			JLabel comp = new JLabel("" + i);
			comp.setFont(comp.getFont().deriveFont(5f));
			add(comp, gbc);

		}
	}

	public JTextField addJTextField(String _label, GridBagConstraints gbcLabels, GridBagConstraints gbcFields) {
		JTextField jText = new JTextField();
		jText.setMinimumSize(new Dimension(200, 20));
		add(new JLabel(_label), jText, gbcLabels, gbcFields);

		return jText;
	}

	protected void newLine(GridBagConstraints gbcLabels, GridBagConstraints gbcFields) {
		gbcLabels.gridy += 2;
		gbcFields.gridy += 2;
		gbcLabels.gridx = 1;
		gbcFields.gridx = 2;
	}

	protected void add(JLabel lblNewLabel, JTextField jtextDevice2, GridBagConstraints gbcLabels,
			GridBagConstraints gbcFields) {
		jtextDevice2.setColumns(15);
		jtextDevice2.setDisabledTextColor(new Color(100, 100, 200));
		addComponent(lblNewLabel, jtextDevice2, gbcLabels, gbcFields);
	}

	public void addComponent(JLabel lblNewLabel, JComponent jc, GridBagConstraints gbcLabels,
			GridBagConstraints gbcFields) {
		add(lblNewLabel, gbcLabels);
		add(jc, gbcFields);
		gbcLabels.gridy += 2;
		gbcFields.gridy += 2;
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setText("    " + lblNewLabel.getText() + ": ");
		jc.setEnabled(false);
	}

	public void updateFields(LuaEvent event) {

		if (event == null) {
			jtextDevice.setText("");
			jtextEvent.setText("");
			jtextWindowExe.setText("");
			jtextWindowTitle.setText("");
			useTheseButton.setEnabled(false);
			filterByTheseButton.setEnabled(false);
			usCaseTableModel.setArray(new UseCase[] {});
			systemModifierMasksGroup.setModifierMask(0);
			applicationModifierMasksGroup.setModifierMask(0);
			
			return;
		} else if (!event.isDown()) {
			return;
		}

		this.lastEvent = event;
		Device device = event.getDevice();

		jtextDevice.setText(device.getName());
		jtextEvent.setText(String.valueOf(device.getType() + ": " + device.getKeySet().find(event.getScanCode())));
		jtextWindowExe.setText(event.getActiveWindow().getName());
		jtextWindowTitle.setText(event.getActiveWindow().getWindowTitle());
		useTheseButton.setEnabled(true);
		filterByTheseButton.setEnabled(true);
		usCaseTableModel.setArray(event.getActiveUseCases());
		systemModifierMasksGroup.setModifierMask(event.getSystemModifiersMask());
		applicationModifierMasksGroup.setModifierMask(event.getApplicationModifiersMaskPreset());

	}

}

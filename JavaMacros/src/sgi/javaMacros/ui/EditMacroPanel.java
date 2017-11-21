package sgi.javaMacros.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import sgi.javaMacros.model.enums.ActionType;
import sgi.javaMacros.model.internal.Application;
import sgi.javaMacros.model.internal.ConfigAtom;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.ui.internal.AbstractButtonablePanel;
import sgi.javaMacros.ui.internal.FixedGridBagConstraints;

public class EditMacroPanel extends AbstractButtonablePanel {

	private static final long serialVersionUID = -4707792221537349074L;


	private JTextField textFieldMacroName;

	protected DefaultComboBoxModel<ConfigAtom> windowComboBoxModel, deviceComboBoxModel;

	private JPanel modifiersPanel;
	
	public void loadMacro( Macro macro){
		textFieldMacroName.setText(macro.getName());
		payLoadSelectorsGroup.setSelected(macro.getActionType());
	}

	private PayLoadSelectorsGroup payLoadSelectorsGroup;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame();
					frame.getContentPane().setLayout(new GridLayout(1, 1));
					EditMacroPanel comp = new EditMacroPanel();
					frame.getContentPane().add(comp);
					comp.getPayLoadSelectorsGroup().setSelected(ActionType.SCRIPT);
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public EditMacroPanel() {
		super();
		
		initModels();

		this.setLayout(new BorderLayout(0, 0));

		JPanel editMacroPanel = new JPanel();
		editMacroPanel
				.setBorder(new TitledBorder(null, "Edit macro", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.add(editMacroPanel);
		GridBagLayout gridBagLayout_editMacroPanel = new GridBagLayout();
		gridBagLayout_editMacroPanel.columnWidths = new int[] { 440 };
		gridBagLayout_editMacroPanel.rowHeights = new int[] { 40, 5, 250 };
		gridBagLayout_editMacroPanel.columnWeights = new double[] { 1.0 };
		gridBagLayout_editMacroPanel.rowWeights = new double[] { 0.0, 1.0, 1.0 };
		editMacroPanel.setLayout(gridBagLayout_editMacroPanel);

		JPanel macroHeaderPanel = new JPanel();
		GridBagConstraints gbc_macroHeaderPanel = new GridBagConstraints();
		gbc_macroHeaderPanel.gridheight = 2;
		gbc_macroHeaderPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_macroHeaderPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_macroHeaderPanel.insets = new Insets(0, 0, 5, 0);
		gbc_macroHeaderPanel.weighty = 5.0;
		gbc_macroHeaderPanel.gridx = 0;
		gbc_macroHeaderPanel.gridy = 0;
		int comboWidth = 130;

		editMacroPanel.add(macroHeaderPanel, gbc_macroHeaderPanel);
		GridBagLayout gbl_macroHeaderPanel = new GridBagLayout();
		gbl_macroHeaderPanel.columnWidths = new int[] { 51, 120, 120, 120 };
		gbl_macroHeaderPanel.rowHeights = new int[] { 23, 23, 23, 23 };
		gbl_macroHeaderPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 0.0 };
		gbl_macroHeaderPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		macroHeaderPanel.setLayout(gbl_macroHeaderPanel);

		JLabel lblNewLabel_1 = new JLabel("Name:");
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
		gbc_textFieldMacroName.anchor = GridBagConstraints.NORTH;
		gbc_textFieldMacroName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMacroName.gridx = 1;
		gbc_textFieldMacroName.gridwidth = 4;
		gbc_textFieldMacroName.gridy = 0;
		macroHeaderPanel.add(textFieldMacroName, gbc_textFieldMacroName);

		JLabel lblNewLabel_2 = new JLabel("Trigger: ");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 1;
		macroHeaderPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);

		GridBagConstraints gbc_macroComboBoxDevice = new GridBagConstraints();
		gbc_macroComboBoxDevice.fill = GridBagConstraints.BOTH;
		gbc_macroComboBoxDevice.insets = new Insets(0, 0, 5, 5);
		gbc_macroComboBoxDevice.gridx = 1;
		gbc_macroComboBoxDevice.gridy = 1;

		JComboBox<ConfigAtom> deviceComboBox = new JComboBox<ConfigAtom>();

		deviceComboBox.setPreferredSize(new Dimension(comboWidth, 20));

		macroHeaderPanel.add(deviceComboBox, gbc_macroComboBoxDevice);
		deviceComboBox.setModel(deviceComboBoxModel);


		JComboBox<ConfigAtom> appLicationComboBox = new JComboBox<ConfigAtom>();
		appLicationComboBox.setPreferredSize(new Dimension(comboWidth, 20));
		appLicationComboBox.setModel(windowComboBoxModel);

		GridBagConstraints gbc_macroApplicationComboBox = new GridBagConstraints();
		gbc_macroApplicationComboBox.fill = GridBagConstraints.BOTH;
		gbc_macroApplicationComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_macroApplicationComboBox.gridx = 3;
		gbc_macroApplicationComboBox.gridy = 1;

		macroHeaderPanel.add(appLicationComboBox, gbc_macroApplicationComboBox);

		JButton scanButton = new JButton("Scan");
		scanButton.setActionCommand(ActionCommands.SCAN_FOR_NEW_EVENT_IN_MACRO.name());
		getButtons().add(scanButton);

		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.anchor = GridBagConstraints.NORTHEAST;
		gbc_btnNewButton_1.gridx = 4;
		gbc_btnNewButton_1.gridy = 1;
		macroHeaderPanel.add(scanButton, gbc_btnNewButton_1);

		JLabel lblNewLabel_5 = new JLabel("Modifiers:");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 2;
		macroHeaderPanel.add(lblNewLabel_5, gbc_lblNewLabel_5);

		modifiersPanel = new JPanel();
		modifiersPanel.setLayout(new GridLayout(2, 5));

		GridBagConstraints gbc_modifiersPanel = new GridBagConstraints();
		gbc_modifiersPanel.insets = new Insets(0, 0, 5, 0);
		gbc_modifiersPanel.gridheight = 2;
		gbc_modifiersPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_modifiersPanel.gridx = 1;
		gbc_modifiersPanel.gridwidth = 4;
		gbc_modifiersPanel.gridy = 2;

		macroHeaderPanel.add(modifiersPanel, gbc_modifiersPanel);

		JCheckBox ctrlCheckBox = new ModifierCheckBox("Ctrl");
		modifiersPanel.add(ctrlCheckBox);
		JCheckBox altCheckBox = new ModifierCheckBox("Alt");
		modifiersPanel.add(altCheckBox);
		JCheckBox shiftCheckBox = new ModifierCheckBox("Shift");
		modifiersPanel.add(shiftCheckBox);
		JCheckBox metaCheckBox = new ModifierCheckBox("Meta");
		modifiersPanel.add(metaCheckBox);

		JPanel editMacroActionPanel = new JPanel();
		editMacroActionPanel
				.setBorder(new TitledBorder(null, "Action", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		gbl_panelSelectActionTypeRadioButtons.rowHeights = new int[] { 25, 25, 25, 25, 25, 25, 0 };
		gbl_panelSelectActionTypeRadioButtons.columnWeights = new double[] { 1.0 };
		gbl_panelSelectActionTypeRadioButtons.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 100 };
		panelSelectActionTypeRadioButtons.setLayout(gbl_panelSelectActionTypeRadioButtons);

		JRadioButton rdbtnNewRadioButton = new PayloadSelectorRadioButton("Keyboard", ActionType.SEND);
		rdbtnNewRadioButton.setSelected(true);
		panelSelectActionTypeRadioButtons.add(rdbtnNewRadioButton, new FixedGridBagConstraints(0, 0));

		JRadioButton rdbtnNewRadioButton_1 = new PayloadSelectorRadioButton("Run application",
				ActionType.RUN_APPLICATION);

		panelSelectActionTypeRadioButtons.add(rdbtnNewRadioButton_1, new FixedGridBagConstraints(0, 1));

		JRadioButton rdbtnNewRadioButton_2 = new PayloadSelectorRadioButton("Script", ActionType.SCRIPT);
		panelSelectActionTypeRadioButtons.add(rdbtnNewRadioButton_2, new FixedGridBagConstraints(0, 2));

		JRadioButton rdbtnNewRadioButton_3 = new PayloadSelectorRadioButton("Modifier", ActionType.MODIFIER);
		panelSelectActionTypeRadioButtons.add(rdbtnNewRadioButton_3, new FixedGridBagConstraints(0, 3));

		JRadioButton rdbtnNewRadioButton_4 = new PayloadSelectorRadioButton("Variant", ActionType.VARIANT);
		panelSelectActionTypeRadioButtons.add(rdbtnNewRadioButton_4, new FixedGridBagConstraints(0, 4));

		payLoadSelectorsGroup = new PayLoadSelectorsGroup();
		payLoadSelectorsGroup.add(rdbtnNewRadioButton);
		payLoadSelectorsGroup.add(rdbtnNewRadioButton_1);
		payLoadSelectorsGroup.add(rdbtnNewRadioButton_2);
		payLoadSelectorsGroup.add(rdbtnNewRadioButton_3);
		payLoadSelectorsGroup.add(rdbtnNewRadioButton_4);

		final JLayeredPane layeredPane = new JLayeredPane();
		editMacroActionPanel.add(layeredPane, BorderLayout.CENTER);
		layeredPane.setLayout(new BorderLayout(0, 0));
		layeredPane.setLayout(new GridLayout(1, 1));

		JScrollPane scrollPane = new JScrollPane();
		layeredPane.add(scrollPane);

		JTextArea payLoadTextArea = new JTextArea();
		scrollPane.setViewportView(payLoadTextArea);
	}

	@SuppressWarnings("deprecation")
	public void initModels() {
		Application a0 = new Application(); 
		a0.setName("[Every Application]");
	
		windowComboBoxModel = new DefaultComboBoxModel<>(new ConfigAtom[] {a0});
		
	Device b0 = new Device(); 
		b0.setName("[Every Application]");
		
		deviceComboBoxModel = new DefaultComboBoxModel<>(new ConfigAtom[] {b0});


	}

	public PayLoadSelectorsGroup getPayLoadSelectorsGroup() {
		return payLoadSelectorsGroup;
	}

}

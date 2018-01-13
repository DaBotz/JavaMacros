package sgi.javaMacros.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import sgi.gui.configuration.IAwareOfChanges;
import sgi.gui.configuration.JLabelUpdater;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.internal.CompoundDevice;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.msgs.Messages;

public class DeviceConfigPanelCreator extends JavaMacrosPanelCreator {

	public static final String MAIN_KBD = "MAIN_KBD";
	public static final String NEW_DEVICE = "New  Device Name";
	public static final String NSTRING = "---";

	private boolean nameIsGood;
	private long lastClick;

	public String[] devicesNames = new String[0];

	public String[] getDevicesNames() {
		return devicesNames;
	}

	public void setDevicesNames(String[] devicesNames) {
		this.devicesNames = devicesNames;
	}

	private boolean firstCall;

	public DeviceConfigPanelCreator() {
		super(Messages.M);
		setUseFieldSeparators(true);
		setUsingRecursiveFieldScan(true);
		FieldsSettings.setInvisibleFields("label", "systemId");

	}

	@Override
	protected JComponent createIntEditor(IAwareOfChanges obj, Field field) throws IllegalAccessException {
		JComponent createIntEditor = super.createIntEditor(obj, field);
		createIntEditor.setEnabled(false);
		return createIntEditor;
	}

	@Override
	protected JComponent createTextField(IAwareOfChanges obj, Field field)
			throws IllegalArgumentException, IllegalAccessException {

		if ("name".equalsIgnoreCase(field.getName()) && obj instanceof Device)
			return createNamesComboBox(obj);

		JLabel lbl = new JLabel();
		new JLabelUpdater(obj, field, lbl).updateComponent();
		lbl.setHorizontalTextPosition(SwingConstants.RIGHT);
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);

		return lbl;

		// JComponent jc = super.createTextField(obj, field);
		//
		// if ("luaMacrosId".equalsIgnoreCase(field.getName())) {
		// if (jc instanceof JTextField) {
		// JTextField ftext = (JTextField) jc;
		// ftext.setHorizontalAlignment(SwingConstants.RIGHT);
		// }
		//
		//
		// }
		//
		//
		// jc.setEnabled(false);
		// //
		// jc.setBackground(JavaMacrosConfiguration.instance().getColorsSettings().get_disabledColor());
		// return jc;
	}

	public JComponent createNamesComboBox(IAwareOfChanges obj) {
		{

			final Device dev = (Device) obj;
			if (devicesNames == null) {
				CompoundDevice[] asArray = JavaMacrosMemory.instance().getDeviceSet().getDevicesByName().asArray();
				devicesNames = new String[asArray.length];
				for (int i = 0; i < asArray.length; i++) {
					devicesNames[i] = asArray[i].getName();
				}

			}

			firstCall = devicesNames.length == 0;
			if (firstCall) {
				devicesNames = new String[] { MAIN_KBD };

			}

			final String[] list = new String[devicesNames.length + 2];
			list[0] =
					// NSTRING;
					// list[1] =
					dev.getLuaMacrosId();

			final String oN = dev.getName();

			System.arraycopy(devicesNames, 0, list, 1, devicesNames.length);

			list[list.length - 1] = NEW_DEVICE;
			final JComboBox<String> box = new JComboBox<>(list);
			box.setRenderer(new ListCellRenderer<String>() {

				@Override
				public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
						boolean isSelected, boolean cellHasFocus) {

					JButton jb = new JButton(value);
					if (MAIN_KBD.equals(value)) {

						jb.setBackground(Color.YELLOW);
					}

					if (value.equals(oN)) {

						jb.setBackground(Color.LIGHT_GRAY);
					}

					if (value == NEW_DEVICE) {

						jb.setBackground(Color.BLUE);
						jb.setForeground(Color.white);
					}

					Border line = new LineBorder(Color.BLACK);
					jb.setBorder(line);
					int left = SwingConstants.LEFT;
					jb.setHorizontalTextPosition(left);
					jb.setHorizontalAlignment(left);
					jb.setBorderPainted(false);
					jb.setFocusPainted(false);

					return jb;
				}
			});
			if (firstCall) {
				box.setSelectedItem(MAIN_KBD);
			} else if (dev.getName() != null) {
				box.setSelectedItem(dev.getName());

			}

			ActionListener lst = new ActionListener() {

				Object lastAdded;

				@Override
				public void actionPerformed(ActionEvent e) {
					Object selectedItem = box.getSelectedItem();

					if (selectedItem == NEW_DEVICE || selectedItem.equals(lastAdded)) {
						box.setEditable(true);
						if (NEW_DEVICE == selectedItem)
							box.setSelectedItem("");

					}

					nameIsGood = !("".equals(selectedItem) || selectedItem == NSTRING || selectedItem == NEW_DEVICE);
					String name = "" + selectedItem;

					if (nameIsGood) {
						if (box.isEditable()) {
							box.removeItem(NSTRING);

							if (lastAdded != null)
								box.removeItem(lastAdded);
							box.insertItemAt(name, 0);
							box.setSelectedItem(name);
							box.setEditable(false);
							lastAdded = name;
						}

						dev.setName(name);
					}

					JButton saveButton = getSaveButton();
					if (saveButton != null)
						saveButton.setEnabled(nameIsGood);
				}
			};

			box.addActionListener(lst);
			box.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					long ctms = System.currentTimeMillis();
					if (nameIsGood && (ctms - lastClick) < 300) {
						box.setEditable(true);
					}

					lastClick = ctms;
				}
			});
			box.getEditor().addActionListener(lst);

			return box;
		}
	}

}
package sgi.javaMacros.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

import sgi.generic.ui.GUIMemory;
import sgi.generic.ui.IMessages;
import sgi.generic.ui.ModalConfigDialog;
import sgi.generic.ui.RichFileIconer;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.model.lists.DeviceSet;

public class DeviceInputDialog extends ModalConfigDialog {

	private static final String MAIN_KBD = "MAIN_KBD";
	private static final String NEW_DEVICE = "New  Device Name";
	private static final String NSTRING = "---";
	/**
	 * 
	 */
	private static final long serialVersionUID = -1846333374092233287L;
	private String[] devicesNames;
	private JCheckBox ignoredCheckBox;
	private boolean firstCall;

	@Override
	protected String[] noFields() {
		return new String[] { "luaMacrosId" };
	}

	@Override
	protected void configureFileChooser(JFileChooser chooser, File file, Field field, Object object) {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Only .gif, .png  and .jpg files";
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				String ext = f.getName().replaceAll(".*(\\.(\\w{3,4}))", "$2").toLowerCase();
				if ("png".equals(ext))
					return true;
				if ("jpg".equals(ext))
					return true;
				if ("jpeg".equals(ext))
					return true;
				if ("gif".equals(ext))
					return true;

				return false;
			}
		});

		chooser.setFileView(new RichFileIconer(RichFileIconer.BIG));

	}

	@Override
	public boolean requireSortFields() {
		return false;
	}

	@Override
	public void build(Object object, IMessages msgs) throws HeadlessException {
		setUsingRecursiveFieldScan(true);
		setFieldsSorted(false);
		super.build(object, msgs);
		GUIMemory.add(this);
		if (firstCall && ! ignoredCheckBox.isSelected() ) {
			ignoredCheckBox.doClick();
		}
		setResizable(true);
		GUIMemory.add(this);
	}

	@Override
	public JComponent createBooleanEditor(Object obj, Field field) throws IllegalAccessException {
		// TODO Auto-generated method stub
		JComponent createBooleanEditor = super.createBooleanEditor(obj, field);
		if ("ignored".equals(field.getName())) {
			JPanel panel = (JPanel) createBooleanEditor;
			Component[] components = panel.getComponents();
			for (int i = 0; ignoredCheckBox == null && i < components.length; i++) {
				Component component = components[i];
				if (component instanceof JCheckBox) {
					this.ignoredCheckBox = (JCheckBox) component;

				}
			}

		}

		return createBooleanEditor;
	}

	private boolean nameIsGood; 
	private long lastClick;

	@Override
	protected JComponent createTextField(Object obj, Field field)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		JComponent jc = super.createTextField(obj, field);

		if ("luaMacrosId".equalsIgnoreCase(field.getName())) {
			jc.setEnabled(false);
		}

		if ("name".equalsIgnoreCase(field.getName()) && obj instanceof Device) {

			final Device dev = (Device) obj;
			firstCall = devicesNames.length == 0;
			if (firstCall) {
				devicesNames = new String[] { MAIN_KBD };

			}

			final String[] list = new String[devicesNames.length + 2];
			list[0] =
					// NSTRING;
					// list[1] =
					dev.getLuaMacrosId();
			
			String oN= dev.getName();

			System.arraycopy(devicesNames, 0, list, 1, devicesNames.length);

			list[list.length - 1] = NEW_DEVICE;
			final JComboBox<String> box = new JComboBox<>(list);
			box.setRenderer(new ListCellRenderer<String>() {

				@Override
				public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
						boolean isSelected, boolean cellHasFocus) {
				
					JButton jb = new JButton(value);
					if( MAIN_KBD.equals(value) ) {
						
						jb.setBackground(Color.YELLOW);
					}
					
					if( value.equals(oN) ) {
						
						jb.setBackground(Color.LIGHT_GRAY);
					}
					
					if( value==NEW_DEVICE ) {
						
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
			}else if(dev.getName()!= null) {
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

					if (selectedItem == MAIN_KBD) {
						ignoredCheckBox.setSelected(true);
					}

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

					getSaveButton().setEnabled(nameIsGood);
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

		return jc;
	}

	public DeviceInputDialog() {
		super();

	}

	public DeviceInputDialog(Dialog owner, boolean modal) {
		super(owner, modal);

	}

	public DeviceInputDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);

	}

	public DeviceInputDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);

	}

	public DeviceInputDialog(Dialog owner, String title) {
		super(owner, title);

	}

	public DeviceInputDialog(Dialog owner) {
		super(owner);

	}

	public DeviceInputDialog(Frame owner, boolean modal) {
		super(owner, modal);

	}

	public DeviceInputDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);

	}

	public DeviceInputDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);

	}

	public DeviceInputDialog(Frame owner, String title) {
		super(owner, title);

	}

	public DeviceInputDialog(Frame owner) {
		super(owner);

	}

	public DeviceInputDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);

	}

	public DeviceInputDialog(Window owner, String title, ModalityType modalityType,
			GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);

	}

	public DeviceInputDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);

	}

	public DeviceInputDialog(Window owner, String title) {
		super(owner, title);

	}

	public DeviceInputDialog(Window owner) {
		super(owner);

	}

	public void setDeviceSet(DeviceSet devices) {
		this.devicesNames = devices.nameSet();

	}

}

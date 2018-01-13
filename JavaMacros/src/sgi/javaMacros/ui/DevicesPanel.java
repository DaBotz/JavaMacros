package sgi.javaMacros.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.internal.CompoundDevice;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.dialogs.DeviceSetFrame;
import sgi.javaMacros.ui.internal.AbstractTreePanel;
import sgi.javaMacros.ui.tree.IEditabilityAssessor;

public class DevicesPanel extends AbstractTreePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8869342590705490883L;

	public DevicesPanel() {
		super();
		super.listensTo(JavaMacrosMemory.instance().getDevices());

		getTree().setEditabilityAssessor(new IEditabilityAssessor() {
			@Override
			public boolean isEditable(TreePath path, Field field) {

				// Object[] path2 = path.getPath();
				//
				// Object expectedDevice = path2[path2.length - 3];
				//
				// if (!(expectedDevice instanceof ConfigAtomMutableTreeNode))
				// return false;
				//
				// ConfigAtomMutableTreeNode ctn = (ConfigAtomMutableTreeNode) expectedDevice;
				// if (!(ctn.getUserObject() instanceof Device))
				// return false;

				String fname = field.getName();
				if ("name".equalsIgnoreCase(fname))
					return true;
				if ("enabled".equalsIgnoreCase(fname))
					return true;
				if ("autonomousNumLock".equalsIgnoreCase(fname))
					return true;

				return false;
			}
		});

		addButton("Edit", ActionCommands.MANAGE_DEVICES).addActionListener(new ActionListener() {

			private Container devicesPane;

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton bt = (JButton) e.getSource();
				JScrollPane scp = getScrollPanel();
				Component view = scp.getViewport().getView();
				if (view == getTree()) {

					
					
					scp.getViewport().setView(getDevicesPane());
					bt.setText("Tree");
				} else {
					bt.setText("Edit");
					scp.getViewport().setView(getTree());
				}
			}

			public Container getDevicesPane() {
				if( devicesPane== null) {
					DeviceSetFrame dsf = new DeviceSetFrame(
							JavaMacrosMemory.instance().getDevices(), Messages.M);

					devicesPane = dsf.getContentPane();
				}
				return devicesPane;
			}
		});

	}

	@Override
	protected Object getRootObject() {
		return JavaMacrosMemory.instance().getDeviceSet().getDevicesByName().asListForViewer();
	}

	public void expandMacrosPath(Macro macro) {

		if (!(macro.getScanCode() <= 0 || macro.getKey() == null))
			getTree().exandPathTo(macro.getKey());
		else {
			CompoundDevice device = macro.getDevice();
			if (device == null)
				return;

			getTree().exandPathTo(device);
		}
	}
}
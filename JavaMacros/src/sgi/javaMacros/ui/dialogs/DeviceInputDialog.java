package sgi.javaMacros.ui.dialogs;

import java.awt.HeadlessException;
import java.awt.Window;

import javax.swing.JPanel;

import sgi.gui.configuration.ISaveable;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.model.lists.DeviceSet;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.ModalConfigDialog;

public class DeviceInputDialog extends ModalConfigDialog implements ISaveable {

	private static final String MSGS_PREFIX = "sgi.javaMacros.ui.dialogs.DeviceInputDialog";
	private boolean addEndingButtons = true;

	protected boolean isAddEndingButtons() {
		return addEndingButtons;
	}

	protected void setAddEndingButtons(boolean addEndingButtons) {
		this.addEndingButtons = addEndingButtons;
	}

	public DeviceInputDialog() {
		super();
	}

	public DeviceInputDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}

	public void build(Device device) throws HeadlessException {

		Messages msgs = Messages.M;
		setTitle(msgs._$(MSGS_PREFIX + ".title"));
		setIconImage(msgs.getIcon());

		JPanel mainPanel = createDevicePanel(device, devicesNames);

		setContentPane(mainPanel);

		pack();
		setResizable(true);
	}

	public JPanel createDevicePanel(Device device, String[] deviceNames) {
		DeviceConfigPanelCreator creator = new DeviceConfigPanelCreator();
		creator.setMsgsPrefix(MSGS_PREFIX);

		if (!isKeySetVisible())
			creator.FieldsSettings.setInvisibleFields("label","keySet", "systemId", "detected", "handle","known");
		creator.setDevicesNames(deviceNames);
		creator.setTargetWindow(this);
		creator.setUseFieldSeparators(true);
		creator.setAddingEndButtons(device.getParent()== null ); //isUsingEndButtons());
		JPanel mainPanel = creator.createConfigPanel(device);
		return mainPanel;
	}

	private String[] devicesNames;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1846333374092233287L;

	public void setDeviceSet(DeviceSet devices, Device deviceToModify) {
		this.devicesNames = devices.nameSet(deviceToModify);

	}

	boolean keySetVisible = false;

	protected boolean isKeySetVisible() {
		return keySetVisible;
	}

	protected void setKeySetVisible(boolean keySetVisible) {
		this.keySetVisible = keySetVisible;
	}

}

package sgi.javaMacros.ui;

import sgi.javaMacros.ui.internal.AbstractTabledPanel;
import sgi.javaMacros.ui.viewmodels.DeviceTableModel;

public class DevicesPanel extends AbstractTabledPanel {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8869342590705490883L;

	public DevicesPanel(DeviceTableModel dataModel) {
		super(dataModel); 
		addButton("Fix Aliases", ActionCommands.FIX_ALIASES);
		addButton("Add Device", ActionCommands.ADD_DEVICE);
	}
	
	public DevicesPanel(){
		this( new DeviceTableModel()); 
	}

}
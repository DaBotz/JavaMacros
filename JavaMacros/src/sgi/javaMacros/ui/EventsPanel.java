package sgi.javaMacros.ui;

import sgi.javaMacros.ui.internal.AbstractTabledPanel;
import sgi.javaMacros.ui.viewmodels.EventsTableModel;

public class EventsPanel extends AbstractTabledPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8869342590705490883L;

	public EventsPanel(EventsTableModel dataModel) {
		super(dataModel); 
		addButton("Scan For New Events", ActionCommands.SCAN_FOR_NEW_EVENT);
	}
	
	public EventsPanel(){
		this( new EventsTableModel()); 
	}



}
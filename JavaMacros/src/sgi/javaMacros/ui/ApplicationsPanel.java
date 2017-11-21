package sgi.javaMacros.ui;

import javax.swing.table.TableModel;

import sgi.javaMacros.ui.internal.AbstractTabledPanel;
import sgi.javaMacros.ui.viewmodels.ApplicationTableModel;

public class ApplicationsPanel extends AbstractTabledPanel {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8869342590705490883L;

	public ApplicationsPanel(){
		this(new ApplicationTableModel ());
	}
	
	public ApplicationsPanel(ApplicationTableModel dataModel) {
		super((TableModel)dataModel); 
		super.addButton("Add Application", ActionCommands.ADD_APPLICATION);
	}

}
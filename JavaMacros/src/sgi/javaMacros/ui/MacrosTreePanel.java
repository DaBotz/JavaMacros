package sgi.javaMacros.ui;

import java.lang.reflect.Field;

import javax.swing.tree.TreePath;

import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.ui.internal.AbstractTreePanel;
import sgi.javaMacros.ui.tree.IEditabilityAssessor;

public class MacrosTreePanel extends AbstractTreePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8869342590705490883L;

	public MacrosTreePanel() {
		super();
		super.listensTo(JavaMacrosMemory.instance().getMacros());

		getTree().setEditabilityAssessor(new IEditabilityAssessor() {
			@Override
			public boolean isEditable(TreePath path, Field field) {
				String fname = field.getName();
				if ("name".equalsIgnoreCase(fname))
					return true;
				
				return false;
			}
		});

	}

	@Override
	protected Object getRootObject() {
		return JavaMacrosMemory.instance().getMacros().getFilteredList();
	}

}
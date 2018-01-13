package sgi.javaMacros.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.ui.dialogs.ApplicationListDialog;
import sgi.javaMacros.ui.internal.AbstractTreePanel;
import sgi.javaMacros.ui.tree.IEditabilityAssessor;

public class ApplicationsPanel extends AbstractTreePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8869342590705490883L;
	private Container applicationsPanel;

	public ApplicationsPanel() {
		super();
		listensTo(JavaMacrosMemory.instance().getApplications());

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
				// if (!(ctn.getUserObject() instanceof ApplicationForMacros))
				// return false;

				String fname = field.getName();
				if ("name".equalsIgnoreCase(fname))
					return true;
				if ("enabled".equalsIgnoreCase(fname))
					return true;

				return false;
			}
		});

		addButton("Edit", ActionCommands.MANAGE_DEVICES).addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton bt = (JButton) e.getSource();
				JScrollPane scp = getScrollPanel();
				Component view = scp.getViewport().getView();
				if (view == getTree()) {
					Container applicationsPanel = getApplicationsAslist();

					scp.getViewport().setView(applicationsPanel);
					bt.setText("Tree");
				} else {
					bt.setText("Edit");
					scp.getViewport().setView(getTree());
				}

			}
		});

	}

	public void expandMacrosPath(Macro macro) {

		if (!(macro.get___useCase() != null))
			getTree().exandPathTo(macro.get___useCase());
		else {
			if (macro.getApplication() == null)
				return;

			getTree().exandPathTo(macro.getApplication());
		}
	}

	@Override
	protected Object getRootObject() {
		return JavaMacrosMemory.instance().getApplicationSet().purgedCopy();
	}

	public Container getApplicationsAslist() {
		if (applicationsPanel == null) {

			ApplicationListDialog dsf = new ApplicationListDialog(null, ModalityType.MODELESS);
			dsf.loadApplications();
			applicationsPanel = dsf.getContentPane();
		}
		return applicationsPanel;
	}

}
package sgi.javaMacros.ui.dialogs;

import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sgi.generic.ui.GUIMemory;
import sgi.generic.ui.IMessages;
import sgi.generic.ui.ModalConfigDialog;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.internal.Application;
import sgi.javaMacros.model.lists.ApplicationSet;
import sgi.javaMacros.model.lists.ApplicationSet.ApplicationList;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.os.windows.ApplicationsRetriever;

public class ApplicationListDialog extends ModalConfigDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7711287359050448783L;

	public ApplicationListDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}

	@Override
	public void build(Object object, IMessages msgs) throws HeadlessException {
		super.build(object, msgs);
		setContentPane(new JScrollPane(getContentPane()));
		pack();
		GUIMemory.add(this);
		setResizable(true);
	}

	@Override
	public boolean doesCollectionAdmitRemovers(Object anObject, Field aField) {
		return false;
	}

	@Override
	protected JPanel createCollectionSingleLine(Field field, boolean addRemovers, JPanel p,
			boolean collectionAsHorizontalalList, Collection<?> q, GridLayout layout, ButtonGroup radios, Object node)
			throws IllegalAccessException {
		JPanel line = super.createCollectionSingleLine(field, addRemovers, p, collectionAsHorizontalalList, q, layout,
				radios, node);

		if (node instanceof Application) {
			final Application app = (Application) node;
			final ApplicationListDialog me = this;
			if (app.getName().trim().isEmpty())
				return new JPanel();

			JButton btn = new JButton(app.getBigIcon());
			line.add(btn);
			btn.setBackground(new Color(0, 0, 0, 0));

			btn.setOpaque(false);
			btn.setToolTipText(getMsgs()._$(this, "ClickToEdit"));
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (!app.isEnabled())
						return;

					ModalConfigDialog appConfig = new ApplicationModifyDialog(me, ModalityType.DOCUMENT_MODAL);
					app.getUseCases();
					appConfig.build(app, getMsgs());
					appConfig.autoPosition();
					appConfig.setVisible(true);
				}
			});
		}

		return line;
	}

	@Override
	protected JPanel createEndButtons() {
		super.createEndButtons();
		return null;
	}

	private class Capsule {

		@SuppressWarnings("unused")
		ApplicationList applications;

		public Capsule(ApplicationList applications) {
			this.applications = applications;
		}

	}

	public void loadApplications() {
		ApplicationSet allWindows = ApplicationsRetriever.getAllWindows();

		JavaMacrosMemory memory = JavaMacrosMemory.instance();
		ApplicationSet applicationSet = memory.getApplications().getSet();
		applicationSet.purge();
		applicationSet.addAll(allWindows);
		ApplicationList aslist = applicationSet.aslist();
		Collections.sort(aslist, new Comparator<Application>() {

			@Override
			public int compare(Application o1, Application o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		Iterator<Application> asIt = aslist.iterator();
		while (asIt.hasNext()) {
			Application application = (Application) asIt.next();
			if (application.getExeFile().contains("LuaMacros.exe"))
				asIt.remove();

		}
		build(new Capsule(aslist), Messages.M);

	}

	@Override
	protected void addRemover(JPanel p, Collection<?> q, GridLayout layout, Object node, JPanel p0,
			boolean collectionAsHorizontalalList) {
		// TODO Auto-generated method stub
		super.addRemover(p, q, layout, node, p0, collectionAsHorizontalalList);
	}

}
package sgi.javaMacros.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import sgi.configuration.ConfigurationAtom;
import sgi.gui.ComponentWalker;
import sgi.gui.IComponentModifier;
import sgi.gui.IElementNodeSelectionListener;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.lists.ApplicationSet;
import sgi.javaMacros.model.lists.ApplicationSet.ApplicationList;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.ModalConfigDialog;
import sgi.localization.IMessages;

public class ApplicationListDialog extends ModalConfigDialog {
	private static final String MSG_PREFIX = "sgi.javaMacros.ui.dialogs.ApplicationListDialog";
	/**
	 * 
	 */
	private static final long serialVersionUID = 7711287359050448783L;

	public ApplicationListDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}
	
	private HashSet<ApplicationListUpdater> disposables = new HashSet<>(); 
	@Override
	public void dispose() {
		for (ApplicationListUpdater disposed : disposables) {
			disposed.removeMe();
		}
		super.dispose();
	}

	private JComponent innerList;

	public void build(Capsule capsule, IMessages msgs) {

		setTitle(msgs._$(MSG_PREFIX + ".title"));
		setIconImage(msgs.getIcon());
		JavaMacrosPanelCreator creator = new JavaMacrosPanelCreator(msgs, MSG_PREFIX) {

			@Override
			protected JComponent createCollectionEditor(IAwareOfChanges anObject, Field field)
					throws IllegalArgumentException, IllegalAccessException {
				// TODO Auto-generated method stub
				innerList = super.createCollectionEditor(anObject, field);
				return innerList;
			}

			@Override
			protected JPanel createCollectionSingleLine(Field field, boolean addRemovers, JPanel p,
					boolean collectionAsHorizontalalList, Collection<IAwareOfChanges> q, GridLayout layout,
					final IAwareOfChanges node) throws IllegalAccessException {

				final JPanel panel = super.createCollectionSingleLine(field, addRemovers, p,
						collectionAsHorizontalalList, q, layout, node);

				PropertyChangeListener listener = new ApplicationListUpdater(panel, node);
				listener.propertyChange(null);
				node.addPropertyChangeListener(listener);

				return panel;
			}
		};

		creator.setUseFieldSeparators(true);
		creator.setCollectionElementsOpeningListeners(Capsule.class, "applications",
				new IElementNodeSelectionListener() {

					@Override
					public void handleSelection(Object target) {
						ApplicationModifyDialog modify = new ApplicationModifyDialog(getWindow(),
								ModalityType.APPLICATION_MODAL);
						modify.build((ApplicationForMacros) target, Messages.M);
						modify.autoPosition();
						modify.setVisible(true);

					}
				});

		creator.createConfigPanel(capsule);

		setContentPane(innerList);

		pack();

		setResizable(true);
	}

	class ApplicationListUpdater implements PropertyChangeListener {
		private final JPanel panel;
		private final IAwareOfChanges node;
		PropertyChangeListener me = this;
		boolean wasShown = false;

		ApplicationListUpdater(JPanel panel, IAwareOfChanges node) {
			this.panel = panel;
			this.node = node;
			disposables.add(this); 
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			
			if (panel.isDisplayable())
				wasShown = true;
			else if (wasShown)
				node.removePropertyChangeListener(me);

			panel.setOpaque(true);

			final ApplicationForMacros app = (ApplicationForMacros) node;

			new ComponentWalker(new IComponentModifier() {

				@Override
				public void modify(Component component) {

					if (component instanceof JButton) {
						JButton jb = (JButton) component;
						if (jb.getForeground().equals(Color.YELLOW)) {
							jb.setEnabled(!app.mayBePurged());
							jb.setVisible(app.isReal());
							return;
						} else
							component.setBackground(new Color(255, 255, 255, 0));
					} else if (app.isAbsent()) {
						component.setBackground(ConfigurationAtom.getAbsentColor());
					} else if (app.isEnabled()) {
						component.setBackground(ConfigurationAtom.getEnabledColor());
					} else if (!app.mayBePurged()) {
						component.setBackground(ConfigurationAtom.getDisabledColor());
					} else {
						component.setBackground(null);
					}

				}
			}).walk(panel);
			panel.repaint(50);
		}
		
		public void removeMe() {
			node.removePropertyChangeListener(me);
			
		}
	}

	public class Capsule extends ConfigurationAtom {
		ApplicationList applications;

		public Capsule(ApplicationList applications) {
			this.applications = applications;
		}
	}

	public void loadApplications() {
		JavaMacrosMemory memory = JavaMacrosMemory.instance();
		ApplicationSet applicationSet = memory.getApplications().getSet();
		applicationSet.purge();
		applicationSet.loadAppsFromSystem();
		ApplicationList aslist = applicationSet.aslist();
		// aslist.remove(ApplicationForMacros.getANY());
		Collections.sort(aslist, new Comparator<ApplicationForMacros>() {

			@Override
			public int compare(ApplicationForMacros o1, ApplicationForMacros o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		Iterator<ApplicationForMacros> asIt = aslist.iterator();

		while (asIt.hasNext()) {
			ApplicationForMacros application = (ApplicationForMacros) asIt.next();
			if (application.getExeFile().contains("LuaMacros.exe"))
				asIt.remove();

		}
		build(new Capsule(aslist), Messages.M);

		aslist.addPropertyChangeListener(new PropertyChangeListener() {
			PropertyChangeListener me = this;

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!ApplicationListDialog.this.isDisplayable())
					aslist.removePropertyChangeListener(me);
				if (evt.getOldValue() instanceof ApplicationForMacros) {

					memory.getApplications().getSet().remove(evt.getOldValue());
				}
			}
		});
	}

}
package sgi.javaMacros.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import sgi.configuration.ConfigurationAtom;
import sgi.gui.ComponentWalker;
import sgi.gui.IComponentModifier;
import sgi.gui.IElementNodeSelectionListener;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.gui.configuration.ISaveable;
import sgi.javaMacros.model.Devices;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.ui.ModalConfigDialog;
import sgi.localization.IMessages;

public class DeviceSetFrame extends ModalConfigDialog implements ISaveable {

	
	static class DeviceListUpdater implements PropertyChangeListener {
		private final JPanel panel;
		private final Device app;
	//	private final IAwareOfChanges node;
		PropertyChangeListener me= this;

		DeviceListUpdater(JPanel panel, Device app, IAwareOfChanges node) {
			this.panel = panel;
			this.app = app;
		//	this.node = node;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			
			new ComponentWalker(new IComponentModifier() {

				@Override
				public void modify(Component component) {

					if (component instanceof JButton) {
						JButton jb = (JButton) component;
						if (jb.getForeground().equals(Color.YELLOW))
							return;
						else
							component.setBackground(new Color(255, 255, 255, 0));
					} else

					if (!app.isDetected()) {
						component.setBackground(ConfigurationAtom.getAbsentColor());
					} else 
					if (app.isEnabled()) {
						component.setBackground(ConfigurationAtom.getEnabledColor());
					}
					else {
						component.setBackground(null);
					}

				}
			}).walk(panel);

		}

		public void removeMe() {

			app.removePropertyChangeListener(me);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 491577256130786882L;

	public DeviceSetFrame(Devices devices, IMessages msgs)
			throws HeadlessException {
		super(null, ModalityType.APPLICATION_MODAL);
		build(devices, msgs);

	}

	private HashSet<DeviceListUpdater> disposables = new HashSet<>(); 
	@Override
	public void dispose() {
		for (DeviceListUpdater disposed : disposables) {
			disposed.removeMe();
		}
		super.dispose();
	}

	protected JComponent collectionView;

	protected void build(final Devices devices, IMessages msgs) {
		setTitle(msgs._$(this.getClass(), "title"));
		setIconImage(msgs.getIcon());

		JavaMacrosPanelCreator creator = new JavaMacrosPanelCreator(msgs, "sgi.javaMacros.ui.dialogs.DeviceSetFrame",
				this) {

			@Override
			protected JComponent createCollectionEditor(IAwareOfChanges anObject, Field field)
					throws IllegalArgumentException, IllegalAccessException {
				collectionView = super.createCollectionEditor(anObject, field);
				return collectionView;
			}

			@Override
			protected JPanel createCollectionSingleLine(Field field, boolean addRemovers, JPanel p,
					boolean collectionAsHorizontalalList, Collection<IAwareOfChanges> q, GridLayout layout,
					IAwareOfChanges node) throws IllegalAccessException {

				final JPanel panel = super.createCollectionSingleLine(field, addRemovers, p,
						collectionAsHorizontalalList, q, layout, node);
				final Device app = (Device) node;
				panel.setOpaque(true);

				PropertyChangeListener listener = new DeviceListUpdater(panel, app, node);
				listener.propertyChange(null);
				node.addPropertyChangeListener(listener);

				return panel;
			}

		};

		creator.setCollectionElementsOpeningListeners(Devices.class, "set", new IElementNodeSelectionListener() {

			@Override
			public void handleSelection(Object target) {

				DeviceInputDialog dialog = new DeviceInputDialog(getWindow(), ModalityType.APPLICATION_MODAL);
				Device targetDevice = (Device) target;
				dialog.setDeviceSet(devices.getSet(),targetDevice);
				dialog.setAddEndingButtons(false);
				dialog.build(targetDevice);
				dialog.autoPosition();
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);

				// DeviceInputDialog jm = new NoButtonsDeviceDialog((Device)target,
				// ModalityType.APPLICATION_MODAL);
				// jm.setDeviceSet(DeviceSetFrame.this.javaMacrosController.getMemory().getDeviceSet());
				// jm.build(node, Messages.M);
				// jm.threadSafeShow();
			}
		});

		creator.createConfigPanel(devices);

		setResizable(true);
		// JScrollPane jsp = new JScrollPane(panel);
		// jsp.setPreferredSize(getContentPane().getPreferredSize());

		setContentPane(collectionView);
		pack();
	}

	// @Override
	// protected String[] noFields() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void setVisible(boolean b) {
	// setDefaultCloseOperation(HIDE_ON_CLOSE);
	// super.setVisible(b);
	// }
	//
	// @Override
	// protected JPanel createEndButtons() {
	// super.createEndButtons();
	// return null;
	// }
	//
	// @Override
	// public JComponent createBooleanEditor(Object obj, Field field) throws
	// IllegalAccessException {
	// return super.createBooleanEditor(obj, field);
	// }
	//
	// protected void addRemover(final JPanel p, final Collection<?> q, final
	// GridLayout layout, final Object node,
	// final JPanel p0, final boolean collectionAsHorizontalalList) {
	// JButton btn = new JButton("X");
	//
	// final DeviceSetFrame me = this;
	// layout.setVgap(2);
	// final Device dev = ((Device) node);
	//
	// btn.addActionListener(new ActionListener() {
	//
	// @Override
	// public void actionPerformed(final ActionEvent e) {
	// q.remove(node);
	// p.remove(p0);
	// if (collectionAsHorizontalalList)
	// layout.setColumns(layout.getColumns() - 1);
	// else
	// layout.setRows(layout.getRows() - 1);
	// p.validate();
	// p.repaint();
	//
	// }
	// });
	//
	// final JPanel dual = new JPanel();
	// String cn = getClass().getCanonicalName();
	//
	// JButton comp = new JButton(Messages.M._$(cn + ".edit"));
	// comp.addActionListener(new ActionListener() {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// final String originalName = "" + dev.getName();
	// DeviceInputDialog jm = new NoButtonsDeviceDialog(me,
	// ModalityType.APPLICATION_MODAL);
	// jm.setDeviceSet(DeviceSetFrame.this.javaMacrosController.getMemory().getDeviceSet());
	// jm.build(node, Messages.M);
	// jm.threadSafeShow();
	// jm.addComponentListener(new ComponentAdapter() {
	// @Override
	// public void componentHidden(ComponentEvent e) {
	// {
	// String name = dev.getName();
	// decor(dev, dual);
	// DeviceSetFrame.this.javaMacrosController.checkForDevicesUpdate();
	// if (!originalName.equals(name)) {
	// Component[] components = dual.getParent().getComponents();
	// for (int i = 0, k = 1; k < components.length; i++, k++) {
	// Component component = components[i];
	//
	// if (components[k] == dual && component instanceof JLabel) {
	// final JLabel lbl = (JLabel) component;
	// lbl.setText(name);
	// lbl.setBackground(Color.ORANGE);
	// lbl.setOpaque(true);
	// new Timer(30000, new ActionListener() {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// lbl.setBackground(null);
	// lbl.setOpaque(false);
	// ((Timer) e.getSource()).stop();
	// }
	// }).start();
	// }
	//
	// }
	// }
	// }
	// }
	// });
	// jm.setUsingRecursiveFieldScan(true);
	//
	// }
	// });
	// btn.setBackground(new Color(200, 0, 0));
	// comp.setBackground(new Color(200, 140, 0));
	//
	// styleDeviceSetButton(btn);
	// styleDeviceSetButton(comp);
	//
	// dual.add(comp);
	// dual.add(btn);
	// comp.setToolTipText(Messages.M._$(cn + ".edit.tooltip"));
	// btn.setToolTipText(Messages.M._$(getClass().getCanonicalName() +
	// ".erase.tooltip"));
	//
	// dual.setOpaque(true);
	//
	// decor(dev, dual);
	//
	// p0.add(dual);
	//
	// }
	//
	// protected void decor(Device dev, JPanel dual) {
	// if (dual.getParent() != null && dual.getParent() instanceof JPanel) {
	// dual = (JPanel) dual.getParent();
	//
	// }
	//
	// dual.setBackground(!dev.isIgnored() ? (dev.isAutonomousNumLock() ?
	// Color.YELLOW : null) : Color.GREEN);
	// }
	//
	// protected void styleDeviceSetButton(JButton btn) {
	// btn.setForeground(new Color(255, 255, 0));
	// btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	// btn.setPreferredSize(new Dimension(16, 16));
	// }

}
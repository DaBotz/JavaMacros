package sgi.javaMacros.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import sgi.generic.serialization.AbstractMemoryRoot;
import sgi.generic.ui.GUIMemory;
import sgi.generic.ui.IMessages;
import sgi.generic.ui.ModalConfigDialog;
import sgi.javaMacros.controller.JavaMacrosController;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.msgs.Messages;

public class DeviceSetFrame extends ModalConfigDialog {

	/**
	 * 
	 */
	private final JavaMacrosController javaMacrosController;

	/**
	 * 
	 */
	private static final long serialVersionUID = 491577256130786882L;

	public DeviceSetFrame(JavaMacrosController javaMacrosController, AbstractMemoryRoot configuration, IMessages msgs) throws HeadlessException {
		super(null, ModalityType.TOOLKIT_MODAL);
		this.javaMacrosController = javaMacrosController;
		build(configuration, msgs);
		setResizable(true);
		JScrollPane jsp = new JScrollPane(getContentPane());
		jsp.setPreferredSize(getContentPane().getPreferredSize());

		setContentPane(jsp);
		pack();
		GUIMemory.add(this);

	}

	@Override
	protected String[] noFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVisible(boolean b) {
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		super.setVisible(b);
	}

	@Override
	protected JPanel createEndButtons() {
		super.createEndButtons();
		return null;
	}
	
	@Override
	public JComponent createBooleanEditor(Object obj, Field field) throws IllegalAccessException {
		return super.createBooleanEditor(obj, field);
	}

	protected void addRemover(final JPanel p, final Collection<?> q, final GridLayout layout, final Object node,
			final JPanel p0, final boolean collectionAsHorizontalalList) {
		JButton btn = new JButton("X");

		final DeviceSetFrame me = this;
		layout.setVgap(2);
		final Device dev = ((Device) node);

		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				q.remove(node);
				p.remove(p0);
				if (collectionAsHorizontalalList)
					layout.setColumns(layout.getColumns() - 1);
				else
					layout.setRows(layout.getRows() - 1);
				p.validate();
				p.repaint();

			}
		});

		final JPanel dual = new JPanel();
		String cn = getClass().getCanonicalName();

		JButton comp = new JButton(Messages.M._$(cn + ".edit"));
		comp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String originalName = "" + dev.getName();
				DeviceInputDialog jm = new NoButtonsDeviceDialog(me, ModalityType.APPLICATION_MODAL);
				jm.setDeviceSet(DeviceSetFrame.this.javaMacrosController.getMemory().getDeviceSet());
				jm.build(node, Messages.M);
				jm.threadSafeShow();
				jm.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentHidden(ComponentEvent e) {
						{
							String name = dev.getName();
							decor(dev, dual);
							DeviceSetFrame.this.javaMacrosController.checkForDevicesUpdate(); 
							if (!originalName.equals(name)) {
								Component[] components = dual.getParent().getComponents();
								for (int i = 0, k = 1; k < components.length; i++, k++) {
									Component component = components[i];

									if (components[k] == dual && component instanceof JLabel) {
										final JLabel lbl = (JLabel) component;
										lbl.setText(name);
										lbl.setBackground(Color.ORANGE);
										lbl.setOpaque(true);

										new Thread(new Runnable() {

											@Override
											public void run() {
												try {
													Thread.sleep(3000);
													SwingUtilities.invokeLater(new Runnable() {

														@Override
														public void run() {
															lbl.setBackground(null);

															lbl.setOpaque(false);

														}
													});
												} catch (InterruptedException e) {
												}
											}
										}).start();
									}

								}
							}
						}
					}
				});
				jm.setUsingRecursiveFieldScan(true);

			}
		});
		btn.setBackground(new Color(200, 0, 0));
		comp.setBackground(new Color(200, 140, 0));

		styleDeviceSetButton(btn);
		styleDeviceSetButton(comp);

		dual.add(comp);
		dual.add(btn);
		comp.setToolTipText(Messages.M._$(cn + ".edit.tooltip"));
		btn.setToolTipText(Messages.M._$(getClass().getCanonicalName() + ".erase.tooltip"));

		dual.setOpaque(true);

		decor(dev, dual);

		p0.add(dual);

	}

	protected void decor(Device dev, JPanel dual) {
		if (dual.getParent() != null && dual.getParent() instanceof JPanel) {
			dual = (JPanel) dual.getParent();

		}

		dual.setBackground(!dev.isIgnored() ? (dev.isAutonomousNumLock() ? Color.YELLOW : null) : Color.GREEN);
	}

	protected void styleDeviceSetButton(JButton btn) {
		btn.setForeground(new Color(255, 255, 0));
		btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		btn.setPreferredSize(new Dimension(16, 16));
	}

}
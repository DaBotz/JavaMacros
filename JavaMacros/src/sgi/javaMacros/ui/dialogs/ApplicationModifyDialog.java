package sgi.javaMacros.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import sgi.configuration.ConfigurationAtom;
import sgi.gui.ComponentWalker;
import sgi.gui.GUIMemory2;
import sgi.gui.IComponentModifier;
import sgi.gui.IElementNodeSelectionListener;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.gui.configuration.ISaveable;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.internal.AutomaticUseCase;
import sgi.javaMacros.model.internal.AutomaticUseCaseOnTitle;
import sgi.javaMacros.model.internal.ManualUseCase;
import sgi.javaMacros.model.internal.UseCase;
import sgi.javaMacros.model.internal.UseCases;
import sgi.javaMacros.model.internal.defaults.BasicUseCase;
import sgi.javaMacros.model.lists.ApplicationSet;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.ModalConfigDialog;
import sgi.localization.IMessages;

public class ApplicationModifyDialog extends ModalConfigDialog implements ISaveable {
	class AppComboListener implements ItemListener, ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			populateUCaseCombo();
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			populateUCaseCombo();
		}

		public void populateUCaseCombo() {
			UseCases useCases2 = appCombo.getItemAt(appCombo.getSelectedIndex()).getUseCases();
			UseCase[] a = new UseCase[useCases2.size()];
			useCases2.toArray(a);
			uCaseCombo.setModel(new DefaultComboBoxModel<>(a));
			uCaseCombo.setSelectedItem(null);
		}
	}

	public class ButtonOpener implements ActionListener {

		private int type;

		public ButtonOpener(UseCases useCases, int b) {
			this.type = b;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String name = "----";
			UseCase newCase = null;
			switch (type) {

			case 0:
				newCase = new ManualUseCase(name);
				break;
			case 1:
				newCase = new AutomaticUseCase(name);
				break;
			case 2:
				newCase = new AutomaticUseCaseOnTitle(name);
				break;
			}

			launchUseCase(newCase);

		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7800182944589046156L;
	private JComboBox<ApplicationForMacros> appCombo;

	private JComboBox<UseCase> uCaseCombo;

	private UseCases useCases;

	public ApplicationModifyDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}

	public void build(final ApplicationForMacros applicationForMacros, IMessages msgs) throws HeadlessException {

		this.useCases = applicationForMacros.getUseCases();
		setTitle(msgs.getString("ApplicationModifyDialog.title"));

		ButtonOpener manualOpener = new ButtonOpener(useCases, 0);
		ButtonOpener automaticOpener = new ButtonOpener(useCases, 1);
		ButtonOpener automaticOpener1 = new ButtonOpener(useCases, 2);

		if (useCases.isEmpty()) {
			useCases.add(new BasicUseCase());
		}

		JavaMacrosPanelCreator creator = new JavaMacrosPanelCreator(msgs) {

			private JPanel collectionHolder;

			@Override
			protected JComponent createCollectionEditor(IAwareOfChanges anObject, Field field)
					throws IllegalArgumentException, IllegalAccessException {
				JPanel encloser = new JPanel(new BorderLayout(0, 3));
				JComponent collectionScroll = super.createCollectionEditor(anObject, field);

				encloser.add(collectionScroll, BorderLayout.CENTER);
				JPanel southern = new JPanel(new GridLayout(4, 1, 0, 5));
				southern.add(new JLabel("Add user case: "));
				JButton comp = new JButton("Manually Activated");
				JButton comp2 = new JButton("Auto-actived, on win class");
				JButton comp3 = new JButton("Auto-actived, on win caption");

				styleButton(comp);
				styleButton(comp2);
				styleButton(comp3);

				southern.add(comp);
				southern.add(comp2);
				southern.add(comp3);
				// southern.add(new JLabel(" "));

				comp.addActionListener(manualOpener);
				comp2.addActionListener(automaticOpener);
				comp3.addActionListener(automaticOpener1);

				Dimension pSize = collectionScroll.getPreferredSize();
				pSize.height += 10;
				encloser.setMinimumSize((Dimension) pSize.clone());
				pSize.height += 100;
				encloser.setPreferredSize((Dimension) pSize.clone());
				pSize.height += 300;
				
				encloser.setMaximumSize((Dimension) pSize.clone());
				encloser.add(southern, BorderLayout.SOUTH);

				return encloser;
			}

			@Override
			protected JPanel createCollectionSingleLine(Field field, boolean addRemovers, JPanel p,
					boolean collectionAsHorizontalalList, Collection<IAwareOfChanges> q, GridLayout layout,
					final IAwareOfChanges node) throws IllegalAccessException {

				if (collectionHolder == null)
					collectionHolder = p;

				final JPanel createCollectionSingleLine = super.createCollectionSingleLine(field, true, p,
						collectionAsHorizontalalList, q, layout, node);

				PropertyChangeListener listener = new PropertyChangeListener() {

					PropertyChangeListener me = this;
					final UseCase uc = (UseCase) node;
					ComponentWalker componentWalker = new ComponentWalker(new IComponentModifier() {

						@Override
						public void modify(Component component) {
							boolean b = !uc.isBasic();

							if (component instanceof JCheckBox && !b) {
								component.setVisible(b);
							} else if (component instanceof JButton) {
								JButton jb = (JButton) component;
								if (jb.getForeground().equals(Color.YELLOW)) {
									jb.setEnabled(b);
									return;
								} else
									((JButton) component).setContentAreaFilled(false); // .setBackground(new Color(255,
																						// 255, 255, 0));
							} else {
								component.setBackground(uc.isEnabled() ? ConfigurationAtom.getEnabledColor()
										: ConfigurationAtom.getDisabledColor());

							}
						}
					});

					{
						createCollectionSingleLine.setOpaque(true);
						componentWalker.walk(createCollectionSingleLine);
					}

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (!createCollectionSingleLine.isDisplayable())
							node.removePropertyChangeListener(me);

						componentWalker.walk(createCollectionSingleLine);

					}
				};

				node.addPropertyChangeListener(listener);

				return createCollectionSingleLine;
			}

			@Override
			protected JPanel createEndButtons() {
				GridBagLayout gbl = new GridBagLayout();
				gbl.rowHeights = new int[] { //
						getSpacerHeight() + getSpacerHeight(), //
						getOccupiedRowHeight(), //
						getSpacerHeight(), //
						getSpacerHeight(), //
				};

				gbl.rowWeights = new double[gbl.rowHeights.length];
				gbl.rowWeights[gbl.rowWeights.length - 1] = 1;

				gbl.columnWidths = new int[] { 5, 100, 10, 100, 5, 50, 5 };

				gbl.columnWeights = new double[] { 0, 1, 0, 1, 0, 0.2, 0 };

				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 1;
				gbc.gridy = 1;
				gbc.anchor = GridBagConstraints.SOUTHEAST;
				gbc.fill = GridBagConstraints.HORIZONTAL;

				JPanel endPanel = new JPanel(gbl);

				TitledBorder titleBorder = BorderFactory
						.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
				String prefix = getMsgsPrefix() + ".importMacrosPanel";

				titleBorder.setTitle(Messages.M.getString(prefix + ".title"));

				endPanel.setBorder(titleBorder);

				ApplicationForMacros[] array = getClonableApps(applicationForMacros);
				appCombo = new JComboBox<ApplicationForMacros>(array);
				uCaseCombo = new JComboBox<>();

				AppComboListener aListener = new AppComboListener();
				appCombo.addItemListener(aListener);
				appCombo.addActionListener(aListener);

				JButton button = new JButton(Messages.M.getString(prefix + ".button"));
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							JavaMacrosMemory.instance().getMacros().copyFromAppToApp(//
									appCombo.getItemAt(appCombo.getSelectedIndex()), //
									(UseCase) uCaseCombo.getSelectedItem(), applicationForMacros);
						} catch (InstantiationException | IllegalAccessException e1) {
							e1.printStackTrace();
						}

					}
				});

				endPanel.add(appCombo, gbc);
				gbc.gridx += 2;
				endPanel.add(uCaseCombo, gbc);
				gbc.gridx += 2;

				gbc.fill = GridBagConstraints.NONE;
				endPanel.add(button, gbc.clone());
				if (appCombo.getItemCount() > 1) {
					appCombo.setSelectedIndex(1);
					appCombo.setSelectedIndex(0);
				}
				gbc.gridx = 1;
				gbc.gridy++;
				gbc.fill = GridBagConstraints.NONE;
				gbc.anchor = GridBagConstraints.NORTHEAST;
				JLabel lbl = new JLabel(Messages.M.getString(prefix + ".labels.apps"));
				Font dFont = lbl.getFont().deriveFont(8).deriveFont(Font.ITALIC);
				lbl.setFont(dFont);
				gbc.gridx = 1;
				gbc.gridy++;
				endPanel.add(lbl, gbc);

				lbl = new JLabel(Messages.M.getString(prefix + ".labels.useCases"));
				lbl.setFont(dFont);
				gbc.gridx += 2;
				endPanel.add(lbl, gbc);
				return endPanel;
			}

			protected void fixEndButtonsPosition(GridBagConstraints btnGb) {
				btnGb.gridwidth++;
				btnGb.gridx = 1;
				btnGb.gridy -= 4;
				btnGb.gridheight += 6;

				btnGb.fill = GridBagConstraints.BOTH;
			}

			@Override
			protected void styleButton(JButton btn) {
				btn.setContentAreaFilled(false);
			}

		};
		creator.setUseFieldSeparators(true);
		creator.setAddingEndButtons(true);
		creator.setCollectionElementsOpeningListeners(ApplicationForMacros.class, "useCases",
				new IElementNodeSelectionListener() {

					@Override
					public void handleSelection(Object target) {

						UseCaseConfigDialog useCaseDlg = new UseCaseConfigDialog(ApplicationModifyDialog.this,
								ModalityType.APPLICATION_MODAL);
						useCaseDlg.setUsingEndButtons(false);
						useCaseDlg.build((UseCase) target);
						useCaseDlg.autoPosition();
						useCaseDlg.setVisible(true);
						// System.out.println(target);
					}
				});

		creator.setUsingRecursiveFieldScan(true);

		creator.setMsgsPrefix("sgi.javaMacros.ui.dialogs.ApplicationModifyDialog");
		creator.FieldsSettings.setInvisibleFields(// "enabled",//
				"exeFile", //
				"currentUseCaseCreationTime", "currentUseCase", //
				"windowClasses" //
		);
		creator.setTargetWindow(this);
		creator.setMainPageColumnWidths(new int[] { //
				10, //
				50, //
				10, //
				70, //
				10 });

		JPanel createdConfigPanel = creator.createConfigPanel(applicationForMacros);
		GridBagLayout layout = (GridBagLayout) createdConfigPanel.getLayout();
		layout.rowHeights = new int[] { 0, 2, 2, 2, 2, 22, 2, 2, 22, 2, 2, 22, 2, 2, 220, 2, 10, 10, 10, 22, 22, 2, 2,
				10 };
		layout.rowWeights = new double[layout.rowHeights.length];
		for (int i = 0; i < layout.rowHeights.length; i++) {
			int h = layout.rowHeights[i];
			if (h >= 200)
				layout.rowWeights[i] = 10.0;
			else
				switch (h) {
				case 0:
				case 2:
				case 22:

					break;
				default:
				//	layout.rowWeights[i]=2;
				}
		}

		// layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10,
		// 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		setContentPane(createdConfigPanel);
		pack();
		setMaximumSize(getSize());
	}

	public ApplicationForMacros[] getClonableApps(final ApplicationForMacros applicationForMacros) {
		ApplicationSet pCopy = JavaMacrosMemory.instance().getApplicationSet().purgedCopy();
		ArrayList<ApplicationForMacros> filtered = new ArrayList<>();
		for (ApplicationForMacros pCopied : pCopy) {
			if (applicationForMacros != pCopied //
					&& pCopied.isReal() && ( // Debug.isDeBug() ||
					pCopied.doesHaveMacros())

			) {
				filtered.add(pCopied);
			}
		}

		ApplicationForMacros[] array = new ApplicationForMacros[filtered.size()];
		filtered.toArray(array);
		return array;
	}

	protected UseCases getUseCases() {
		return useCases;
	}

	public void launchUseCase(final UseCase newCase) {
		final UseCaseConfigDialog useCaseDlg = new UseCaseConfigDialog(ApplicationModifyDialog.this,
				ModalityType.APPLICATION_MODAL);
		useCaseDlg.setUsingEndButtons(true);
		useCaseDlg.build(newCase);
		GUIMemory2.add(useCaseDlg);
		useCaseDlg.autoPosition();

		new Thread(new Runnable() {

			@Override
			public void run() {
				useCaseDlg.showAndLock();
				if (useCaseDlg.isSaved()) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							getUseCases().add(newCase);
						}
					});
				}

			}
		}).start();
	}

}
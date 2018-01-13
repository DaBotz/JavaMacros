package sgi.javaMacros.model.macros.execution.executors;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import sgi.gui.ConfigPanelCreator;
import sgi.gui.RenderingType;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.JavaMacrosConfiguration;
import sgi.javaMacros.model.enums.ModifierMasks;
import sgi.javaMacros.model.macros.execution.Executor;
import sgi.javaMacros.model.macros.sendkeys.KeyCodeChecker;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.ModifierMasksGroup;

public class KEY_ALIAS extends Executor implements PropertyChangeListener, ActionListener {

	private transient ModifierMasksGroup mmGroup;
	private int modifiersMask = 0;
	private int keyAliasScanCode = 0;
	private transient ModifierMasks[] modifiers = ModifierMasks.values(true);
	private transient Timer timer; 

	@Override
	public Component getInputGUI() {
		mmGroup = new ModifierMasksGroup(true);
		mmGroup.setModifierMask(modifiersMask);
		mmGroup.addPropertyChangeListener(this);

		ConfigPanelCreator creator = new ConfigPanelCreator(Messages.M, getClass().getName()) {
			@Override
			protected JComponent createIntEditor(IAwareOfChanges obj, Field field) throws IllegalAccessException {
				if ("modifiersMask".equals(field.getName()))
					return mmGroup.getPanel();
				JPanel panel = (JPanel) super.createIntEditor(obj, field);
				final JTextField txtf = new JTextField();
				KEY_ALIAS.this.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("keyAliasScanCode".equalsIgnoreCase(evt.getPropertyName())) {
							txtf.setText(KeyEvent.getKeyText(getKeyAliasScanCode()));
						}
					}
				});

				Component[] cts = panel.getComponents();
				panel.removeAll();
				panel.add(txtf);
				for (Component ct : cts) {
					panel.add(ct);
				}

				txtf.setColumns(12);
				txtf.addKeyListener(new KeyAdapter() {

					@Override
					public void keyTyped(KeyEvent e) {
						txtf.setText("");
					}

					@Override
					public void keyReleased(KeyEvent e) {
						setKeyAliasScanCode(KeyCodeChecker.getAcceptableKeyCode(e));
					}

					@Override
					public void keyPressed(KeyEvent e) {
						setKeyAliasScanCode(KeyCodeChecker.getAcceptableKeyCode(e));
					}
				});
				txtf.setBackground(Color.orange);

				return panel;
			}
		};
		creator.DefaultSetters.setDefaultComboBoxWidth(60);
		creator.DefaultSetters.setDefaultEnumRenderingType(RenderingType.HORIZONTAL_RADIOBUTTONS);
		creator.setAddingEndButtons(false);
		creator.setUseFieldSeparators(true);
		JPanel basePanel = creator.createConfigPanel(this);

		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() == this && "modifiersMask".equals(evt.getPropertyName())) {
					mmGroup.setModifierMask(modifiersMask);
				}
			}
		});

		return basePanel;
	}

	private transient boolean firstDown= true; 
	@Override
	public int execute(LuaEvent event) {
		try {
			if (RobotAction.getRobotx() == null)
				return FAIL;
			if(timer== null) timer = 
					new Timer(JavaMacrosConfiguration.instance().getDelayBetweenKeys(), this);
		} catch (AWTException e) {
			e.printStackTrace();
			return FAIL;
		}

		if (event.isDown()) {
			if( firstDown ) {
				firstDown= false; 
				this.actionPerformed(null);
			}
			timer.restart();
		} else {
			firstDown= true;
			timer.stop();
			release();
		}

		return COMPLETE;
	}

	protected void press() {
		for (int i = 0; i < modifiers.length; i++) {
			ModifierMasks mm = modifiers[i];
			if (mm.isSet(modifiersMask)) {
				RobotAction.keyPress(mm.getScanCode());
			}
		}
		try {
			RobotAction.keyPress(getKeyAliasScanCode());
		} catch (Exception e) {
			RobotAction.keyPress(getKeyAliasBasicScanCode());
			
		}
	}

	private transient int basicScanCode=-1; 
	
	private int getKeyAliasBasicScanCode() {
		if( basicScanCode ==-1) {	}
		
		return basicScanCode;
	}

	protected void release() {
		RobotAction.keyRelease(getKeyAliasScanCode());
		for (int i = modifiers.length - 1; i >= 0; i--) {
			ModifierMasks mm = modifiers[i];
			if (mm.isSet(modifiersMask)) {
				RobotAction.keyRelease(mm.getScanCode());
			}
		}
	}



	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int oldMM = this.modifiersMask;
		this.modifiersMask = mmGroup.getModifierMask();
		notifyPropertyChange("modifiersMask", oldMM, this.modifiersMask);
	}

	@Override
	public Executor copyMe() {

		KEY_ALIAS copy = new KEY_ALIAS();
		copy.keyAliasScanCode = keyAliasScanCode;
		copy.modifiersMask = modifiersMask;
		return copy;
	}

	public int getKeyAliasScanCode() {
		return keyAliasScanCode;
	}

	public void setKeyAliasScanCode(int scanCode) {
		int scanCode2 = this.keyAliasScanCode;
		this.keyAliasScanCode = scanCode;
		notifyPropertyChange("keyAliasScanCode", scanCode2, scanCode);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		press();
	}

}

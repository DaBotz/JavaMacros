package sgi.javaMacros.model.macros.execution.executors;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import sgi.gui.ConfigPanelCreator;
import sgi.gui.RenderingType;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.enums.ModifierMasks;
import sgi.javaMacros.model.enums.executors.VirtualModifierModes;
import sgi.javaMacros.model.enums.executors.VirtualModifierScopes;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.model.macros.execution.Executor;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;
import sgi.javaMacros.msgs.Messages;
import sgi.javaMacros.ui.ModifierMasksGroup;

public class VIRTUAL_MODIFIER extends Executor implements PropertyChangeListener, ActionListener {

	private static int threshold = 3;
	private static final int UNLIMITED = 0;

	public static int getThreshold() {
		return threshold;
	}

	public static void setThreshold(int threshold) {
		VIRTUAL_MODIFIER.threshold = threshold;
	}

	private transient ApplicationForMacros _myApp;
	private transient ModifierMasksGroup mmGroup;
	private transient Timer timer;
	private transient JComponent shelfLifeIntSpinner;
	private transient JComponent shelfLifeLabel;

	private int modifierLifeDuration = UNLIMITED;
	private int modifiersMask = 0;
	private VirtualModifierModes mode = VirtualModifierModes.PRESS_AND_RELEASE;
	private VirtualModifierScopes scope = VirtualModifierScopes.SYSTEM_MODIFIER;

	private transient boolean toggleState = false;

	@Override
	public Executor copyMe() {

		VIRTUAL_MODIFIER copy = new VIRTUAL_MODIFIER();
		copy.mode = this.mode;
		copy.scope = scope;
		copy.modifiersMask = modifiersMask;
		return copy;
	}

	@Override
	public int execute(LuaEvent event) {
		switch (mode) {
		case PRESS_AND_RELEASE:
			toggleState = event.isDown();
			break;

		case TOGGLE_STATE:
			if (event.isDown())
				return PASS;
			toggleState = !toggleState;
			if (modifierLifeDuration > 0) {
				setTimedRelease();
			}
			break;
		}

		return doSetModifiers();
	}

	public void setTimedRelease() {
		if (toggleState) {
			if (timer == null) {
				timer = new Timer(delaySecsAsMillis(), this);
				timer.setRepeats(false);
			} else
				timer.setDelay(delaySecsAsMillis());

			timer.restart();
		} else if (!toggleState && timer != null)
			timer.stop();
	}

	public int delaySecsAsMillis() {
		return modifierLifeDuration << 10;
	}

	public int doSetModifiers() {
		switch (scope) {
		case JAVAMACROS_APPLICATION_MODIFIER:
			return updateModifiers(getMyApplication());
		case SYSTEM_MODIFIER:
			updateModifiers(getMyApplication());
			updateModifiers(ApplicationForMacros.getANY());
			return systemWideAction();
		}

		return FAIL;
	}

	private void finMydApp() {

		Macro macro = getMacro();
		if (macro != null)
			_myApp = macro.getApplication();
	}

	public long getDuration() {
		return modifierLifeDuration;
	}

	@Override
	public Component getInputGUI() {
		mmGroup = new ModifierMasksGroup(true);
		mmGroup.setModifierMask(modifiersMask);
		mmGroup.addPropertyChangeListener(this);

		ConfigPanelCreator creator = new ConfigPanelCreator(Messages.M, getClass().getName()) {
			@Override
			protected JComponent getLabel(String name) {
				JComponent label = super.getLabel(name);

				if ("modifierLifeDuration".equals(name)) {
					shelfLifeLabel = label;
				}
				return label;
			}

			@Override
			protected JComponent createIntEditor(IAwareOfChanges obj, Field field) throws IllegalAccessException {
				if ("modifiersMask".equals(field.getName()))

					return mmGroup.getPanel();

				shelfLifeIntSpinner = super.createIntEditor(obj, field);
				return shelfLifeIntSpinner;
			}
		};

		creator.DefaultSetters.setDefaultComboBoxWidth(60);
		creator.DefaultSetters.setDefaultEnumRenderingType(RenderingType.HORIZONTAL_RADIOBUTTONS);
		creator.setAddingEndButtons(false);
		creator.setUseFieldSeparators(true);
		JPanel basePanel = creator.createConfigPanel(this);

		shelfLifeLabel.setVisible(mode == VirtualModifierModes.TOGGLE_STATE);
		shelfLifeIntSpinner.setVisible(mode == VirtualModifierModes.TOGGLE_STATE);

		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() == VIRTUAL_MODIFIER.this) {
					if ("modifiersMask".equals(evt.getPropertyName())) {
						mmGroup.setModifierMask(modifiersMask);
					}
					shelfLifeLabel.setVisible(mode == VirtualModifierModes.TOGGLE_STATE);
					shelfLifeIntSpinner.setVisible(mode == VirtualModifierModes.TOGGLE_STATE);
				}

			}

		});

		return basePanel;
	}

	private ApplicationForMacros getMyApplication() {
		if (_myApp == null)
			finMydApp();

		return _myApp;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int old = this.modifiersMask;
		this.modifiersMask = mmGroup.getModifierMask();
		notifyPropertyChange("modifiersMask", old, this.modifiersMask);
	}

	public void setModifierLifeDuration(int duration) {

		final long old = this.modifierLifeDuration;
		if (duration < threshold) {
			if (duration - old > 0)
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						modifierLifeDuration = threshold;
						notifyPropertyChange("modifierLifeDuration", old, modifierLifeDuration);
					}
				});
			else
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						modifierLifeDuration = UNLIMITED;
						notifyPropertyChange("modifierLifeDuration", old, modifierLifeDuration);
					}
				});

		}

		this.modifierLifeDuration = duration;
		notifyPropertyChange("modifierLifeDuration", old, duration);
	}

	private int systemWideAction() {
		try {
			RobotAction.getRobotx();
			ModifierMasks[] values = ModifierMasks.values();
			for (ModifierMasks mm : values) {
				int scanCode = mm.getScanCode();
				if (scanCode != 0 && mm.isSet(modifiersMask)) {
					if (toggleState) {
						RobotAction.keyPress(scanCode);
					} else {
						RobotAction.keyRelease(scanCode);
					}
				}
			}
		} catch (AWTException e) {
			e.printStackTrace();
			return FAIL;
		}
		return COMPLETE;
	}

	protected int updateModifiers(ApplicationForMacros application) {
		if (application == null)
			return FAIL;
		int appMask = application.getModifiersMask();
		if (toggleState) {
			appMask = appMask | modifiersMask;
		} else {
			appMask = appMask & (modifiersMask ^ 0xffff);
		}
		application.setModifiersMask(appMask);
		return COMPLETE;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		toggleState= false; 
		doSetModifiers(); 
	}

}

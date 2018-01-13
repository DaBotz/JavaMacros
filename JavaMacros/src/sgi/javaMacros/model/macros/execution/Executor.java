package sgi.javaMacros.model.macros.execution;

import java.awt.Component;

import sgi.configuration.ConfigurationAtom;
import sgi.configuration.IConfigurationAtom;
import sgi.generic.debug.Debug;
import sgi.gui.ConfigPanelCreator;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.enums.ActionType;
import sgi.javaMacros.model.internal.AutoCopier;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.msgs.Messages;

public abstract class Executor extends ConfigurationAtom implements AutoCopier<Executor> {

	public static final int FAIL = 0;
	public static final int PASS = 1;
	public static final int REQUIRES_OSD = 2;
	public static final int COMPLETE = 4;

 

	public static Executor getExecuteImplementation(Macro macro, ActionType actionType)  {

		Executor impl = null;
		String capitalize = actionType.name();//StringUtils.decamelizeExe(actionType.name(), true);
		String className = "sgi.javaMacros.model.macros.execution.executors." + capitalize;
		try {

			impl = (Executor) Class.forName(className).newInstance();
			impl.setParent(macro);

		} catch (Exception e) {
			impl = new UnimplementedActionTYpeExecutor(className, capitalize);
			Debug.info(e, 5);
		}

		return impl;
	}

	public boolean isUnimplemented() {
		return false;
	}

	public Component getInputGUI() {
		ConfigPanelCreator creator = getPanelCreator();
		creator.setUseFieldSeparators(true);
		creator.setAddingEndButtons(false);

		return creator.createConfigPanel(this);
	}

	protected ConfigPanelCreator getPanelCreator() {
		ConfigPanelCreator creator = new ConfigPanelCreator(Messages.M, getClass().getName());
		return creator;
	}

	public abstract int execute(LuaEvent event);

	public void mount() {

	}

	public void unmount() {

	}

	public abstract Executor copyMe();

	public boolean usesDirectCode() {
		return false;
	}

	public String getDirectCode() {
		return "";
	}

	private transient Macro macro;

	public Macro getMacro() {
		if (macro == null) {
			IConfigurationAtom parent = this;
			do {
				parent = parent.getParent();
			} while (parent != null && !(parent instanceof Macro));

			if (parent instanceof Macro) {
				macro = (Macro) parent;
			}
		}
		return macro;
	}

}

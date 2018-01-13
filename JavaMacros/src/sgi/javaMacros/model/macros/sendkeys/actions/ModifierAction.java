package sgi.javaMacros.model.macros.sendkeys.actions;

import sgi.javaMacros.model.macros.sendkeys.LuaMacrosCodeTable;

public class ModifierAction extends MonoDirectionalAction {
	@Deprecated
	public ModifierAction() {
	}

	public ModifierAction(int scanCode) {
		super(scanCode, true);
	}

	@Override
	public RobotActionType getType() {
		return RobotActionType.MODIFIER;
	}

	@Override
	public MonoDirectionalAction toPushAction() {
		return this;
	}

	private transient String tString = null;

	@Override
	public String toString() {
		if (tString == null){
			tString = LuaMacrosCodeTable.getCodeTable(false).scanToScript(getScanCode()).replaceAll("(\\()$", "");
		}

		return tString;
	}

}

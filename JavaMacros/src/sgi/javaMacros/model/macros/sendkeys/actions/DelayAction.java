package sgi.javaMacros.model.macros.sendkeys.actions;

import sgi.generic.debug.Debug;

public class DelayAction extends AsyncAction {
	@Deprecated
	public DelayAction() {
	}

	public DelayAction(int scanCode) {
		super(scanCode);
	}

	public void setDuration(int dur) {
		super.setScanCode(dur);
	}

	public int getDuration() {

		return super.getScanCode();
	}

	@Override
	protected void ante_exec() {
		try {
			Thread.sleep(getDuration());
		} catch (InterruptedException e) {
			Debug.info(e, 10);
		}
	}

	@Override
	public int hashCode() {
		return String.valueOf(this).hashCode() + getDuration();
	}

	@Override
	public String toString() {
		return "{delay " + getScanCode()+"}";
	}

	@Override
	public RobotActionType getType() {
		return RobotActionType.DELAY;
	}

}
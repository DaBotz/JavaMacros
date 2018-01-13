package sgi.javaMacros.model.macros.sendkeys.actions;

import java.util.ArrayList;
import java.util.Iterator;

import sgi.javaMacros.model.JavaMacrosConfiguration;

public class WaitAction extends RobotAction {

	public WaitAction() {
		super(JavaMacrosConfiguration.getKeyHeld());
	}

	public WaitAction(int holdTime) {
		super(holdTime);
	}

	@Override
	protected boolean execute(Iterator<RobotAction> itera) {
		try {
			Thread.sleep(getScanCode());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Wait " + getScanCode();
	}

	@Override
	public RobotActionType getType() {
		return RobotActionType.WAIT;
	}

	@Override
	public void atomicElements(ArrayList<RobotAction> list) {
		list.add(this);
		
	}

}

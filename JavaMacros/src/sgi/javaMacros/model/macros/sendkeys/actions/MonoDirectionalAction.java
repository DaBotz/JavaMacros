package sgi.javaMacros.model.macros.sendkeys.actions;

import java.util.ArrayList;
import java.util.Iterator;

public class MonoDirectionalAction extends RobotAction {
	@Deprecated
	public MonoDirectionalAction() {
	}
	
	
	public MonoDirectionalAction(int scanCode, boolean down) {
		super(scanCode);
		this.down = down;
	}

	boolean down;

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	@Override
	public int hashCode() {

		return scanCode << 1 | (down ? 1 : 0);
	}

	@Override
	public boolean execute(Iterator<RobotAction> itera) {

		if (down)
			keyPress(scanCode);
		else
			keyRelease(scanCode);

		return true;
	}

	

	public MonoDirectionalAction getClosure() {
		return new MonoDirectionalAction(getScanCode(), false);
	}

	@Override
	public String toString() {


		return "{" + getScanCode()+" " +(down? "dn":"up")+"}";
	}

	@Override
	public RobotActionType getType() {
		return RobotActionType.MONODIRECTIONAL; 
	}
	
	@Override
	public void atomicElements(ArrayList<RobotAction> list) {
		list.add(this);
	}


}
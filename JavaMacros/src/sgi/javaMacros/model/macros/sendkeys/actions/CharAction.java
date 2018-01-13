package sgi.javaMacros.model.macros.sendkeys.actions;

import static sgi.javaMacros.model.macros.sendkeys.LuaMacrosCodeTable.toDefinedCase;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import sgi.javaMacros.model.JavaMacrosConfiguration;

public class CharAction extends RobotAction {

	@Deprecated
	public CharAction() {
	}

	private int holdingTime;

	public CharAction(char scanCode) {
		this(KeyEvent.getExtendedKeyCodeForChar(scanCode));
	}
	
	public CharAction(int scanCode) {
		super(scanCode);
		this.holdingTime = JavaMacrosConfiguration.getKeyHeld();
	}


	public int getHoldingTime() {
		return holdingTime;
	}

	public void setHoldingTime(int holdingTime) {
		this.holdingTime = holdingTime;
	}

	@Override
	public boolean execute(Iterator<RobotAction> itera) {
		super.keyPress(scanCode);
		
		hold();
		super.keyRelease(scanCode);
		System.out.println("typed: "+scanCode);
		return true;
	}

	private void hold() {
		try {
			Thread.sleep(holdingTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return toDefinedCase (new Character((char) getScanCode()).toString());
	}

	@Override
	public RobotActionType getType() {
		return RobotActionType.TYPE_A_KEY;
	}

	@Override
	public void atomicElements(ArrayList<RobotAction> list) {
		list.add(new MonoDirectionalAction(scanCode, true)); 
		list.add(new MonoDirectionalAction(scanCode, false)); 
		
	}

}

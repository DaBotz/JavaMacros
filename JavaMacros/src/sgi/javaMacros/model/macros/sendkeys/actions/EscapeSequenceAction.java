package sgi.javaMacros.model.macros.sendkeys.actions;

import static sgi.javaMacros.model.macros.sendkeys.LuaMacrosCodeTable.toDefinedCase;
import static sgi.javaMacros.model.macros.sendkeys.LuaMacrosScriptScanner.HOLD;

import java.util.ArrayList;
import java.util.Iterator;

import sgi.javaMacros.model.KeyNameProvider;

public class EscapeSequenceAction extends RobotAction {

	public int getRepeats() {
		return repeats;
	}

	public int getHold() {
		return hold;
	}

	public void setRepeats(int repeats) {
		this.repeats = repeats;
	}

	public void setHold(int hold) {
		this.hold = hold;
	}

	private int repeats;
	private int hold;
	private transient String name;

	@Deprecated
	public EscapeSequenceAction() {
	}

	public EscapeSequenceAction(int scanCode) {
		super(scanCode);
		this.repeats = 1;
		this.hold = 0;
	}

	public EscapeSequenceAction(int scanCode, int repeats, int hold) {
		super(scanCode);
		this.repeats = repeats;
		this.hold = hold;
	}

	@Override
	public void atomicElements(ArrayList<RobotAction> list) {
		for (int i = 0; i < repeats; i++) {
			list.add(new MonoDirectionalAction( scanCode, true));
			list.add(new MonoDirectionalAction( scanCode, false));
		}

	}

	
	@Override
	protected boolean execute(Iterator<RobotAction> itera) {

		for (int i = 0; i < repeats; i++) {
			keyPress(scanCode);
			_hold();
			keyRelease(scanCode);
			System.out.println("Typed "+scanCode);
		}

		return true;
	}

	private void _hold() {

		try {
			Thread.sleep(hold);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		if (name == null) {
			int scanCode2 = getScanCode();
			if (scanCode2 == 32)
				name = toDefinedCase("SPACE");
			name =toDefinedCase (KeyNameProvider.getKeyName(scanCode2));
		}

		return "{"//
				+ name//
				+ (repeats > 1 ? (" " + repeats) : "") //
				+ (hold > 0 ? (", " + HOLD + " " + hold) : "") //
				+ "}";
	}

	@Override
	public RobotActionType getType() {
		return RobotActionType.ESCAPE_SEQUENCE;
	}

	public void pushedAgain() {
		repeats++; 	
	}

}

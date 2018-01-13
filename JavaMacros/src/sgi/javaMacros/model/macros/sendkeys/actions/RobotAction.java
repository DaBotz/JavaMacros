package sgi.javaMacros.model.macros.sendkeys.actions;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import sgi.javaMacros.model.JavaMacrosConfiguration;

public abstract class RobotAction {

	protected int scanCode;

	@Deprecated
	public RobotAction() {
		super();
	}

	public int getScanCode() {
		return scanCode;
	}

	public void setScanCode(int scanCode) {
		this.scanCode = scanCode;
	}

	protected abstract boolean execute(Iterator<RobotAction> itera);

	public abstract String toString();

	public RobotAction(int scanCode) {
		super();
		this.scanCode = scanCode;
	}

	@Override
	public int hashCode() {

		return scanCode << 1;
	}

	public boolean equals(MonoDirectionalAction obj) {
		return obj.hashCode() == hashCode();
	}

	public boolean isAsyncAction() {
		return false;
	}

	private static AWTException robotCreationError;
	protected static Robot robotx;
	static {
		try {
			robotx = new Robot();
		} catch (AWTException e) {
			RobotAction.robotCreationError = e;
			e.printStackTrace();
		}
	}

	public abstract RobotActionType getType();

	public static Robot getRobotx() throws AWTException {
		if (robotx == null)
			robotx = new Robot();
		robotx.setAutoDelay(JavaMacrosConfiguration.instance().getKeyHoldingTime());
		return robotx;
	}

	public static void execute(ArrayList<RobotAction> list) throws AWTException {
		getRobotx();

		ListIterator<RobotAction> listIterator = list.listIterator();
		processIterator(listIterator);
	}

	protected static void processIterator(Iterator<RobotAction> itera) {
		while (itera.hasNext() //
				&& itera.next().execute(itera))
			;
	}

	public void atomicElements(ArrayList<RobotAction> list) {
		list.add(this);
	}

	public static ArrayList<RobotAction> toAtomics(ArrayList<RobotAction> list) {
		ArrayList<RobotAction> list2 = new ArrayList<>(list.size());
		for (RobotAction robotAction : list) {
			robotAction.atomicElements(list2);
		}
		return list2;
	}

	public static ArrayList<RobotAction> toPushes(ArrayList<RobotAction> list) {
		ArrayList<RobotAction> list2 = new ArrayList<>(list.size());
		for (RobotAction robotAction : list) {
			list2.add(robotAction.toPushAction());
		}
		return list2;
	}

	public static ArrayList<RobotAction> toBalancedActions(ArrayList<RobotAction> list) {
		int size = list.size() + list.size();
		ArrayList<RobotAction> list2 = new ArrayList<>(size + 1);

		for (int i = 0; i < list.size(); i++) {

			list2.add(i, list.get(i).toPushAction());
			list2.add(size - i, list.get(i).toReleaseAction());
		}

		list2.add(list.size(), new WaitAction());

		return list2;
	}

	public MonoDirectionalAction toPushAction() {
		return new MonoDirectionalAction(scanCode, true);
	}

	public MonoDirectionalAction toReleaseAction() {
		return new MonoDirectionalAction(scanCode, true);
	}

	private static Object lock = "";

	public static void keyPress(int scanCode2) {
		synchronized (lock) {
			robotx.keyPress(scanCode2);
		}
	}

	public static void keyRelease(int scanCode2) {
		synchronized (lock) {

			robotx.keyRelease(scanCode2);
		}
	}

	public static AWTException getRobotCreationError() {
		return robotCreationError;
	}

	
	

}
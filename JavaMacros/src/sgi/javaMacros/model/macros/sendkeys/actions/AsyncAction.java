package sgi.javaMacros.model.macros.sendkeys.actions;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingUtilities;

public class AsyncAction extends RobotAction {

	@Deprecated
	public AsyncAction() {
		super();
	}

	public AsyncAction(int scanCode) {
		super(scanCode);
	}

	public void setDuration(int dur) {
		super.setScanCode(dur);
	}

	public int getDuration() {

		return super.getScanCode();
	}

	public void doExecute(final Iterator<RobotAction> itera) {
		ante_exec();
		reMergeInAWTThread(itera);
	}

	protected void reMergeInAWTThread(final Iterator<RobotAction> itera) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				processIterator(itera);
//				while (itera.hasNext()) {
//					RobotAction keyAction = (RobotAction) itera.next();
//					if (keyAction.isAsyncAction()) {
//						AsyncAction subAsync = (AsyncAction) keyAction;
//						subAsync.doExecute(itera);
//					} else
//						keyAction.execute(itera);
//
//				}
			}
		});
	}

	protected void ante_exec() {
	}

	protected boolean execute(final Iterator<RobotAction> itera) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				doExecute(itera);
			}
		}

		).start();

		return false;
	}

	@Override
	public int hashCode() {
		return String.valueOf(this).hashCode();
	}

	@Override
	public boolean isAsyncAction() {
		return true;
	}

	@Override
	public String toString() {
		return "{async}";
	}

	@Override
	public RobotActionType getType() {
		return RobotActionType.ASYNC;
	}

	@Override
	public void atomicElements(ArrayList<RobotAction> list) {

	}

}
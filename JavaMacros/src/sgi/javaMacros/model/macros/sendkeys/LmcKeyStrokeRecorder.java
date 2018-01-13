package sgi.javaMacros.model.macros.sendkeys;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import sgi.javaMacros.model.macros.sendkeys.actions.CharAction;
import sgi.javaMacros.model.macros.sendkeys.actions.GroupClose;
import sgi.javaMacros.model.macros.sendkeys.actions.GroupOpen;
import sgi.javaMacros.model.macros.sendkeys.actions.ModifierAction;
import sgi.javaMacros.model.macros.sendkeys.actions.MonoDirectionalAction;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotActionType;

public class LmcKeyStrokeRecorder {
	private static final LuaMacrosCodeTable MODIFIERS_TABLE = LuaMacrosCodeTable.getModifiersTable();

	private static final LuaMacrosCodeTable CODE_TABLE = LuaMacrosCodeTable.getCodeTable(false);

	private LinkedList<RobotAction> actions;

	private transient long lastEventTime;

	boolean modifUp;

	private Object semaphore = "";

	private HashMap<RobotAction, Long> times;
	private HashSet<Integer> invokedModifs;


	public void preLoad(List<RobotAction> preexistent) {
		synchronized (semaphore) {
			actions = new LinkedList<>(

					// RobotAction.toAtomics(
					preexistent
			// )
			);
		}
		times = new HashMap<>();
		invokedModifs = new HashSet<>();
		long time = getTime();

		if (actions.size() > 0) {
			RobotAction[] a = new RobotAction[actions.size()];
			actions.toArray(a);
			for (int i = 0; i < a.length; i++) {
				times.put(a[i], time - i);
			}

		}
	}

	public void reset() {
		synchronized (semaphore) {
			actions = new LinkedList<>();
			times = new HashMap<>();
			modifUp = false;
			invokedModifs = new HashSet<>();
		}
	}

	public LmcKeyStrokeRecorder() {
		reset();
	}

	public LinkedList<RobotAction> getActions() {

		synchronized (semaphore) {
			return new LinkedList<>(actions);
		}
	}

	public String getActionsAsText() {
		StringBuffer buffer = new StringBuffer();

		RobotAction[] actionList = new RobotAction[actions.size()];
		synchronized (semaphore) {
			actions.toArray(actionList);
		}
		for (int i = 0; i < actionList.length; i++) {
			RobotAction action = actionList[i];
			action.getType();
			if (action != null && action.getType()!= RobotActionType.MONODIRECTIONAL)
				buffer.append(action.toString());
		}

		return buffer.toString();
	}

	public void keyStrokeRaw(KeyEvent evt, boolean down) {
		actions.add(new MonoDirectionalAction(evt.getKeyCode(), down));
	}

	public void keyStrokeRaw(int scanCode, boolean down) {
		actions.add(new MonoDirectionalAction(scanCode, down));
	}

	public void keyStroke(int scanCode, boolean down) {
		synchronized (semaphore) {
			long time = getTime();
			if (lastEventTime == 0)
				lastEventTime = time;

			int hold = (int) (lastEventTime - time);
			boolean isModifierScanCode = MODIFIERS_TABLE.knownScan(scanCode);
			boolean requiresEscape = (scanCode!= 32)//
					&& CODE_TABLE.requiresEscape(scanCode);

			RobotAction lastAction = actions.size() > 0 ? actions.getLast() : null;
			// RobotAction pastlastAction = actions.size() > 1 ?
			// actions.get(actions.size() - 2) : null;
			// RobotAction past2lastAction = actions.size() > 2 ?
			// actions.get(actions.size() - 3) : null;

			if (down) {
				if (isModifierScanCode) {
					if (!invokedModifs.contains(scanCode)) {
						ModifierAction modifier = new ModifierAction(scanCode);
						if (isType(lastAction, RobotActionType.GROUP_OPEN))
							addAction(-1, modifier);
						else
							actions.add(new ModifierAction(scanCode));
						invokedModifs.add(scanCode);
					}
				} else if (lastAction != null) {
					int pushes = 1;
					switch (lastAction.getType()) {

					case ESCAPE_SEQUENCE:
						if (lastAction.getScanCode() == scanCode)
							((LMCEscapeSequenceAction) lastAction).pushedAgain();
						else
							addAction(new MonoDirectionalAction(scanCode, down));

						break;

					case MODIFIER:
						if (!modifUp) {
							modifUp = true;
							addAction(new GroupOpen());
						}
						addAction(new MonoDirectionalAction(scanCode, down));

						break;

					case TYPE_A_KEY:
						if ( requiresEscape)
							pushes++;
						else {
//							if (lastAction.getScanCode() == scanCode)
//								actions.add(new CharAction(scanCode));
//							else
								addAction(new MonoDirectionalAction(scanCode, down));

							break;
						}
					case MONODIRECTIONAL:
						if (lastAction.getScanCode() == scanCode) {
							if (requiresEscape) {
								replaceLastAction(//
										new LMCEscapeSequenceAction(scanCode, pushes, 0));
							} else {
								RobotAction esa0 = new CharAction(scanCode);
								replaceLastAction(esa0);
								while (--pushes > 0)
									actions.add(new CharAction(scanCode));
							}
						} else
							addAction(new MonoDirectionalAction(scanCode, down));
						break;

					case ASYNC:
					case DELAY:
					case WAIT:
					case GROUP_CLOSURE:
					case GROUP_OPEN:
					default:
						addAction(new MonoDirectionalAction(scanCode, down));
						break;
					}

				} else
					addAction(new MonoDirectionalAction(scanCode, down));

			} else {

				RobotAction opener = lastAction;
				int index = actions.size() - 1;

				while (index >= 0 && (//
				opener.getType() == RobotActionType.DELAY || //
						opener.getType() == RobotActionType.WAIT || //
						opener.getScanCode() != scanCode)) {
					opener = actions.get(index--);
				}

				if (opener != null && opener.getScanCode() == scanCode) {

					switch (opener.getType()) {
					case ESCAPE_SEQUENCE:
						LMCEscapeSequenceAction esa1 = (LMCEscapeSequenceAction) opener;
						if (hold > 3000) {

						} else if (hold > 500) {
							if ((actions.size() - index) < 3)
								esa1.setHold(hold);
						}

						break;
					case MODIFIER:
						invokedModifs.remove(scanCode);
						if (invokedModifs.size() == 0) {
							modifUp = false;
							if (!isType(lastAction, RobotActionType.GROUP_CLOSURE)) {
								RobotAction rA2 = null, rA = lastAction;

								for (int k = actions.size() - 1; k >= 0 && rA != opener; k--) {
									rA = actions.get(k);
									if (isType(rA, RobotActionType.GROUP_CLOSURE)//
											|| (isType(rA, RobotActionType.GROUP_OPEN)//
													&& isType(rA2, RobotActionType.GROUP_OPEN))

									) {
										actions.remove(k);
									}
									rA2 = rA;
								}
								addAction(new GroupClose());
							}
						}

						break;
					case TYPE_A_KEY:
						if (requiresEscape) {
							LMCEscapeSequenceAction esa0 = new LMCEscapeSequenceAction(scanCode, 2, 0);
							replaceAction(index, esa0);
						} else
							addAction(new CharAction(scanCode));
						break;
					case MONODIRECTIONAL:
						RobotAction toReplaceCurrent = null;

						if (requiresEscape)
							toReplaceCurrent = new LMCEscapeSequenceAction(scanCode);
						else
							toReplaceCurrent = new CharAction(scanCode);

						replaceAction(index, toReplaceCurrent);
						break;
					default:
						// addAction(new MonoDirectionalAction(scanCode, down));
						break;
					}
				} else
					addAction(new LMCEscapeSequenceAction(scanCode));

			}
			lastEventTime = time;
		}

	}

	public boolean isType(RobotAction lastAction, RobotActionType type) {
		return lastAction != null && lastAction.getType() == type;
	}

	private void addAction(int index, RobotAction esa0) {
		RobotAction removed = actions.get(index);
		if (index < 0)
			index += actions.size();
		actions.add(index, esa0);
		Long long1 = times.get(removed);
		times.put(esa0, long1 + 1);
	}

	public void replaceLastAction(RobotAction esa0) {
		RobotAction removed = actions.removeLast();
		actions.add(esa0);
		Long long1 = times.get(removed);
		times.put(esa0, long1);
	}

	public void replaceAction(int index, RobotAction esa0) {
		RobotAction removed = actions.remove(index);
		actions.add(index, esa0);
		if (removed != null) {
			Long long1 = times.get(removed);
			times.put(esa0, long1);
		}
	}

	private void addAction(RobotAction e) {
		actions.add(e);
		times.put(e, getTime());
	}

	public long getTime() {
		long time = System.currentTimeMillis();
		return time;
	}

	public void keyStroke(KeyEvent event, boolean down) {
		keyStroke(event.getKeyCode(), down);
	}



}

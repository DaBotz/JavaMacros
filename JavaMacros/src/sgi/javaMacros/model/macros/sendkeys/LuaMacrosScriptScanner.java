package sgi.javaMacros.model.macros.sendkeys;

import static sgi.javaMacros.model.macros.sendkeys.LuaMacrosCodeTable.toDefinedCase;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import sgi.generic.debug.Debug;
import sgi.javaMacros.model.macros.execution.ErrorCode;
import sgi.javaMacros.model.macros.execution.IScanner;
import sgi.javaMacros.model.macros.execution.ScriptSegment;
import sgi.javaMacros.model.macros.sendkeys.actions.AsyncAction;
import sgi.javaMacros.model.macros.sendkeys.actions.CharAction;
import sgi.javaMacros.model.macros.sendkeys.actions.DelayAction;
import sgi.javaMacros.model.macros.sendkeys.actions.EscapeSequenceAction;
import sgi.javaMacros.model.macros.sendkeys.actions.GroupClose;
import sgi.javaMacros.model.macros.sendkeys.actions.GroupOpen;
import sgi.javaMacros.model.macros.sendkeys.actions.ModifierAction;
import sgi.javaMacros.model.macros.sendkeys.actions.MonoDirectionalAction;
import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;
import sgi.javaMacros.model.macros.sendkeys.actions.WaitAction;

public class LuaMacrosScriptScanner implements IScanner {


	private String DELAY = toDefinedCase("delay");
	private String ASYNC = toDefinedCase("async");
	public static final String HOLD = toDefinedCase("hold");
	public static final String UP = toDefinedCase("up");
	public static final String DN = toDefinedCase("dn");
	public static final String DOWN = toDefinedCase("down");

	private RobotAction groupOpen;
	private RobotAction groupClosed;
	public ArrayList<ErrorCode> errorcodes = new ArrayList<>();
	private boolean forJava;

	public ArrayList<ErrorCode> getErrorcodes() {
		return errorcodes;
	}

	public LuaMacrosScriptScanner(boolean forJava) {

		this.forJava = forJava;

		if (!forJava) {
			DELAY = "d y";
			ASYNC = "a c";
		}
		codes = LuaMacrosCodeTable.getCodeTable(forJava);

		groupOpen = new GroupOpen();
		groupClosed = new GroupClose();

	}

	public boolean isForJava() {
		return forJava;
	}

	public LuaMacrosCodeTable getCodes() {
		return codes;
	}

	RobotAction separator = new MonoDirectionalAction('\'', false);

	LuaMacrosCodeTable modifiers = LuaMacrosCodeTable.getModifiersTable();
	LuaMacrosCodeTable codes = null;
	private String LastScanned;
	private int[] statusCodes;

	public ArrayList<RobotAction> parse(String text) {
		this.LastScanned = text;
		errorcodes.clear();
		int level = NORMAL;

		int groupCounter = 0;

		char[] charArray = text.toCharArray();

		this.statusCodes = new int[charArray.length];
		// for (int i = 0; i < statusCodes.length; i++) {
		// statusCodes[i] = 3;
		// }

		int length = text.length();

		lastParsedActions = new ArrayList<>((length << 1) + length);
		Hashtable<Integer, Stack<MonoDirectionalAction>> openMods = new Hashtable<>();

		int length2 = charArray.length;
		for (int i = 0; i < length2; i++) {
			char c = charArray[i];
			switch (c) {
			case '{':

				i = scanForEscapes(charArray, lastParsedActions, i);

				break;
			case '(':

				lastParsedActions.add(groupOpen);
				statusCodes[i] = level = Math.max(level, GROUP);
				groupCounter++;
				scanForGroupClosures(charArray, i);

				break;

			case ')':
				lastParsedActions.add(groupClosed);
				closeModifiers(groupCounter, lastParsedActions, openMods);
				groupCounter--;

				statusCodes[i] = level;
				if (groupCounter == 0)
					level = NORMAL;
				break;
			default:
				int scanCode = getScanCode(c);
				if (modifiers.knownScan(scanCode)) {
					MonoDirectionalAction modifier = new ModifierAction(scanCode);
					lastParsedActions.add(modifier);
					statusCodes[i] = level = MODIFIER;

					int nextCounter = groupCounter + 1;

					Stack<MonoDirectionalAction> stack = openMods.get(nextCounter);
					if (stack == null) {
						stack = new Stack<>();
						openMods.put(nextCounter, stack);
					}
					stack.push(modifier);

				} else if (scanCode > 0) {

					lastParsedActions.add(new CharAction(c));
					statusCodes[i] = level;

					if (openMods.size() > 0 && groupCounter == 0) {
						closeModifiers(groupCounter + 1, lastParsedActions, openMods);
					}

				}
			}
		}
		return lastParsedActions;
	}

	private void scanForGroupClosures(char[] charArray, int i) {
		int groupCounter = 1;
		int j = i + 1;
		for (; j < charArray.length; j++) {
			switch (charArray[j]) {
			case '(':
				groupCounter++;
				break;
			case ')':
				groupCounter--;
				break;
			}
		}
		if (groupCounter > 0) {
			errorcodes.add(new ErrorCode("Unclosed group at " + i, i, j));
		}

	}

	public void closeModifiers(int groupCounter, ArrayList<RobotAction> actions,
			Hashtable<Integer, Stack<MonoDirectionalAction>> openMods) {
		Stack<MonoDirectionalAction> opens = openMods.remove(groupCounter);
		if (opens != null) {
			while (!opens.isEmpty()) {
				actions.add(opens.pop().getClosure());
			}
		}
	}

	public void printStatus(char[] charArray) {
		String stats = "";
		for (int stat : statusCodes) {
			stats += (stat + ",");
		}
		System.out.println(stats);
		stats = "";
		for (char stat : charArray) {
			stats += (stat + ",");
		}
		System.out.println(stats);
	}

	public int scanForEscapes(char[] charArray, ArrayList<RobotAction> actions, int i) {

		statusCodes[i] = ESCAPE;

		int k = i + 1, kk = k + 1;
		int length2 = charArray.length;
		int holdTime = 0;

		for (; k < length2 //
				&& !(charArray[k] == '}' && (kk == length2 || charArray[kk] != '}')); k++, kk++) {
			statusCodes[k] = ESCAPE;
			if (kk < statusCodes.length)
				statusCodes[kk] = ESCAPE;
		}

		if (k == length2) {

			errorcodes.add(new ErrorCode("Unterminated Escape sequence starting at index " + i, i, kk));
		}

		String block = toDefinedCase(new String(charArray, i + 1, k - i - 1));
		String[] split0 = block.split(",\\\\s{0,8}(" + toDefinedCase(HOLD) + ")?\\s*");
		switch (split0.length) {

		case 0:
			break;
		case 1:
			block = split0[0];
			break;
		case 2:
			try {
				holdTime = Integer.parseInt(split0[1]);
			} catch (NumberFormatException e) {
				Debug.info(e, 5);
			}
		}

//		if (split0.length > 0) {
//
//		}

		String[] split = block.split("\\s+");
		int num = 1;
		String kName = null;
		kName = split[0];

		switch (split.length) {
		case 3:
			errorcodes
					.add(new ErrorCode("Invalid Escape sequence format {" + block + "} starting at index " + i, i, kk));

		case 2:
			String s = split[1];

			try {
				num = 0;
				num = Integer.parseInt(s);
			} catch (NumberFormatException e) {
				int scanCode = getScanCode(kName);

				if (UP.equalsIgnoreCase(s)) {
					actions.add(new MonoDirectionalAction(scanCode, false));
					return k;

				} else if (DN.equalsIgnoreCase(s)//
						|| DOWN.equalsIgnoreCase(s)) {
					actions.add(new MonoDirectionalAction(scanCode, true));
					if (holdTime > 0) {
						actions.add(new WaitAction(holdTime));
					}
					return k;

				} else

					errorcodes.add(
							new ErrorCode("Invalid Escape sequence {" + block + "} starting at index " + i, i, kk));
			}
			if (num < 1) {
				int scanCode = getScanCode(kName);

				if (UP.equalsIgnoreCase(s)) {
					actions.add(new MonoDirectionalAction(scanCode, false));
					return k;

				} else if (DN.equalsIgnoreCase(s)//
						|| DOWN.equalsIgnoreCase(s)) {
					actions.add(new MonoDirectionalAction(scanCode, true));
					if (holdTime > 0) {
						actions.add(new WaitAction(holdTime));
					}
					return k;

				} else {

					errorcodes.add(new ErrorCode("Invalid number of repetitions " + s + " in Escape sequence {" + block
							+ "} starting at index " + i, i, kk));

				}
			}

		case 1:
		}
		int maxDelay = maxDelay(num);

		if (ASYNC.equals(kName)) {
			actions.add(new AsyncAction(0));
		} else if (DELAY.equals(kName)) {
			actions.add(new DelayAction(maxDelay));
		} else {
			int scanCode = getScanCode(kName);
			if (modifiers.knownScan(scanCode)) {
				actions.add(new ModifierAction(scanCode));
			} else if (scanCode > 0) {
				// for (int q = 0; q < num; q++)
				// actions.add(new CharAction(scanCode));
				if (num > 0)
					actions.add(new EscapeSequenceAction(scanCode, num, holdTime));

			} else {
				errorcodes.add(new ErrorCode("Unknown Escape name {" + kName + "} at index " + i, i, kk));
			}
		}

		return k;
	}

	public int maxDelay(int num) {
		return Math.max(5000, num);
	}

	private int getScanCode(char c) {
		switch (c) {
		case '#':
			return KeyEvent.VK_ALT_GRAPH;
		case '&':
			return KeyEvent.VK_TAB;
		case '%':
			return KeyEvent.VK_ALT;
		case '^':
			return KeyEvent.VK_CONTROL;
		case '+':
			return KeyEvent.VK_SHIFT;
		}

		return getScanCode(String.valueOf(c));

	}

	private int getScanCode(String kName) {
		if (kName == null)
			return -1;
		int scanCode = codes.getScanCode("{" + kName + "}");

		if (scanCode < 0) {
			String kName0 = kName.replaceAll("^(\\\\)", "");
			if (kName0.length() == 1) {
				int ekfc = //
						KeyEvent.getExtendedKeyCodeForChar(//
								kName0.charAt(0));
				if (!KeyCodeChecker.isValidkeyCode(ekfc)) {
					return -1;
				}
				return ekfc;
			} else {
				if (forJava)
					try {
						return Integer.parseInt(kName);
					} catch (Exception e) {
					}
			}
		}
		return scanCode;
	}

	public ArrayList<ScriptSegment> getSegmentation() {

		ArrayList<ScriptSegment> segments = new ArrayList<>();
		if (statusCodes.length == 0)
			return segments;
		// char[] charArray = LastScanned.toCharArray();
		// printStatus(charArray);
		ArrayList<ErrorCode> errorcodes2 = getErrorcodes();
		for (ErrorCode eC : errorcodes2) {
			for (int k = eC.getStart(); k < eC.getEnd() && k < statusCodes.length; k++) {
				statusCodes[k] = ERROR;
			}
		}
		// printStatus(charArray);

		int b, a = b = statusCodes[0];
		int cut, i = cut = 0;

		for (; i < statusCodes.length; i++) {
			b = statusCodes[i];
			if (b != a) {
				segments.add(new ScriptSegment(LastScanned.substring(cut, i), a, cut, i));
				cut = i;
				a = b;
			}
		}
		segments.add(new ScriptSegment(LastScanned.substring(cut, i), b, cut, i));

		// for (ScriptSegment seg : segments) {
		// System.out.print(seg.getPart() + "::" + seg.getStatus());
		// }
		// System.out.println();
		return segments;

	}

	@Override
	public void scan(String text) {
		ArrayList<RobotAction> parseList = parse(text);
		fireScans(new ScanEvent(this, text, parseList));
	}

	private ScannerListener[] listeners = new ScannerListener[0];
	private ArrayList<RobotAction> lastParsedActions;

	protected void fireScans(ScanEvent evt) {
		ScannerListener[] scannerListeners = listeners;
		for (int i = 0; i < scannerListeners.length; i++) {
			scannerListeners[i].readScanEvent(evt);
		}
	}

	public void removeScannerListener(ScannerListener scannerListener) {
		ArrayList<ScannerListener> l = new ArrayList<>(Arrays.asList(listeners));
		l.remove(scannerListener);
		ScannerListener[] ls = new ScannerListener[l.size()];
		l.toArray(ls);
		this.listeners = ls;
	}

	public void addScannerListener(ScannerListener scannerListener) {
		ArrayList<ScannerListener> l = new ArrayList<>(Arrays.asList(listeners));
		l.add(scannerListener);
		ScannerListener[] ls = new ScannerListener[l.size()];
		l.toArray(ls);
		this.listeners = ls;
	}

	public List<RobotAction> getLastParsed() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<RobotAction> getLastParsedActions() {
		return lastParsedActions;
	}

}

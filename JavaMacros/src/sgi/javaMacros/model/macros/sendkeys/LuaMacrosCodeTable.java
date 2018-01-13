package sgi.javaMacros.model.macros.sendkeys;

import static java.awt.event.KeyEvent.VK_ADD;
import static java.awt.event.KeyEvent.VK_ALT_GRAPH;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_CANCEL;
import static java.awt.event.KeyEvent.VK_CAPS_LOCK;
import static java.awt.event.KeyEvent.VK_CLEAR;
import static java.awt.event.KeyEvent.VK_DECIMAL;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_DIVIDE;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_END;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_F1;
import static java.awt.event.KeyEvent.VK_F10;
import static java.awt.event.KeyEvent.VK_F11;
import static java.awt.event.KeyEvent.VK_F12;
import static java.awt.event.KeyEvent.VK_F13;
import static java.awt.event.KeyEvent.VK_F14;
import static java.awt.event.KeyEvent.VK_F15;
import static java.awt.event.KeyEvent.VK_F16;
import static java.awt.event.KeyEvent.VK_F17;
import static java.awt.event.KeyEvent.VK_F18;
import static java.awt.event.KeyEvent.VK_F19;
import static java.awt.event.KeyEvent.VK_F2;
import static java.awt.event.KeyEvent.VK_F20;
import static java.awt.event.KeyEvent.VK_F21;
import static java.awt.event.KeyEvent.VK_F22;
import static java.awt.event.KeyEvent.VK_F23;
import static java.awt.event.KeyEvent.VK_F24;
import static java.awt.event.KeyEvent.VK_F3;
import static java.awt.event.KeyEvent.VK_F4;
import static java.awt.event.KeyEvent.VK_F5;
import static java.awt.event.KeyEvent.VK_F6;
import static java.awt.event.KeyEvent.VK_F7;
import static java.awt.event.KeyEvent.VK_F8;
import static java.awt.event.KeyEvent.VK_F9;
import static java.awt.event.KeyEvent.VK_HELP;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_INSERT;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_MULTIPLY;
import static java.awt.event.KeyEvent.VK_NUMPAD0;
import static java.awt.event.KeyEvent.VK_NUMPAD1;
import static java.awt.event.KeyEvent.VK_NUMPAD2;
import static java.awt.event.KeyEvent.VK_NUMPAD3;
import static java.awt.event.KeyEvent.VK_NUMPAD4;
import static java.awt.event.KeyEvent.VK_NUMPAD5;
import static java.awt.event.KeyEvent.VK_NUMPAD6;
import static java.awt.event.KeyEvent.VK_NUMPAD7;
import static java.awt.event.KeyEvent.VK_NUMPAD8;
import static java.awt.event.KeyEvent.VK_NUMPAD9;
import static java.awt.event.KeyEvent.VK_NUM_LOCK;
import static java.awt.event.KeyEvent.VK_PAGE_DOWN;
import static java.awt.event.KeyEvent.VK_PAGE_UP;
import static java.awt.event.KeyEvent.VK_PRINTSCREEN;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SCROLL_LOCK;
import static java.awt.event.KeyEvent.VK_SUBTRACT;
import static java.awt.event.KeyEvent.VK_TAB;
import static java.awt.event.KeyEvent.VK_UNDEFINED;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

public class LuaMacrosCodeTable {

	private static LuaMacrosCodeTable noJavaNames;
	private static LuaMacrosCodeTable yesJavaNames;

	public static LuaMacrosCodeTable getCodeTable(boolean loadJavaNames) {

		if (loadJavaNames && yesJavaNames != null)
			return yesJavaNames;
		else if (!loadJavaNames && noJavaNames != null)
			return noJavaNames;

		LuaMacrosCodeTable macrosCodeTable = new LuaMacrosCodeTable();

		if (loadJavaNames) {
			macrosCodeTable.loadLuaMacrosNames(true);
			macrosCodeTable.loadJavaNames();
			yesJavaNames = macrosCodeTable;
		} else {
			macrosCodeTable.loadLuaMacrosNames(false);
			noJavaNames = macrosCodeTable;
		}

		return macrosCodeTable;
	}

	public static LuaMacrosCodeTable getModifiersTable() {

		LuaMacrosCodeTable macrosCodeTable = new LuaMacrosCodeTable();
		macrosCodeTable.loadModifiers();
		return macrosCodeTable;
	}

	public static String toDefinedCase(String codeName) {
		codeName = codeName.toLowerCase();
		return codeName;
	}

	private Hashtable<Integer, String> scanToScriptTable = new Hashtable<>();

	private Hashtable<String, Integer> scriptToScanTable = new Hashtable<>();

	private LuaMacrosCodeTable() {

	}

	public int getScanCode(char c) {
		return getScanCode(String.valueOf(c));
	}

	public int getScanCode(String text) {
		Integer integer = scriptToScanTable.get(text);
		return integer == null ? -1 : integer.intValue();
	}

	public Hashtable<Integer, String> getScanToScriptTableCopy() {
		return new Hashtable<>(scanToScriptTable);
	}

	public ArrayList<String> getScripNames() {
		ArrayList<String> list = new ArrayList<>(this.scriptToScanTable.keySet());
		Collections.sort(list);
		return list;

	}

	public String getScriptNameFromScan(int scanCode) {

		String nm = scanToScriptTable.get(new Integer(scanCode));
		return nm == null ? "" : nm;
	}

	public Hashtable<String, Integer> getScriptToScanTableCopy() {
		return new Hashtable<>(scriptToScanTable);
	}

	public boolean knownScan(int code) {
		return scanToScriptTable.containsKey(code);
	}

	public boolean knownScript(String code) {
		return scriptToScanTable.containsKey(code);
	}

	public void load(Integer scanCode, String codeName) {
		codeName = toDefinedCase(codeName);
		if (!scanToScriptTable.contains(scanCode)) {
			scanToScriptTable.put(scanCode, codeName);
		}

		scriptToScanTable.put(codeName, scanCode);
		String codeName2 = codeName.replaceAll("(\\()$", "");

		if (!(codeName2.equals(codeName))) {
			scriptToScanTable.put(codeName2, scanCode);
		}

		// codeName2 = codeName.replaceAll("(\\})$", "");
		// codeName2 = codeName2.replaceAll("^(\\{)", "");
		// if (!(codeName2.equals(codeName))) {
		// scriptToScanTable.put(codeName2, scanCode);
		// }
	}

	private void load(String name, int scanCode) {
		load(scanCode, name);

	}

	public void loadJavaNames() {
		Field[] declaredFields = KeyEvent.class.getDeclaredFields();
		for (Field field : declaredFields) {
			for (char c = 1; c < 256; c++) {
				if (!(Character.isUpperCase(c) || Character.isWhitespace(c))) {
					int eKCode = KeyEvent.getExtendedKeyCodeForChar(c);
					if (eKCode != VK_UNDEFINED && KeyCodeChecker.isValidkeyCode(eKCode)) {

						load(eKCode, "{" + String.valueOf(c) + "}");
					}
				}

			}

			load(10, "{line_feed}");

			String name = field.getName();
			if (name.startsWith("VK_")) {
				String newName = name.replaceAll("^(VK_)", "").toLowerCase();
				try {
					load(((Integer) field.get(null)), "{" + newName + "}");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadLuaMacrosNames(boolean overWriteWithJavaScanCodes) {
		load(8, "{backspace}");
		load(9, "&(");
		load(13, "{enter}");
		load(16, "+(");
		load(17, "^(");
		load(18, "%(");
		load(19, "{pause}");
		load(20, "{capslock}");
		load(27, "{escape}");
		load(32, " ");
		load(32, "{space}");
		load(33, "{pgup}");
		load(34, "{pgdn}");
		load(35, "{end}");
		load(36, "{home}");
		load(37, "{left}");
		load(38, "{up}");
		load(39, "{right}");
		load(40, "{down}");
		load(44, "{prtsc}");
		load(45, "{ins}");
		load(46, "{del}");
		load(48, "0");
		load(49, "1");
		load(50, "2");
		load(51, "3");
		load(52, "4");
		load(53, "5");
		load(54, "6");
		load(55, "7");
		load(56, "8");
		load(57, "9");
		load(65, "a");
		load(66, "b");
		load(67, "c");
		load(68, "d");
		load(69, "e");
		load(70, "f");
		load(71, "g");
		load(72, "h");
		load(73, "i");
		load(74, "j");
		load(75, "k");
		load(76, "l");
		load(77, "m");
		load(78, "n");
		load(79, "o");
		load(80, "p");
		load(81, "q");
		load(82, "r");
		load(83, "s");
		load(84, "t");
		load(85, "u");
		load(86, "v");
		load(87, "w");
		load(88, "x");
		load(89, "y");
		load(90, "z");
		load(96, "{num0}");
		load(97, "{num1}");
		load(98, "{num2}");
		load(99, "{num3}");
		load(100, "{num4}");
		load(101, "{num5}");
		load(102, "{num6}");
		load(103, "{num7}");
		load(104, "{num8}");
		load(105, "{num9}");
		load(106, "{nummultiply}");
		load(107, "{numplus}");
		load(109, "{numminus}");
		load(110, "{numdecimal}");
		load(111, "{numdivide}");
		load(112, "{f1}");
		load(113, "{f2}");
		load(114, "{f3}");
		load(115, "{f4}");
		load(116, "{f5}");
		load(117, "{f6}");
		load(118, "{f7}");
		load(119, "{f8}");
		load(120, "{f9}");
		load(121, "{f10}");
		load(122, "{f11}");
		load(123, "{f12}");
		load(124, "{f13}");
		load(125, "{f14}");
		load(126, "{f15}");
		load(127, "{f16}");
		load(144, "{numlock}");
		load(145, "{scrolllock}");
		load(160, "+<(");
		load(161, "+>(");
		load(162, "^<(");
		load(163, "^>(");
		load(164, "%<(");
		load(165, "%>(");
		load(186, ";");
		load(187, "=");
		load(188, ",");
		load(189, "-");
		load(190, ".");
		load(191, "/");
		load(192, "`");
		load(220, "\\");
		load(221, "]");
		load(219, "[");
		load(222, "\'");
		
		if( ! overWriteWithJavaScanCodes ) return; 

		load("{BKSP}", VK_BACK_SPACE);
		load("{BS}", VK_BACK_SPACE);
		load("{BACKSPACE}", VK_BACK_SPACE);
		load("{BREAK}", VK_CANCEL);
		load("{CAPSLOCK}", VK_CAPS_LOCK);
		load("{CLEAR}", VK_CLEAR);
		load("{DEL}", VK_DELETE);
		load("{DELETE}", VK_DELETE);
		load("{DOWN}", VK_DOWN);
		load("{END}", VK_END);
		load("{ENTER}", VK_ENTER);
		load("{ESC}", VK_ESCAPE);
		load("{ESCAPE}", VK_ESCAPE);
		load("{F1}", VK_F1);
		load("{F10}", VK_F10);
		load("{F11}", VK_F11);
		load("{F12}", VK_F12);
		load("{F13}", VK_F13);
		load("{F14}", VK_F14);
		load("{F15}", VK_F15);
		load("{F16}", VK_F16);
		load("{F17}", VK_F17);
		load("{F18}", VK_F18);
		load("{F19}", VK_F19);
		load("{F2}", VK_F2);
		load("{F20}", VK_F20);
		load("{F21}", VK_F21);
		load("{F22}", VK_F22);
		load("{F23}", VK_F23);
		load("{F24}", VK_F24);
		load("{F3}", VK_F3);
		load("{F4}", VK_F4);
		load("{F5}", VK_F5);
		load("{F6}", VK_F6);
		load("{F7}", VK_F7);
		load("{F8}", VK_F8);
		load("{F9}", VK_F9);
		load("{HELP}", VK_HELP);
		load("{HOME}", VK_HOME);
		load("{INS}", VK_INSERT);
		load("{LEFT}", VK_LEFT);
		load("{NUM0}", VK_NUMPAD0);
		load("{NUM1}", VK_NUMPAD1);
		load("{NUM2}", VK_NUMPAD2);
		load("{NUM3}", VK_NUMPAD3);
		load("{NUM4}", VK_NUMPAD4);
		load("{NUM5}", VK_NUMPAD5);
		load("{NUM6}", VK_NUMPAD6);
		load("{NUM7}", VK_NUMPAD7);
		load("{NUM8}", VK_NUMPAD8);
		load("{NUM9}", VK_NUMPAD9);
		load("{NUMDECIMAL}", VK_DECIMAL);
		load("{NUMDIVIDE}", VK_DIVIDE);
		load("{NUMLOCK}", VK_NUM_LOCK);
		load("{NUMMINUS}", VK_SUBTRACT);
		load("{NUMMULTIPLY}", VK_MULTIPLY);
		load("{NUMPLUS}", VK_ADD);
		load("{PGDN}", VK_PAGE_DOWN);
		load("{PGUP}", VK_PAGE_UP);
		load("{PRTSC}", VK_PRINTSCREEN);
		load("{RIGHT}", VK_RIGHT);
		load("{SCROLLLOCK}", VK_SCROLL_LOCK);
		load("{TAB}", VK_TAB);
		load("{UP}", VK_UP);

	}

	public void loadModifiers() {
		load(9, "&(");
		load(16, "+(");
		load(17, "^(");
		load(18, "%(");
		load(160, "+<(");
		load(161, "+>(");
		load(162, "^<(");
		load(163, "^>(");
		load(164, "%<(");
		load(165, "%>(");
		load(VK_ALT_GRAPH, "#(");

	}

	private Hashtable<Integer, Integer> requiredEscapes;

	private void createRequiredEscapesTable() {
		if (requiredEscapes != null)
			return;
		requiredEscapes = new Hashtable<>();

		requiredEscapes.put(8, 8); // , 8); //, "{backspace}");
		requiredEscapes.put(9, 9); // , "&(");
		requiredEscapes.put(13, 13); // , "{enter}");
		requiredEscapes.put(16, 16); // , "+(");
		requiredEscapes.put(17, 17); // , "^(");
		requiredEscapes.put(18, 18); // , "%(");
		requiredEscapes.put(19, 19); // , "{pause}");
		requiredEscapes.put(20, 20); // , "{capslock}");
		requiredEscapes.put(27, 27); // , "{escape}");
		requiredEscapes.put(32, 32); // , " ");
		requiredEscapes.put(32, 32); // , "{space}");
		requiredEscapes.put(33, 33); // , "{pgup}");
		requiredEscapes.put(34, 34); // , "{pgdn}");
		requiredEscapes.put(35, 35); // , "{end}");
		requiredEscapes.put(36, 36); // , "{home}");
		requiredEscapes.put(37, 37); // , "{left}");
		requiredEscapes.put(38, 38); // , "{up}");
		requiredEscapes.put(39, 39); // , "{right}");
		requiredEscapes.put(40, 40); // , "{down}");
		requiredEscapes.put(44, 44); // , "{prtsc}");
		requiredEscapes.put(45, 45); // , "{ins}");
		requiredEscapes.put(46, 46); // , "{del}");
		requiredEscapes.put(96, 96); // , "{num0}");
		requiredEscapes.put(97, 97); // , "{num1}");
		requiredEscapes.put(98, 98); // , "{num2}");
		requiredEscapes.put(99, 99); // , "{num3}");
		requiredEscapes.put(100, 100); // , "{num4}");
		requiredEscapes.put(101, 101); // , "{num5}");
		requiredEscapes.put(102, 102); // , "{num6}");
		requiredEscapes.put(103, 103); // , "{num7}");
		requiredEscapes.put(104, 104); // , "{num8}");
		requiredEscapes.put(105, 105); // , "{num9}");
		requiredEscapes.put(106, 106); // , "{nummultiply}");
		requiredEscapes.put(107, 107); // , "{numplus}");
		requiredEscapes.put(109, 109); // , "{numminus}");
		requiredEscapes.put(110, 110); // , "{numdecimal}");
		requiredEscapes.put(111, 111); // , "{numdivide}");
		requiredEscapes.put(112, 112); // , "{f1}");
		requiredEscapes.put(113, 113); // , "{f2}");
		requiredEscapes.put(114, 114); // , "{f3}");
		requiredEscapes.put(115, 115); // , "{f4}");
		requiredEscapes.put(116, 116); // , "{f5}");
		requiredEscapes.put(117, 117); // , "{f6}");
		requiredEscapes.put(118, 118); // , "{f7}");
		requiredEscapes.put(119, 119); // , "{f8}");
		requiredEscapes.put(120, 120); // , "{f9}");
		requiredEscapes.put(121, 121); // , "{f10}");
		requiredEscapes.put(122, 122); // , "{f11}");
		requiredEscapes.put(123, 123); // , "{f12}");
		requiredEscapes.put(124, 124); // , "{f13}");
		requiredEscapes.put(125, 125); // , "{f14}");
		requiredEscapes.put(126, 126); // , "{f15}");
		requiredEscapes.put(127, 127); // , "{f16}");
		requiredEscapes.put(144, 144); // , "{numlock}");
		requiredEscapes.put(145, 145); // , "{scrolllock}");
		requiredEscapes.put(160, 160); // , "+<(");
		requiredEscapes.put(161, 161); // , "+>(");
		requiredEscapes.put(162, 162); // , "^<(");
		requiredEscapes.put(163, 163); // , "^>(");
		requiredEscapes.put(164, 164); // , "%<(");
		requiredEscapes.put(165, 165); // , "%>(");
		requiredEscapes.put(186, 186); // , ";");
		requiredEscapes.put(187, 187); // , "=");
		requiredEscapes.put(188, 188); // , ",");
		requiredEscapes.put(189, 189); // , "-");
		requiredEscapes.put(190, 190); // , ".");
		requiredEscapes.put(191, 191); // , "/");
		requiredEscapes.put(192, 192); // , "`");
		requiredEscapes.put(220, 220); // , "\\");
		requiredEscapes.put(221, 221); // , "]");
		requiredEscapes.put(219, 219); // , "[");
		requiredEscapes.put(222, 222); // , "\'");

		requiredEscapes.put(VK_BACK_SPACE, VK_BACK_SPACE); // ;
		requiredEscapes.put(VK_CANCEL, VK_CANCEL); // ;
		requiredEscapes.put(VK_CAPS_LOCK, VK_CAPS_LOCK); // ;
		requiredEscapes.put(VK_CLEAR, VK_CLEAR); // ;
		requiredEscapes.put(VK_DELETE, VK_DELETE); // ;
		requiredEscapes.put(VK_DOWN, VK_DOWN); // ;
		requiredEscapes.put(VK_END, VK_END); // ;
		requiredEscapes.put(VK_ENTER, VK_ENTER); // ;
		requiredEscapes.put(VK_ESCAPE, VK_ESCAPE); // ;
		requiredEscapes.put(VK_F1, VK_F1); // ;
		requiredEscapes.put(VK_F2, VK_F2); // ;
		requiredEscapes.put(VK_F3, VK_F3); // ;
		requiredEscapes.put(VK_F4, VK_F4); // ;
		requiredEscapes.put(VK_F5, VK_F5); // ;
		requiredEscapes.put(VK_F6, VK_F6); // ;
		requiredEscapes.put(VK_F7, VK_F7); // ;
		requiredEscapes.put(VK_F8, VK_F8); // ;
		requiredEscapes.put(VK_F9, VK_F9); // ;
		requiredEscapes.put(VK_F10, VK_F10); // ;
		requiredEscapes.put(VK_F11, VK_F11); // ;
		requiredEscapes.put(VK_F12, VK_F12); // ;
		requiredEscapes.put(VK_F13, VK_F13); // ;
		requiredEscapes.put(VK_F14, VK_F14); // ;
		requiredEscapes.put(VK_F15, VK_F15); // ;
		requiredEscapes.put(VK_F16, VK_F16); // ;
		requiredEscapes.put(VK_F17, VK_F17); // ;
		requiredEscapes.put(VK_F18, VK_F18); // ;
		requiredEscapes.put(VK_F19, VK_F19); // ;
		requiredEscapes.put(VK_F20, VK_F20); // ;
		requiredEscapes.put(VK_F21, VK_F21); // ;
		requiredEscapes.put(VK_F22, VK_F22); // ;
		requiredEscapes.put(VK_F23, VK_F23); // ;
		requiredEscapes.put(VK_F24, VK_F24); // ;
		requiredEscapes.put(VK_HELP, VK_HELP); // ;
		requiredEscapes.put(VK_HOME, VK_HOME); // ;
		requiredEscapes.put(VK_INSERT, VK_INSERT); // ;
		requiredEscapes.put(VK_LEFT, VK_LEFT); // ;

		requiredEscapes.put(VK_NUMPAD0, VK_NUMPAD0); // ;
		requiredEscapes.put(VK_NUMPAD1, VK_NUMPAD1); // ;
		requiredEscapes.put(VK_NUMPAD2, VK_NUMPAD2); // ;
		requiredEscapes.put(VK_NUMPAD3, VK_NUMPAD3); // ;
		requiredEscapes.put(VK_NUMPAD4, VK_NUMPAD4); // ;
		requiredEscapes.put(VK_NUMPAD5, VK_NUMPAD5); // ;
		requiredEscapes.put(VK_NUMPAD6, VK_NUMPAD6); // ;
		requiredEscapes.put(VK_NUMPAD7, VK_NUMPAD7); // ;
		requiredEscapes.put(VK_NUMPAD8, VK_NUMPAD8); // ;
		requiredEscapes.put(VK_NUMPAD9, VK_NUMPAD9); // ;
		requiredEscapes.put(VK_DECIMAL, VK_DECIMAL); // ;

		requiredEscapes.put(VK_DIVIDE, VK_DIVIDE); // ;
		requiredEscapes.put(VK_NUM_LOCK, VK_NUM_LOCK); // ;
		requiredEscapes.put(VK_SUBTRACT, VK_SUBTRACT); // ;
		requiredEscapes.put(VK_MULTIPLY, VK_MULTIPLY); // ;
		requiredEscapes.put(VK_ADD, VK_ADD); // ;
		requiredEscapes.put(VK_PAGE_DOWN, VK_PAGE_DOWN); // ;
		requiredEscapes.put(VK_PAGE_UP, VK_PAGE_UP); // ;
		requiredEscapes.put(VK_PRINTSCREEN, VK_PRINTSCREEN); // ;
		requiredEscapes.put(VK_RIGHT, VK_RIGHT); // ;
		requiredEscapes.put(VK_SCROLL_LOCK, VK_SCROLL_LOCK); // ;
		requiredEscapes.put(VK_TAB, VK_TAB); // ;
		requiredEscapes.put(VK_UP, VK_UP); // ;

	}

	public boolean requiresEscape(int scanCode) {
		createRequiredEscapesTable();
		return requiredEscapes.containsKey(scanCode);
	}

	public String scanToScript(int scanCode) {

		return scanToScriptTable.get(new Integer(scanCode));
	}

	@Override
	public String toString() {

		Set<Entry<String, Integer>> entrySet = scriptToScanTable.entrySet();
		ArrayList<Entry<String, Integer>> list = new ArrayList<>(entrySet);
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				int rv = o1.getValue().intValue() - o2.getValue().intValue();
				if (rv == 0)
					return o1.getKey().compareTo(o2.getKey());
				return rv;
			}
		});

		StringBuffer buffer = new StringBuffer();
		int i = 0;
		for (Entry<String, Integer> entry : list) {
			if (i > 0)
				buffer.append(",\n");
			i = 1;
			buffer.append("[");
			buffer.append(entry.getValue());
			buffer.append("] = \"");
			buffer.append(entry.getKey());
			buffer.append("\"");
		}

		return buffer.toString();

	}

	public String toStringRev() {

		Set<Entry<Integer, String>> entrySet = scanToScriptTable.entrySet();
		ArrayList<Entry<Integer, String>> list = new ArrayList<>(entrySet.size());
		Collections.sort(list, new Comparator<Entry<Integer, String>>() {

			@Override
			public int compare(Entry<Integer, String> o1, Entry<Integer, String> o2) {
				int rv = o1.getKey().intValue() - o2.getKey().intValue();
				return rv;
			}
		});

		StringBuffer buffer = new StringBuffer();
		int i = 0;
		for (Entry<Integer, String> entry : list) {
			if (i > 0)
				buffer.append(",\n");
			i = 1;

			buffer.append("[");
			buffer.append(entry.getValue());
			buffer.append("] = \"");
			buffer.append(entry.getKey());
			buffer.append("\"");
		}

		return buffer.toString();

	}

}

package sgi.javaMacros.model;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Hashtable;

public class KeyNameProvider {
	public static String getKeyName(int scanCode) {

		if ((scanCode & 0x01000000) != 0) {
			return String.valueOf((char) (scanCode ^ 0x01000000));

		}
		Integer key = new Integer(scanCode);

		String string = names.get(key);
		if (string != null) {
			return string;
		}
		String keyText = KeyEvent.getKeyText(scanCode);

		if (keyText.indexOf(" keyCode: 0x") >= 0)
			keyText = "{" + scanCode + "}";

		names.put(key, keyText);

		return keyText;
	}

	private static Hashtable<Integer, String> names = new Hashtable<>(150);
	
	static {

		Field[] declaredFields = KeyEvent.class.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			Field field = declaredFields[i];
			if (field.getName().startsWith("VK_")) {
				try {
					field.setAccessible(true);
					Object object = field.get(null);
					if (object instanceof Integer) {
						Integer inte = (Integer) object;
						StringBuffer bO = new StringBuffer(field.getName().substring(3).toLowerCase());
						//bO.setCharAt(0, Character.toUpperCase(bO.charAt(0)));
						names.put(inte, bO.toString());
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {

				}
			}
		}
		
		names.put(0, "gamebutton1");
		names.put(1, "gamebutton2");
		names.put(2, "gamebutton3");
		names.put(3, "gamebutton4");
		names.put(4, "backspace");
		names.put(13, "enter");
		names.put(19, "pause");
		names.put(20, "capslock");
		names.put(27, "escape");
		names.put(33, "pgup");
		names.put(34, "pgdn");
		names.put(35, "end");
		names.put(36, "home");
		names.put(37, "left");
		names.put(38, "up");
		names.put(39, "right");
		names.put(40, "down");
		names.put(44, "prtsc");
		names.put(45, "ins");
		names.put(46, "del");
		names.put(96, "num0");
		names.put(97, "num1");
		names.put(98, "num2");
		names.put(99, "num3");
		names.put(100, "num4");
		names.put(101, "num5");
		names.put(102, "num6");
		names.put(103, "num7");
		names.put(104, "num8");
		names.put(105, "num9");
		names.put(106, "nummultiply");
		names.put(107, "numplus");
		names.put(109, "numminus");
		names.put(110, "numdecimal");
		names.put(111, "numdivide");
		names.put(112, "f1");
		names.put(113, "f2");
		names.put(114, "f3");
		names.put(115, "f4");
		names.put(116, "f5");
		names.put(117, "f6");
		names.put(118, "f7");
		names.put(119, "f8");
		names.put(120, "f9");
		names.put(121, "f10");
		names.put(122, "f11");
		names.put(123, "f12");
		names.put(124, "f13");
		names.put(125, "f14");
		names.put(126, "f15");
		names.put(127, "f16");
		names.put(128, "f17");
		names.put(129, "f18");
		names.put(130, "f19");
		names.put(131, "f20");
		names.put(132, "f21");
		names.put(133, "f22");
		names.put(134, "f23");
		names.put(135, "f24");
		names.put(144, "numlock");
		names.put(145, "scrolllock");

		
		
		
		
		
		
		
		
		
		
		
		
	}
/*
	public static void main(String[] args) {
		JavaMacrosMemory mem = JavaMacrosMemory.instance();
		DeviceSet deviceSet = mem.getDeviceSet();
		for (Device device : deviceSet) {=
			KeySet keySet = device.getKeySet();
			for (Key key : keySet) {
				key.setName(getKeyName(key.getScanCode()));
			}
		}
		mem.storeToFile();
		
	}
 */
}

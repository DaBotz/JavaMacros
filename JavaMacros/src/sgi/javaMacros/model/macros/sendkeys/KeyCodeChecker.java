package sgi.javaMacros.model.macros.sendkeys;


import java.awt.event.KeyEvent;
import java.util.HashSet;

public class KeyCodeChecker {

	private static HashSet<Integer> validKeyCodes = new HashSet<>();

	static {
		validKeyCodes.add(3);
		validKeyCodes.add(8);
		validKeyCodes.add(9);
		validKeyCodes.add(10);
		validKeyCodes.add(12);
		validKeyCodes.add(16);
		validKeyCodes.add(17);
		validKeyCodes.add(18);
		validKeyCodes.add(19);
		validKeyCodes.add(20);
		validKeyCodes.add(27);
		validKeyCodes.add(28);
		validKeyCodes.add(29);
		validKeyCodes.add(32);
		validKeyCodes.add(33);
		validKeyCodes.add(34);
		validKeyCodes.add(35);
		validKeyCodes.add(36);
		validKeyCodes.add(37);
		validKeyCodes.add(38);
		validKeyCodes.add(39);
		validKeyCodes.add(40);
		validKeyCodes.add(44);
		validKeyCodes.add(45);
		validKeyCodes.add(46);
		validKeyCodes.add(47);
		validKeyCodes.add(48);
		validKeyCodes.add(49);
		validKeyCodes.add(50);
		validKeyCodes.add(51);
		validKeyCodes.add(52);
		validKeyCodes.add(53);
		validKeyCodes.add(54);
		validKeyCodes.add(55);
		validKeyCodes.add(56);
		validKeyCodes.add(57);
		validKeyCodes.add(59);
		validKeyCodes.add(61);
		validKeyCodes.add(65);
		validKeyCodes.add(66);
		validKeyCodes.add(67);
		validKeyCodes.add(68);
		validKeyCodes.add(69);
		validKeyCodes.add(70);
		validKeyCodes.add(71);
		validKeyCodes.add(72);
		validKeyCodes.add(73);
		validKeyCodes.add(74);
		validKeyCodes.add(75);
		validKeyCodes.add(76);
		validKeyCodes.add(77);
		validKeyCodes.add(78);
		validKeyCodes.add(79);
		validKeyCodes.add(80);
		validKeyCodes.add(81);
		validKeyCodes.add(82);
		validKeyCodes.add(83);
		validKeyCodes.add(84);
		validKeyCodes.add(85);
		validKeyCodes.add(86);
		validKeyCodes.add(87);
		validKeyCodes.add(88);
		validKeyCodes.add(89);
		validKeyCodes.add(90);
		validKeyCodes.add(91);
		validKeyCodes.add(92);
		validKeyCodes.add(93);
		validKeyCodes.add(96);
		validKeyCodes.add(97);
		validKeyCodes.add(98);
		validKeyCodes.add(99);
		validKeyCodes.add(100);
		validKeyCodes.add(101);
		validKeyCodes.add(102);
		validKeyCodes.add(103);
		validKeyCodes.add(104);
		validKeyCodes.add(105);
		validKeyCodes.add(106);
		validKeyCodes.add(107);
		validKeyCodes.add(108);
		validKeyCodes.add(109);
		validKeyCodes.add(110);
		validKeyCodes.add(111);
		validKeyCodes.add(112);
		validKeyCodes.add(113);
		validKeyCodes.add(114);
		validKeyCodes.add(115);
		validKeyCodes.add(116);
		validKeyCodes.add(117);
		validKeyCodes.add(118);
		validKeyCodes.add(119);
		validKeyCodes.add(120);
		validKeyCodes.add(121);
		validKeyCodes.add(122);
		validKeyCodes.add(123);
		validKeyCodes.add(127);
		validKeyCodes.add(144);
		validKeyCodes.add(145);
		validKeyCodes.add(154);
		validKeyCodes.add(155);
		validKeyCodes.add(156);
		validKeyCodes.add(192);
		validKeyCodes.add(222);
		validKeyCodes.add(240);
		validKeyCodes.add(241);
		validKeyCodes.add(242);
		validKeyCodes.add(243);
		validKeyCodes.add(244);
		validKeyCodes.add(245);
		validKeyCodes.add(256);
		validKeyCodes.add(257);
		validKeyCodes.add(258);
		validKeyCodes.add(263);
		validKeyCodes.add(524);
		validKeyCodes.add(525);
		validKeyCodes.add(61440);
		validKeyCodes.add(61441);
		validKeyCodes.add(61442);
		validKeyCodes.add(61443);
		validKeyCodes.add(61444);
		validKeyCodes.add(61445);
		validKeyCodes.add(61446);
		validKeyCodes.add(61447);
		validKeyCodes.add(61448);
		validKeyCodes.add(61449);
		validKeyCodes.add(61450);
		validKeyCodes.add(61451);
	}

	public static boolean isValidkeyCode(int keyCode) {
		return validKeyCodes.contains(keyCode);
	}

	public static int getAcceptableKeyCode(KeyEvent event) {
		int ekc = event.getExtendedKeyCode();
		
		if (isValidkeyCode(ekc))
			return ekc;
		else
			return event.getKeyCode();

	}

}

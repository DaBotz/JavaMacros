package sgi.javaMacros.model.enums;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import sgi.javaMacros.msgs.Messages;

public enum ModifierMasks {

	NONE, SHIFT, ALT,
	ALT_GRAPH, 
	CTRL,
	//META, 
	
	ALWAYS_INVOKED;

	private static final int _0XFFFFFFFF = 0xffffffff;

	public int bitMask() {
		return (this == ALWAYS_INVOKED) ? _0XFFFFFFFF : //
				(this == NONE) ? 0 //
						: 1 << ordinal() - 1;

	}

	public int setBit(int modifier) {

		modifier = modifier | bitMask();

		return modifier;
	}

	public boolean isSet(int modifierMask) {
		if (modifierMask == _0XFFFFFFFF)
			return true;
		int bitMask = bitMask();
		if (bitMask == 0xffffffff)
			return bitMask == modifierMask;

		return bitMask == modifierMask || (bitMask & modifierMask) != 0;

	}

	public String getLabelText() {
		return Messages.M._$(getDeclaringClass().getName() + "." + name());
	}

	public boolean isNone() {
		return this == ModifierMasks.NONE;
	}

	public boolean isExtreme() {
		return this == ModifierMasks.NONE || this == ModifierMasks.ALWAYS_INVOKED;

	}

	public static ModifierMasks[] values(boolean avoidExtremes) {
		if (!avoidExtremes)
			return values();

		ArrayList<ModifierMasks> arrayList = new ArrayList<>();
		for (ModifierMasks mm : values()) {
			if (!mm.isExtreme())
				arrayList.add(mm);
		}
		ModifierMasks[] mms = new ModifierMasks[arrayList.size()];
		arrayList.toArray(mms);
		return mms;
	}

	public static boolean isAlwaysInvoked(int modifiersMask) {
		return modifiersMask == _0XFFFFFFFF;
	}

	public int getScanCode() {
		switch (this) {
		case ALT:
			return KeyEvent.VK_ALT;
		case CTRL:
			return KeyEvent.VK_CONTROL;
//		case META:
//			return KeyEvent.VK_META;
		case SHIFT:
			return KeyEvent.VK_SHIFT;
		case ALT_GRAPH:
			return KeyEvent.VK_ALT_GRAPH;
		case ALWAYS_INVOKED:
		case NONE:
		}

		return 0;

	}

	public static String getText(int modifiersMask) {
		StringBuffer b = new StringBuffer(//
				Messages.M._$("sgi.javaMacros.model.enums.ModifierMasks.getText"));
		ModifierMasks[] values = values(true);
		for (int i = 0; i < values.length; i++) {
			ModifierMasks mm = values[i];
			if (mm.isSet(modifiersMask)) {
				if (i > 0)
					b.append(",");
				b.append(" ");
				b.append(Messages.ucFirst(mm.name()));
			}

		}

		return b.toString();
	}

	public boolean isAlways() {
		return this== ALWAYS_INVOKED;
	}

}

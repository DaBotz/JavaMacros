package sgi.javaMacros.model.macros.sendkeys;

import sgi.javaMacros.model.macros.sendkeys.actions.EscapeSequenceAction;

public class LMCEscapeSequenceAction extends EscapeSequenceAction {

	public LMCEscapeSequenceAction(int scanCode, int repeats, int hold) {
		super(scanCode, repeats, hold);
		// TODO Auto-generated constructor stub
	}

	public LMCEscapeSequenceAction(int scanCode) {
		super(scanCode);

	}
	private transient String name;
	
	
	@Override
	public String toString() {
		if (name == null) {
			int scanCode2 = getScanCode();
			if (scanCode2 == 32)
				name = " ";
			name = LuaMacrosCodeTable.getCodeTable(false).scanToScript(scanCode2);
			if( name!= null && name.startsWith("{")) {
				
				name = name.replaceAll("[\\{\\}]","");
			}
		}

		int repeats= getRepeats(); 
		
		
		return "{"//
				+ name//
				+ (repeats > 1 ? (" " + repeats) : "") //
				+ "}";
	}

	
	

}

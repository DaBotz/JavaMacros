package sgi.javaMacros.model.macros.sendkeys;

import java.util.ArrayList;

import sgi.javaMacros.model.macros.sendkeys.actions.RobotAction;

public class ScanEvent {

	private LuaMacrosScriptScanner source;
	private ArrayList<RobotAction> keyActions;
	private String originalText;

	public String getOriginalText() {
		return originalText;
	}

	public ScanEvent(LuaMacrosScriptScanner luaMacrosScriptScanner,String originalText,  ArrayList<RobotAction> parseList) {
		
		source = luaMacrosScriptScanner;
		this.originalText= originalText; 
		this.keyActions= parseList; 
		
	}
	
	public ArrayList<RobotAction> getKeyActions() {
		return keyActions;
	}


	public LuaMacrosScriptScanner getSource() {
		return source;
	}

}

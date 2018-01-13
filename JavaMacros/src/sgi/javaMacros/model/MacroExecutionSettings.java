package sgi.javaMacros.model;

import sgi.configuration.ConfigurationAtom;

public class MacroExecutionSettings extends ConfigurationAtom{
	
	private String pasteSequence;
	private long maxNumberOfDeletedMacros;


	public String getPasteSequence() {
		return pasteSequence;
	}

	public void setPasteSequence(String pasteSequence) {
		this.pasteSequence = pasteSequence;
	} 
	
	public void init() {
		
		pasteSequence="^v";
		maxNumberOfDeletedMacros= 1000; 
	}

	public long getMaxNumberOfDeletedMacros() {
		return maxNumberOfDeletedMacros;
	}

}

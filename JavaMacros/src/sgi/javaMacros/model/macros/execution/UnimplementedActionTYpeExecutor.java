package sgi.javaMacros.model.macros.execution;

import sgi.javaMacros.controller.LuaEvent;

public class UnimplementedActionTYpeExecutor extends Executor {

	private String requiredClass;
	private String message;

	UnimplementedActionTYpeExecutor(String className, String SimpleName) {
		this.requiredClass= className; 
		this.message= "You need to create an executing/model/input class for this action type";
		
		System.out.println("Neesd to create "+requiredClass+""
				+ "\n public class "+SimpleName+" extends Executor{"
				+ "\n"
				+ "\n}"
				+ "\n"
				+ "\n");
	}


	public String getRequiredClass() {
		return requiredClass;
	}

	public String getMessage() {
		return message;
	}

	public void setRequiredClass(String requiredClass) {

	}
	
	@Override
	public boolean isUnimplemented() {

	return true;
	}

	public void setMessage(String message) {

}


	@Override
	public int execute(LuaEvent event) {
		return FAIL;
	}


	@Override
	public Executor copyMe() {
		return null;
	}
	

}

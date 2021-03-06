package sgi.javaMacros.model.macros.sendkeys.actions;

import java.util.ArrayList;
import java.util.Iterator;

public class GroupClose extends RobotAction {
	public GroupClose() {
		super( -1); 
	}

	@Override
	protected boolean execute(Iterator<RobotAction> itera) {
		return true;
	}

	@Override
	public String toString() {
		
		return ")";
	}
	
	@Override
	public RobotActionType getType() {
		return RobotActionType.GROUP_CLOSURE;    
	}

	@Override
	public void atomicElements(ArrayList<RobotAction> list) {
		list.add(this);
	}

}

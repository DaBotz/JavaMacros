package sgi.javaMacros.model.macros.sendkeys.actions;

import java.util.ArrayList;
import java.util.Iterator;

public class GroupOpen extends RobotAction {
	public GroupOpen() {
		super( -1); 
	}

	@Override
	public boolean execute(Iterator<RobotAction> itera) {
		return true;
	}
	
	@Override
	public String toString() {
		
		return "(";
	}
	@Override
	public RobotActionType getType() {
		return RobotActionType.GROUP_OPEN;   
	}
	
	@Override
	public void atomicElements(ArrayList<RobotAction> list) {
		list.add(this);
	}


}

package sgi.javaMacros.model.internal;

import sgi.javaMacros.model.ConfigChangeType;
import sgi.javaMacros.model.enums.ActionType;
import sgi.javaMacros.model.enums.KeyDirection;
import sgi.javaMacros.model.events.ConfigChangeEvent;

public class Macro extends ConfigAtom {

	private ActionType actionType = null;
	private String activeKeys;
	private int winUId;
	private KeyDirection event = null;
	private String payLoad;
	
	
	
	/**
	 * @deprecated
	 */
	public Macro() {
		super();
	}
	public ActionType getAction() {
		return actionType;
	}
	public ActionType getActionType() {
		return actionType;
	}
	public String getActiveKeys() {
		return activeKeys;
	}

	public int getWinUId() {
		return winUId;
	}

	public KeyDirection getEvent() {
		return event;
	}


	public String getPayLoad() {
		return payLoad;
	}

	public void setAction(ActionType actionType) {
		ActionType dd2 = this.actionType;
		this.actionType = actionType;
		fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM, this, "actionType", dd2));
	}

	public void setActionType(ActionType actionType) {
		ActionType dd2 = this.actionType;
		this.actionType = actionType;
		fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM, this, "actionType", dd2));
	}

	public void setActiveKeys(String activeKeys) {
		this.activeKeys = activeKeys;
	}

	public void setWinUId(int WinUI) {
		int dd2 = this.winUId;
		this.winUId = WinUI;
		fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM, this, "name", dd2));
	}

	public void setEvent(KeyDirection direction) {
		KeyDirection dd2 = this.event;
		this.event = direction;
		fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM, this, "direction", dd2));
	}



	public void setPayLoad(String payLoad) {
		String dd2 = this.payLoad;
		this.payLoad = payLoad;
		fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM, this, "payLoad", dd2));
	}


}

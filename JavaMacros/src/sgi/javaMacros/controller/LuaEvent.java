package sgi.javaMacros.controller;

import sgi.javaMacros.model.internal.Device;

public class LuaEvent {

	private String luaId;
	private String deviceName;
	public String getLuaId() {
		return luaId;
	}

	public int getScanCode() {
		return scanCode;
	}

	public boolean isDown() {
		return down;
	}


	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	private int scanCode;
	private int deviceType;
	
	private boolean down;
	private Device device;

	public LuaEvent(String sourceId, int scanCode, boolean down, int deviceType) {
		this.deviceName = this.luaId = sourceId;
		this.scanCode = scanCode;
		this.down = down;
		this.deviceType= deviceType; 
	}

	public void setDevice(Device device) {
		this.device= device; 
		
	}

	public Device getDevice() {
		return device;
	}

}

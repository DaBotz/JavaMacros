package sgi.javaMacros.model.internal;

public interface IDevice {

	void setName(String name);

	Key findKey(int scanCode);


	boolean isDetected();

	String getType();

	boolean isEnabled();

	String getName();

}
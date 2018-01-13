package sgi.javaMacros.model.internal;

import sgi.os.WindowData;

public class ManualUseCase extends UseCase {

	@SuppressWarnings("deprecation")
	public ManualUseCase() {
	}

	public ManualUseCase(String name) {
		super(name);
	}

	@Override
	public int compareTo(UseCase o) {
		return super.compareTo(o);
	}
	
	
	
	@Override
	public UseCase doACopy() {
		ManualUseCase onTitle= new ManualUseCase(); 
		onTitle.setName(getName());
		onTitle.creationTime= getCreationTime(); 
		onTitle.setPriority(getPriority());
		return onTitle;
	}
	
	@Override
	public boolean isManual() {
	return true; 
	}
	@Override
	protected boolean rulesMatch(WindowData activeWindow, UseCase currentUseCase) {
		return currentUseCase == this;
	}

}

package sgi.javaMacros.model.internal.defaults;

import sgi.configuration.IConfigurationAtom;
import sgi.javaMacros.model.internal.UseCase;
import sgi.javaMacros.msgs.Messages;
import sgi.os.WindowData;

public class BasicUseCase extends UseCase {

	private transient boolean open;
	@Override
	public long getCreationTime() {
		super. creationTime = UseCase.BASIC_CASE_TIMESTAMP;
		return creationTime;
	}

	@SuppressWarnings("deprecation")
	public BasicUseCase() {

		super();
		open = true;
		setEnabled(true);
		setName(Messages.M._$("UseCase.BASIC"));
		open = false;
	}
	
	
	
	@Override
	public void relink(IConfigurationAtom parent) {
		super. creationTime = -1;
		super.relink(parent);
	}
	

	@Override
	public void setEnabled(boolean enabled) {
		if (!open)
			return;
		super.setEnabled(enabled);
	}

	@Override
	public void setName(String name) {
		if (!open)
			return;
		super.setName(name);
	}

	@Override
	public boolean equals(Object o1) {
		return o1 != null && o1 instanceof BasicUseCase;
	}

	@Override
	public int hashCode() {
		return "BASIC".hashCode();
	}

	@Override
	public int compareTo(UseCase o) {
		return !equals(o) ? 1: 0;
	}
	@Override
	public boolean isBasic() {
		return true;
	}

	@Override
	protected boolean rulesMatch(WindowData activeWindow, UseCase currentUseCase) {
		return true;
	}

	public boolean isActivable(String string, String lastTitle) {
		return true;
	}

	@Override
	public UseCase doACopy() {
		return new BasicUseCase();
	}
	
	@Override
	public boolean isManual() {
		return true;
	}

}
package sgi.javaMacros.model.internal;

import sgi.gui.configuration.ILabelProvider;
import sgi.javaMacros.model.enums.UseCaseType;
import sgi.os.WindowData;

public class AutomaticUseCase extends UseCase implements Comparable<UseCase>, ILabelProvider {

	private String rule = "";



	public AutomaticUseCase(String name) {
		super(name);
	}

	/**
	 * @deprecated Reserved for serialization only
	 */
	public AutomaticUseCase() {

	}

	@Override
	public int compareTo(UseCase o) {
		if (o.isBasic()|o.isManual())
			return -1;

		int rw = o.getPriority().ordinal() - getPriority().ordinal();

		if (rw != 0)
			return rw;

		if (o instanceof AutomaticUseCase) {
			rw = ((AutomaticUseCase) o).getType().ordinal() - getType().ordinal();
		}

		if (rw != 0)
			return rw;

		return String.valueOf(getName()).compareTo(o.getName());
	}

	public UseCaseType getType() {
		return UseCaseType.WINDOWS_CLASS;
	}

	public String getRule() {
		return rule;
	}


	public void setRule(String rule) {
		String rule2 = this.rule;
		this.rule = rule;
		notifyPropertyChange("rule", rule2, rule);
	}

	@Override

	public String getLabel() {

		return getName()

				+ " ( "//
				+ getPriority().ordinal()//

				+ " )";

	}


	@Override
	protected boolean rulesMatch(WindowData activeWindow, UseCase currentUseCase ) {
		return rule.equals(activeWindow.getWindowClasses().toArray()[0]);

	}
	
	public boolean isActivable(String string, String lastTitle) {
		return rule.equals(string); 
	}
	@Override
	public UseCase doACopy() {
		AutomaticUseCase onTitle= new AutomaticUseCase(); 
		onTitle.setName(getName());
		onTitle.setRule(getRule());
		onTitle.creationTime= getCreationTime(); 
		onTitle.setPriority(getPriority());
		return onTitle;
	}


}

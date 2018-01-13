package sgi.javaMacros.model.internal;

import java.util.regex.Pattern;

import sgi.gui.configuration.ILabelProvider;
import sgi.javaMacros.model.enums.UseCaseMatchMode;
import sgi.javaMacros.model.enums.UseCaseType;
import sgi.javaMacros.msgs.Messages;
import sgi.os.WindowData;

public class AutomaticUseCaseOnTitle extends AutomaticUseCase implements Comparable<UseCase>, ILabelProvider {

	private UseCaseMatchMode matchMode = UseCaseMatchMode.BEGIN;

	public AutomaticUseCaseOnTitle(String name) {
		super(name);
	}

	/**
	 * @deprecated Reserved for serialization only
	 */
	public AutomaticUseCaseOnTitle() {

	}

	@Override
	public int compareTo(UseCase o) {
		if (o.isBasic())
			return -1;

		int rw = o.getPriority().ordinal() - getPriority().ordinal();

		if (rw != 0)
			return rw;

		if (o instanceof AutomaticUseCaseOnTitle) {
			rw = ((AutomaticUseCaseOnTitle) o).getType().ordinal() - getType().ordinal();
		}

		if (rw != 0)
			return rw;

		return String.valueOf(getName()).compareTo(o.getName());
	}

	public UseCaseType getType() {
		return UseCaseType.WINDOWS_CLASS;
	}

	@Override

	public String getLabel() {

		return getName()

				+ " ( "//
				+ getPriority().ordinal()//

				+ " )";

	}

	public UseCaseMatchMode getMatchMode() {
		return matchMode;
	}

	public void setMatchMode(UseCaseMatchMode matchMode) {
		UseCaseMatchMode oldMode = this.matchMode;
		this.matchMode = matchMode;
		notifyPropertyChange("matchMode", oldMode, matchMode);

	}

	@Override
	protected String moreTooltipData() {

		String rv = "";
		rv += ""//
				+ "  Match Mode: " + Messages.M._$("usecase.matchmodes." + getMatchMode().name()) + "\n"//
				+ "  Rule:       " + getRule() + "\n"//
				+ "";
		return rv;
	}

	@Override
	protected boolean rulesMatch(WindowData activeWindow, UseCase currentUseCase) {
		return isActivableOnTitle(activeWindow.getWindowTitle());
	}

	protected boolean isActivableOnTitle(String windowTitle) {
		if (matchMode == null)
			return false;

		String wintitle = String.valueOf(windowTitle);
		String rule = String.valueOf(getRule());

		if (matchMode.isIgnoreCase()) {
			wintitle = wintitle.toLowerCase();
			rule = rule.toLowerCase();
		}

		switch (matchMode) {
		case BEGIN_IGNORE_CASE:
		case BEGIN:
			return wintitle.indexOf(rule) == 0;

		case CONTAINS_IGNORE_CASE:
		case CONTAINS:
			return wintitle.indexOf(rule) >= 0;

		case ENDS_WITH_IGNORE_CASE:
		case ENDS_WITH:
			int lTitle = wintitle.length();
			int lRule = rule.length();
			return wintitle.indexOf(rule) == (lTitle - lRule);

		case EQUAL_IGNORE_CASE:
		case EQUAL:
			return wintitle.equals(rule);

		case REG_EXPRESION:
			return Pattern.matches(rule, windowTitle);

		}

		return false;
	}

	@Override
	public boolean isActivable(String wclass, String lastTitle) {
		return isActivableOnTitle(lastTitle);
	}
	
	@Override
	public UseCase doACopy() {
		AutomaticUseCaseOnTitle onTitle= new AutomaticUseCaseOnTitle(); 
		onTitle.setName(getName());
		onTitle.setRule(getRule());
		onTitle.matchMode= matchMode; 
		onTitle.creationTime= getCreationTime(); 
		onTitle.setPriority(getPriority());
		return onTitle;
	}

}

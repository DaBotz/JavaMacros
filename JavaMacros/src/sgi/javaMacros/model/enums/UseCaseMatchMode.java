package sgi.javaMacros.model.enums;

public enum UseCaseMatchMode {

	EQUAL, //
	EQUAL_IGNORE_CASE, //
	BEGIN, //
	BEGIN_IGNORE_CASE, //
	CONTAINS, //
	CONTAINS_IGNORE_CASE, //
	ENDS_WITH, //
	ENDS_WITH_IGNORE_CASE, //
	REG_EXPRESION;

	public boolean isIgnoreCase() {
		return name().endsWith("_IGNORE_CASE");
	}

}

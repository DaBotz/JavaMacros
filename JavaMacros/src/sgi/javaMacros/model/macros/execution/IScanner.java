package sgi.javaMacros.model.macros.execution;

import java.util.ArrayList;

public interface IScanner {

	public static final int ERROR = 0;
	public static final int NORMAL = 1;
	public static final int GROUP = 2;
	public static final int ESCAPE = 4;
	public static final int MODIFIER = 3;
	
	void scan(String text);

	ArrayList<ScriptSegment> getSegmentation();

}

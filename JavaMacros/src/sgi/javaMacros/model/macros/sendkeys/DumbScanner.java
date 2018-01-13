package sgi.javaMacros.model.macros.sendkeys;

import java.util.ArrayList;

import sgi.javaMacros.model.macros.execution.IScanner;
import sgi.javaMacros.model.macros.execution.ScriptSegment;
import sgi.javaMacros.model.macros.execution.executors.LUAMACROS_DIRECT_CODE;

public class DumbScanner implements IScanner {
	
	
	private static final int BOUNDARIES = IScanner.ESCAPE;

	private static final int UNINFLUENT = IScanner.MODIFIER;

	private static final int END_CODE_LENGTH = LUAMACROS_DIRECT_CODE.END_CODE.length();

	private static final int START_CODE_LENGTH = LUAMACROS_DIRECT_CODE.START_CODE.length();

	private static final int END_STRING_LENGTH = LUAMACROS_DIRECT_CODE.END_STRING.length();

	private static final int START_STRING_LENGTH = LUAMACROS_DIRECT_CODE.START_STRING.length();

	private ArrayList<ScriptSegment> segments = new ArrayList<>();

	private LuaMacrosScriptScanner subparser = new LuaMacrosScriptScanner(false);

	@Override
	public void scan(String text) {
		segments = new ArrayList<>();

		int indexOf = text.indexOf(LUAMACROS_DIRECT_CODE.START_STRING);
		if (indexOf >= 0) {
			String pre = text.substring(0, indexOf);
			segments.add(new ScriptSegment(pre, UNINFLUENT));

			segments.add(new ScriptSegment(LUAMACROS_DIRECT_CODE.START_STRING, BOUNDARIES));
			indexOf += START_STRING_LENGTH;
			text = text.substring(indexOf);

			indexOf = text.indexOf(LUAMACROS_DIRECT_CODE.END_STRING);
			if (indexOf >= 0) {
				String sub = text.substring(0, indexOf);
				subparser.scan(sub);

				segments.addAll(subparser.getSegmentation());

				segments.add(new ScriptSegment(LUAMACROS_DIRECT_CODE.END_STRING, BOUNDARIES));
				indexOf += END_STRING_LENGTH;
				text = text.substring(indexOf);
			}
		}
		
		
		indexOf = text.indexOf(LUAMACROS_DIRECT_CODE.START_CODE);
		if (indexOf >= 0) {
			String pre = text.substring(0, indexOf);
			segments.add(new ScriptSegment(pre, UNINFLUENT));

			segments.add(new ScriptSegment(LUAMACROS_DIRECT_CODE.START_CODE, BOUNDARIES));
			indexOf += START_CODE_LENGTH;
			text = text.substring(indexOf);

			indexOf = text.indexOf(LUAMACROS_DIRECT_CODE.END_CODE);
			if (indexOf >= 0) {
				String sub = text.substring(0, indexOf);
				subparser.scan(sub);
				segments.addAll(subparser.getSegmentation());
				segments.add(new ScriptSegment(LUAMACROS_DIRECT_CODE.END_CODE, BOUNDARIES));
				indexOf += END_CODE_LENGTH;
				text = text.substring(indexOf);
			}
		}
		segments.add(new ScriptSegment(text, GROUP));
		int splitIndex = 0;
		for (ScriptSegment seg : segments) {
			seg.setStart(splitIndex);
			splitIndex = seg.getEnd();
		}
	}

	@Override
	public ArrayList<ScriptSegment> getSegmentation() {
		return segments;
	}
}
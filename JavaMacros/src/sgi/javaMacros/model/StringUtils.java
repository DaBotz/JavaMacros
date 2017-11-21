package sgi.javaMacros.model;

public class StringUtils {
	public static  String plainReplace(String oldString, String newString,
			String buffer) {
		int nx = oldString.length();
		int index = buffer.indexOf(oldString);
		int length = newString.length();
		int failsafe = 1024;

		while (index >= 0 && failsafe > 0) {
			int beginIndex = index + nx;
			buffer = buffer.substring(0, index) + newString
					+ buffer.substring(beginIndex);
			index = buffer.indexOf(oldString, index + length);
			failsafe--;
		}

		return buffer;
	}
	
	
	public static String duoVal(int day) {
		return (day < 10 ? "0" : "") + day; //$NON-NLS-1$ //$NON-NLS-2$
	}

	
	public static String decamelizeExe(String str, boolean noExt) {
		if( str == null)return null; 
		if( str.length() <3 )return str; 
		
		StringBuffer exe2 = new StringBuffer(str);
		exe2.setCharAt(0, Character.toUpperCase(exe2.charAt(0)));
		
		for (int j = exe2.length() - 2; j > 1; j--) {
			int index = j + 1;
			char ch1 = exe2.charAt(j);
			char ch2 = exe2.charAt(index);

			if (ch1 == '_') {
				exe2.setCharAt(j, ' ');
			} else if (Character.isLowerCase(ch1) && Character.isUpperCase(ch2)) {
				exe2.insert(index, ' ');
			} else if (ch2 == '[') {
				switch(ch1){
					case '[': 
					case ']': 
					break; 	
					default:
						exe2.insert(index, ' ');
				}
			}

		}
		String title = exe2.toString();
		title = title.replaceAll("\\.((exe)|(EXE))(.*)",noExt?"": "$4, (exe)"); //$NON-NLS-1$ //$NON-NLS-2$
		title = title.replaceAll("\\.((com)|(COM))(.*)",noExt?"": "$4, (com)"); //$NON-NLS-1$ //$NON-NLS-2$
		title = title.replaceAll("\\.((bin)|(BIN))(.*)",noExt?"": "$4, (bin)"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return title;
	}



}

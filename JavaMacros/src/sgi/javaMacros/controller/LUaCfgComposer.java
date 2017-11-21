package sgi.javaMacros.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import sgi.javaMacros.model.JavaMacrosLuaMacrosConfiguration;
import sgi.javaMacros.model.lists.DeviceSet;

public class LUaCfgComposer extends LuaUpdater {

	private StringBuffer buffer;


	public String compose() {
		buffer = new StringBuffer();
		line("require 'JMacrosBase'");
		line(getJMAinit()); 
		return buffer.toString();
	}


	public LUaCfgComposer(JavaMacrosLuaMacrosConfiguration luaCfg, DeviceSet devices) {
		super(luaCfg, devices);
	}
	public String readFile(File f) throws IOException {
		if (!f.isFile())
			return "";
		BufferedReader reader = new BufferedReader(new FileReader(f));
		buffer = new StringBuffer();
		String _line = reader.readLine();
		do {
			line(_line);
		} while ((_line = reader.readLine()) != null);

		reader.close();
		return buffer.toString();
	}

	protected void line() {
		line("");
	}

	protected void line(String text) {
		buffer.append(text);
		buffer.append(crlf);
	}

//	private String comment(String string) {
//		return "-- " + string;
//	}

}

/*
 * 
 * JMA.avoid('47') JMA.avoid('6C') JMA.no_num_lock('89') JMA.no_num_lock('2E')
 * -- /devices
 * 
 * -- runtime JMA.scan()
 * 
 * 
 */

// public String compose() {
// buffer = new StringBuffer();
// line(comment("Automatically generated file : do not edit"));
// line(comment("###########################################"));
// line();
// line(comment("Appearance"));
// if (luaCfg.isLuaMacrosMinimizingToTray())
// line("lmc.minimizeToTray = true");
// if (luaCfg.isLuaMacrosStartingMinimized())
// line("lmc_minimize()");
// line(comment("/Appearance"));
// line();
// line("require 'JMacrosBase'");
// line();
// line(comment("Server"));
//
// long parameter = luaCfg.getServerPort();
// line(Commands.SET_PORT, parameter);
// line(comment("/Server"));
// line();
//
// line(comment("devices"));
//
// ArrayList<String> initInstructions = getInitInstructions();
// for (String string : initInstructions) {
// line(JMA + "." + string);
//
// }
//
// line(comment("/devices"));
// line();
//
// line(comment("runtime"));
// line(JMA + "." + getScanUpdate());
//
// line(comment("/runtime"));
// line(comment("###########################################"));
//
// return buffer.toString();
//
// }

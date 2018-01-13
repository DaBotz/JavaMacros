package sgi.javaMacros.model;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.util.HashSet;

import sgi.javaMacros.JavaMacrosLauncher;
import sgi.javaMacros.lua.LuascriptsRetriever;



public class LuaMacrosFinder {

	File found=null;
	private HashSet<File> crawled;
	private FileFilter filter = new FileFilter() {
		
		@Override
		public boolean accept(File f2) {
			if( f2.isDirectory()) return true; 
			
			if( f2.isFile()//
					&&"luamacros.exe".equalsIgnoreCase(f2.getName())){
				found= f2; 
			}
			return false;
		}
	}; 
	
	public void crawl(File f){
		if( crawled .contains(f)) return; 
		crawled.add(f); 
		File[] listFiles = f.listFiles(filter);
		for (int i = 0; i < listFiles.length; i++) {
			crawl( listFiles[i]);
		}
	}
	
	public static File findLuaMacros() {
		return new LuaMacrosFinder().findLuaMacros_Exe();
	}

	protected File findLuaMacros_Exe() {
		File file =null; 
		crawled= new HashSet<>(); 
		
		try {
			file = new File(JavaMacrosLauncher.class.//
					getProtectionDomain().getCodeSource().getLocation()//
					.toURI().getPath());
		} catch (Exception e) {
			try {
				file= new File(JavaMacrosLauncher.class.//
						getResource(".").toURI().getPath());
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			e.printStackTrace();
		}
		while(file != null && found==null){
			crawl(file);
			file= file.getParentFile(); 
		}
		crawled.clear();
		File folder= new File( found.getParentFile(), "lua"); 
		if(! folder.exists()){
			folder.mkdirs(); 
		}
		LuascriptsRetriever.placeIn(folder); 
		
		
		return found;
	}

}

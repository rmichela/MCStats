//Copyright (C) 2010  Ryan Michela
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;

/*
 * To use, embed your .updatr file into your .jar file as a resource and include this class in
 * your project. Call writeUpdatrFile with the path to your .updatr file relative to the root
 * of your .jar. writeUpdatrFile will write your .updatr file to the udatr directory if it exists.
 * 
 * If you are using a hosting system such as GitHub where all your source files are accessible from
 * the web, your .updatr file can reference its web accessible version from your repository as the
 * server side version.
 */
public class UpdatrWriter {
	
	private static final int BUF_LEN = 1024;
	
	public static void writeUpdatrFile(String updatrResourceName) {
		
		if(new File("updatr").exists()) {
			try {
				Reader stream = new InputStreamReader(UpdatrWriter.class.getResourceAsStream(updatrResourceName));
				FileWriter fw = new FileWriter("updatr/" + updatrResourceName);
				char[] cbuf = new char[BUF_LEN];
				int read = 0;
				
				// Copy the stream
				do {
					read = stream.read(cbuf);
					fw.write(cbuf);		
				} while (read == BUF_LEN);
				fw.close();
				
			} catch(Exception e) {
				
			}
		}
	}
}

package com.soebes.supose;

import java.net.URL;

public class TestBase {
	/**
	 * This method will give you back
	 * the filename incl. the absolute path name
	 * to the resource. 
	 * If the resource does not exist it will give
	 * you back the resource name incl. the path.
	 * 
	 * It will give you back an absolute path
	 * incl. the name which is in the same directory 
	 * as the the class you've called it from.
	 * 
	 * @param name
	 * @return
	 */
	public String getFileResource(String name) {
		URL url = this.getClass().getResource(name);
		if (url != null) {
			return url.getFile();
		} else {
			//We have a file which does not exists
			//We got the path
			url = this.getClass().getResource(".");
			return url.getFile() + name;
		}
	}

}

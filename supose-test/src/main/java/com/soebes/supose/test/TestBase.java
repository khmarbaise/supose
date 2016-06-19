/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008, 2009, 2010 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008, 2009, 2010 by Karl Heinz Marbaise
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html
 * If you have any questions about the Software or about the license
 * just write an email to license@soebes.de
 */
package com.soebes.supose.test;

import java.io.File;
import java.net.URL;

/**
 * @author Karl Heinz Marbaise
 */
public class TestBase {

	/**
	 * This method will give you back the filename incl. the absolute path name
	 * to the resource. If the resource does not exist it will give you back the
	 * resource name incl. the path.
	 *
	 * It will give you back an absolute path incl. the name which is in the
	 * same directory as the the class you've called it from.
	 *
	 * @param name
	 * @return
	 */
	public String getFileResource(String name) {
		URL url = this.getClass().getResource(name);
		if (url != null) {
			return url.getFile();
		} else {
			// We have a file which does not exists
			// We got the path
			url = this.getClass().getResource(".");
			return url.getFile() + name;
		}
	}

	/**
	 * Return the base directory of the project.
	 * 
	 * @return
	 */
	public String getMavenBaseDir() {
		// basedir is defined by Maven
		// but the above will not work under Eclipse.
		// So there I'M using user.dir
		return System.getProperty("basedir", System.getProperty("user.dir", "."));
	}

	/**
	 * Return the target directory of the current project.
	 * 
	 * @return
	 */
	public String getTargetDir() {
		return getMavenBaseDir() + File.separatorChar + "target";
	}

	/**
	 * This will give you back the position of a repository which is stored
	 * inside the <b>target</b> directory.
	 * 
	 * @return The directory where the repository has been stored.
	 */
	public String getRepositoryDirectory() {
		return getTargetDir() + File.separatorChar + "repos";
	}

	public String getRepositoryDirectory(String supplemental) {
		return getTargetDir() + File.separatorChar + "repos-" + supplemental;
	}

	/**
	 * This will give you back the position of an index directory which is
	 * stored inside the <b>target</b> directory.
	 * 
	 * @return The directory where the index is stored.
	 */
	public String getIndexDirectory() {
		return getTargetDir() + File.separatorChar + "index.Test";
	}

	public String getIndexDirectory(String supplemental) {
		return getTargetDir() + File.separatorChar + "index-" + supplemental + ".Test";
	}

	public String getSrcDirectory() {
		return getMavenBaseDir() + File.separator + "src";
	}

	public String getTestDirectory() {
		return getSrcDirectory() + File.separator + "test";
	}

	public String getTestResourcesDirectory() {
		return getTestDirectory() + File.separator + "resources";
	}
}

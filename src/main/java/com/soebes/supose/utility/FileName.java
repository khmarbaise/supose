/*
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (C) 2007 by Karl Heinz Marbaise

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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA *
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html
 * If you have any questions about the Software or about the license
 * just write an email to license@soebes.de
 *
 */
// SupoSE
package com.soebes.supose.utility;

import java.io.File;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class FileName {


	private String fileName;
    private String name;
    private String ext;
    private String baseName;
    private String path;
    
    public FileName(String fileName) {
    	setExt("");
    	setName("");
    	File f = new File(fileName);
    	setBaseName(f.getName());
    	if (getBaseName().lastIndexOf('.') > 0) {
    		setExt(getBaseName().substring(getBaseName().lastIndexOf('.') + 1));
    		setPath(fileName.substring(0, fileName.length() - getBaseName().length() - 1));
    	}
    	if (getExt().length() > 0) {
    		String nameOnly = getBaseName().substring(0, getBaseName().length() - getExt().length() - 1);
    	}

    	String NameOnly = (new File("foo/bar/baz")).getName();
    }
    
    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
    	return fileName;
    }

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
                                            
}

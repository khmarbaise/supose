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
package com.soebes.supose.utility;

import java.io.File;

/**
 * @author Karl Heinz Marbaise
 *
 * This class will support you to extract the <code>extension</code> of a 
 * file, the <code>path</code> and the <code>filename incl. the extension</code>.
 * 
 * We define a extension as one of the following:
 * <li></li>
 */
public class FileName {

	private String fileName;
    private String name;
    private String ext;
    private String baseName;
    private String path;
    private String nameWithoutExtension;
    
    public FileName(String fileName, boolean isDir) {
    	init(fileName, isDir);
    }
    public FileName(String fileName) {
    	init(fileName, false);
    }
	private void init(String fileName, boolean isDir) {
		setExt("");
    	setName("");
    	setBaseName("");
    	setNameWithoutExtension("");
    	setPath("");
    	if (!isDir) {
    		if (fileName.endsWith("/")) {
    			isDir = true;
    		}
    	}

    	File f = new File(fileName);
    	if (!isDir) {
    		setBaseName(f.getName());
    	} else {
    		setPath(fileName);
    	}

    	if (getBaseName().lastIndexOf('.') > 0) {
    		setExt(getBaseName().substring(getBaseName().lastIndexOf('.') + 1));
    		setPath(fileName.substring(0, fileName.length() - getBaseName().length()));
    	}

    	if (getExt().length() > 0) {
    		nameWithoutExtension = getBaseName().substring(0, getBaseName().length() - getExt().length() - 1);
    	}

    	//Now we check if we have things like ".tar.gz", ".tar.bz2" etc.
    	if (getNameWithoutExtension().lastIndexOf('.') > 0) {
    		String tar = getNameWithoutExtension().substring(getNameWithoutExtension().lastIndexOf('.') + 1);

    		//We only allow double extension in relationship with ".tar".
    		//If we don't reduce this, we would get things like ".i586.rpm" 
    		//or ".1.zip" etc. which we don't like to get. 
    		if (tar.equals("tar")) {
    			setExt(tar + "." + getExt());
    			nameWithoutExtension = getBaseName().substring(0, getBaseName().length() - getExt().length()  -1);
    		}
    	}
	}
    
    /**
     * @return The extension of a filename.
     */
    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    /**
     * @return The name of a file.
     */
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

	public String getNameWithoutExtension() {
		return nameWithoutExtension;
	}

	public void setNameWithoutExtension(String nameWithoutExtension) {
		this.nameWithoutExtension = nameWithoutExtension;
	}
                                            
}

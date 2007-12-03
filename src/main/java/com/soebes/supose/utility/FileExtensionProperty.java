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

import java.util.Properties;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class FileExtensionProperty {

	private static final String RESOURCE_NAME = "/fileextension.properties";

	private Properties properties = null;

	/*
	 */
	private static FileExtensionProperty instance = new FileExtensionProperty();

	/**
	 * Singleton access
	 * @return Instance of ReleaseProperties
	 */
	public static FileExtensionProperty getInstance() {
		return instance;
	}

	/**
	 * Will return the value of the given property of null, 
	 * if the particular property does not exist.
	 */
	public String getProperty(String propName) {
		String s = properties.getProperty(propName);
		if (s == null || s.length() == 0) {
			throw new RuntimeException("Das Property " + propName + " existiert nicht!");
		}
		return s;
	}

	/**
	 * Konstruktor
	 */
	private FileExtensionProperty() {
		super();

		try {
			properties = new Properties();
			properties.load(FileExtensionProperty.class.getResourceAsStream(RESOURCE_NAME));
		} catch (Exception e) {
			throw new RuntimeException("The property file " + RESOURCE_NAME + " couldn't be read.");
		}
	}

}

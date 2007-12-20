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
package com.soebes.supose.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.InvalidIniFormatException;


/**
 * @author Karl Heinz Marbaise
 *
 */
public class ConfigurationRepositories {
	private static Logger LOGGER = Logger.getLogger(ConfigurationRepositories.class);

	private String configFile;
	private Ini iniFile;

	public ConfigurationRepositories(String configFile) {
		setConfigFile(configFile);
		try {
			iniFile = new Ini(new FileInputStream(configFile));
		} catch (InvalidIniFormatException e) {
			LOGGER.error("The format of the given INI is not correct. " + e);
		} catch (IOException e) {
			LOGGER.error("Some problems happen with the INI File " + e);
		}
	}

	/**
	 * Get the names of the repositories.
	 * @return Array of all available repositories.
	 */
	public String[] getNames() {
		int size = iniFile.keySet().size();
		return (String[]) iniFile.keySet().toArray(new String[size]);
	}
	
	public RepositoryConfiguration getRepositoryConfiguration(String name) {
		LOGGER.debug("getRepositoryConfiguration(" + name + ")");
		return new RepositoryConfiguration(iniFile.get(name), name);
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public Ini getIniFile() {
		return iniFile;
	}

	public void setIniFile(Ini iniFile) {
		this.iniFile = iniFile;
	}
}

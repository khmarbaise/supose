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

import org.ini4j.Ini.Section;

import com.soebes.supose.config.ini.IniFileEntryNames;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class RepositoryConfiguration {

	private Section section;
	
	private String repositoryName;

	public RepositoryConfiguration(Section section, String repositoryName) {
		setRepositoryName(repositoryName);
		setSection(section);
	}

	public String getIndexUsername() {
		return section.get(IniFileEntryNames.INDEXUSERNAME);
	}

	public boolean existIndexUsername() {
		if (	getIndexUsername() == null 
			||	getIndexUsername().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void setIndexUsername(String username) {
		section.put(IniFileEntryNames.INDEXUSERNAME, username);
	}

	public String getIndexPassword() {
		return section.get(IniFileEntryNames.INDEXPASSWORD);
	}

	public boolean existIndexPassword() {
		if (	getIndexPassword() == null 
			||	getIndexPassword().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void setIndexPassword(String password) {
		section.put(IniFileEntryNames.INDEXPASSWORD, password);
	}

	private String getFromRevStr () {
		return section.get(IniFileEntryNames.FROMREV);
	}

	public long getFromRev() {
		return Long.parseLong(getFromRevStr());
	}

	public boolean existFromRev() {
		if (getFromRevStr() == null) {
			return false;
		} else if (getFromRevStr().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public void setFromRev(long rev) {
		section.put(IniFileEntryNames.FROMREV, Long.toString(rev));
	}

	public String getToRev() {
		return section.get(IniFileEntryNames.TOREV);
	}

	public boolean existToRev() {
		if (	getToRev() == null 
			||	getToRev().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public void setToRev(String rev) {
		section.put(IniFileEntryNames.TOREV, rev);
	}

	public String getUrl() {
		return section.get(IniFileEntryNames.URL);
	}

	public boolean existUrl() {
		if (	getUrl() == null 
			||	getUrl().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void setUrl(String url) {
		section.put(IniFileEntryNames.URL, url);
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}
	
	public String getCron() {
		return section.get(IniFileEntryNames.CRON);
	}

	public boolean existCron() {
		if (	getCron() == null 
			||	getCron().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void setCron(String cron) {
		section.put(IniFileEntryNames.CRON, cron);
	}

	public String getResultIndex() {
		return section.get(IniFileEntryNames.RESULTINDEX);
	}

	public boolean existResultIndex() {
		if (	getResultIndex() == null 
			||	getResultIndex().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void setResultIndex (String result) {
		section.put(IniFileEntryNames.RESULTINDEX, result);
	}
	
	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

}

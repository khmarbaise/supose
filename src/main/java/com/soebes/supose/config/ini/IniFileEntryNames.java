/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008, 2009 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008, 2009 by Karl Heinz Marbaise
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
package com.soebes.supose.config.ini;

/**
 * @author Karl Heinz Marbaise
 *
 * The following entries represent the available 
 * entries which can be used in the ini file to 
 * configure a repository.
 */
public final class IniFileEntryNames {
	public static final String INDEXUSERNAME = "indexusername";
	public static final String INDEXPASSWORD = "indexpassword";
	public static final String FROMREV = "fromrev";
	public static final String TOREV = "torev";
	public static final String CRON = "cron";
	public static final String URL = "url";
	public static final String RESULTINDEX = "resultindex";
}

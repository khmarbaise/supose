/**
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008, 2009 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (C) 2007, 2008, 2009 by Karl Heinz Marbaise
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

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.InvalidIniFormatException;
import org.ini4j.Ini.Section;
import org.testng.annotations.Test;

@Test
public class IniTest {
	private static Logger LOGGER = Logger.getLogger(IniTest.class);

	public void testReadIni() {
		try {
			Ini ini = new Ini(IniTest.class.getResourceAsStream("/repositories.ini"));
			LOGGER.debug("Nr: " + ini.size());
			for(Iterator iter= ini.keySet().iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				Section value = ini.get(key);
				LOGGER.debug("Section: " + key);
				for(Iterator iterSection = value.keySet().iterator(); iterSection.hasNext(); ) {
					String keySection = (String) iterSection.next();
					String valueSection = value.get(keySection);
					LOGGER.debug("  -> key: " + keySection + " value:" + valueSection);
				}
			}
		} catch (InvalidIniFormatException e) {
			LOGGER.error("Error: " + e);
		} catch (IOException e) {
			LOGGER.error("Error: " + e);
		}
	}
}

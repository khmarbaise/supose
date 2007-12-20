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

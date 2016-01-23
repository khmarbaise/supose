/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2011 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2011 by Karl Heinz Marbaise
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
package com.soebes.supose.core.config.ini;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.soebes.supose.test.TestBase;

public class IniTest extends TestBase {
    private static Logger LOGGER = Logger.getLogger(IniTest.class);

    @Test
    public void testReadIni() {
        try {
            Ini ini = new Ini(
                    IniTest.class.getResourceAsStream("/repositories.ini"));
            LOGGER.debug("Nr: " + ini.size());
            for (Iterator<?> iter = ini.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                Section value = ini.get(key);
                LOGGER.debug("Section: " + key);
                for (Iterator<?> iterSection = value.keySet().iterator(); iterSection
                        .hasNext();) {
                    String keySection = (String) iterSection.next();
                    String valueSection = value.get(keySection);
                    LOGGER.debug("  -> key: " + keySection + " value:"
                            + valueSection);
                }
            }
        } catch (InvalidFileFormatException e) {
            LOGGER.error("Error: ", e);
        } catch (IOException e) {
            LOGGER.error("Error: ", e);
        }
    }

    @Test
    public void testReadIniWithInterface() throws InvalidFileFormatException,
            IOException {
        String url = "file:///" + getTargetDir()
                + "/test-classes/testInterface.ini";
        LOGGER.debug("URL: " + url);
        Ini ini = new Ini(new URL(url));
        IReposConfig rc = ini.get("Test").as(IReposConfig.class);
        LOGGER.debug("FromRev: " + rc.getFromrev());
        LOGGER.debug("ToRev: " + rc.getTorev());
        LOGGER.debug("IndexUserName: " + rc.getIndexusername());
        LOGGER.debug("IndexPassword: " + rc.getIndexpassword());
        LOGGER.debug("URL: " + rc.getUrl());
        LOGGER.debug("ResultIndex: " + rc.getResultindex());
        LOGGER.debug("Cron: " + rc.getCron());
        Assert.assertEquals(rc.getFromrev(), "1");
        Assert.assertEquals(rc.getTorev(), "10");
        Assert.assertEquals(rc.getIndexusername(), "hoge");
        Assert.assertEquals(rc.getIndexpassword(), "elfe");
        Assert.assertEquals(rc.getUrl(), "http://svn.supose.org/supose");
        Assert.assertEquals(rc.getResultindex(), "summary");
        Assert.assertEquals(rc.getCron(), "0 0 18 ? * *");
    }

    @Test
    public void testWriteIniWithInterface() throws InvalidFileFormatException,
            IOException {
        String urlSource = getTargetDir() + "/test-classes/testInterface.ini";
        String urlDest = getTargetDir() + "/NewTestInterface.ini";
        Ini ini = new Ini(new FileInputStream(urlSource));
        IReposConfig rcSource = ini.get("Test").as(IReposConfig.class);
        LOGGER.debug("FromRev: " + rcSource.getFromrev());
        LOGGER.debug("ToRev: " + rcSource.getTorev());
        LOGGER.debug("IndexUserName: " + rcSource.getIndexusername());
        LOGGER.debug("IndexPassword: " + rcSource.getIndexpassword());
        LOGGER.debug("URL: " + rcSource.getUrl());
        LOGGER.debug("ResultIndex: " + rcSource.getResultindex());
        LOGGER.debug("Cron: " + rcSource.getCron());
        Assert.assertEquals(rcSource.getFromrev(), "1");
        Assert.assertEquals(rcSource.getTorev(), "10");
        Assert.assertEquals(rcSource.getIndexusername(), "hoge");
        Assert.assertEquals(rcSource.getIndexpassword(), "elfe");
        Assert.assertEquals(rcSource.getUrl(), "http://svn.supose.org/supose");
        Assert.assertEquals(rcSource.getResultindex(), "summary");
        Assert.assertEquals(rcSource.getCron(), "0 0 18 ? * *");

        rcSource.setFromrev("299");
        rcSource.setTorev("399");
        rcSource.setIndexpassword("Elfine");

        Ini iniDest = new Ini();
        Section sec = iniDest.add("Test");
        sec.from(rcSource);

        FileWriter out = new FileWriter(new File(urlDest));
        iniDest.store(out);
        out.close();

        // Reread the file which has been written before...
        Ini iniReread = new Ini(new FileInputStream(urlDest));
        IReposConfig rcReread = iniReread.get("Test").as(IReposConfig.class);
        Assert.assertEquals(rcReread.getFromrev(), "299");
        Assert.assertEquals(rcReread.getTorev(), "399");
        Assert.assertEquals(rcReread.getIndexusername(), "hoge");
        Assert.assertEquals(rcReread.getIndexpassword(), "Elfine");
        Assert.assertEquals(rcReread.getUrl(), "http://svn.supose.org/supose");
        Assert.assertEquals(rcReread.getResultindex(), "summary");
        Assert.assertEquals(rcReread.getCron(), "0 0 18 ? * *");

    }

}

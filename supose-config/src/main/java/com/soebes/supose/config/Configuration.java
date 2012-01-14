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
package com.soebes.supose.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.soebes.supose.config.model.RepositoryConfigContainer;
import com.soebes.supose.config.model.RepositoryItem;
import com.soebes.supose.config.model.RepositoryList;
import com.soebes.supose.config.model.ScheduledRepositoryItem;
import com.soebes.supose.config.model.SchedulerRepositoryList;
import com.soebes.supose.config.model.io.xpp3.RepositoriesXpp3Reader;
import com.soebes.supose.config.model.io.xpp3.RepositoriesXpp3Writer;

public class Configuration {

    private static Logger LOGGER = Logger.getLogger(Configuration.class);
    private RepositoryConfigContainer configContainer;

    public Configuration() {
        configContainer = new RepositoryConfigContainer();
    }

    public Configuration(File file) {
        configContainer = new RepositoryConfigContainer();
        load(file);
    }

    public Configuration(String fileName) {
        File configFile = new File(fileName);
        configContainer = new RepositoryConfigContainer();
        load(configFile);
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        RepositoriesXpp3Writer xmlWriter = new RepositoriesXpp3Writer();
        try {
            xmlWriter.write(stringWriter, this.configContainer);
            stringWriter.close();
        } catch (IOException e) {
            LOGGER.error("Problem during writing of configuration.", e);
        }
        return stringWriter.toString();
    }

    /**
     * Store the configuration to the given file in
     * XML format.
     * @param output
     * @throws IOException
     */
    public void save(File output) {
        try {
            FileOutputStream fos = new FileOutputStream(output);
            RepositoriesXpp3Writer xmlWriter = new RepositoriesXpp3Writer();
            xmlWriter.write(fos, this.configContainer);
            fos.close();
        } catch (IOException e) {
            LOGGER.error("IOException during writing of XML file.", e);
        }
    }

    /**
     * Load the configuration from the given XML file.
     * @param configFile
     */
    public void load(File configFile) {
        try {
            RepositoriesXpp3Reader xmlReader = new RepositoriesXpp3Reader();
            FileInputStream fis = new FileInputStream(configFile);
            configContainer = xmlReader.read(fis);
        } catch (FileNotFoundException e) {
            LOGGER.error("FileNotFoundException:", e);
        } catch (IOException e) {
            LOGGER.error("IOException:", e);
        } catch (XmlPullParserException e) {
            LOGGER.error("XmlPullParser:", e);
        }
    }

    public RepositoryList getRepositories() {
        if (configContainer.getRepositories() == null) {
            configContainer.setRepositories(new RepositoryList());
        }
        return configContainer.getRepositories();
    }

    public boolean existRepository(String id) {
    	boolean result = false;
    	for(RepositoryItem item : getRepositories().getRepository()) {
    		if (item.getId().equals(id)) {
    			result = true;
    		}
    	}
    	return result;
    }

    public RepositoryItem getRepository(String id) {
    	RepositoryItem result = null;
    	for(RepositoryItem item : getRepositories().getRepository()) {
    		if (item.getId().equals(id)) {
    			result = item;
    		}
    	}
    	return result;
    }

    public SchedulerRepositoryList getScheduler() {
        if (configContainer.getScheduler() == null) {
            configContainer.setScheduler(new SchedulerRepositoryList());
        }
        return configContainer.getScheduler();
    }

    public ScheduledRepositoryItem getScheduler(String id) {
    	ScheduledRepositoryItem result = null;
    	for (ScheduledRepositoryItem item : getScheduler().getScheduledRepository()) {
    		if (item.getRepositoryId().equals(id)) {
    			result = item;
    		}
    	}
    	return result;
    }

    public boolean  existScheduler(String id) {
    	boolean result = false;
    	for (ScheduledRepositoryItem item : getScheduler().getScheduledRepository()) {
    		if (item.getRepositoryId().equals(id)) {
    			result = true;
    		}
    	}
    	return result;
    }

    public String getBaseDirectory() {
        return configContainer.getBaseDirectory();
    }

    public void setBaseDirectory(String baseDir) {
        configContainer.setBaseDirectory(baseDir);
    }
}

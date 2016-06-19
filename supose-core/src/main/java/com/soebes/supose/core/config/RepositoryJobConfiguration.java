/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2016 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2016 by Karl Heinz Marbaise
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
package com.soebes.supose.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.soebes.supose.core.config.ini.IReposConfig;

/**
 * This class will handle the configuration for the Job
 * 
 * @author Karl Heinz Marbaise
 */
public class RepositoryJobConfiguration {
    private static Logger LOGGER = Logger
            .getLogger(RepositoryJobConfiguration.class);

    private String REPOSITORY_CONFIG_SECTION = "repositoryJobConfiguration";

    private String configFile;
    private Ini iniFile;
    private boolean newCreated;

    private IReposConfig configData;

    private RepositoryConfiguration reposConfig;

    public RepositoryJobConfiguration(String configFile,
            RepositoryConfiguration reposConfig) throws Exception {
        setConfigFile(configFile);
        setReposConfig(reposConfig);
        File f = new File(configFile);
        if (f.exists()) {
            setNewCreated(false);
            try {
                FileInputStream fin = new FileInputStream(f);
                iniFile = new Ini(fin);
                configData = iniFile.get(REPOSITORY_CONFIG_SECTION).as(
                        IReposConfig.class);
            } catch (InvalidFileFormatException e) {
                LOGGER.error("The format of the given INI is not correct. ", e);
                throw e;
            } catch (IOException e) {
                LOGGER.error("Some problems happen with the INI File ", e);
                throw e;
            }
        } else {
            LOGGER.debug("We will create a new configuration file "
                    + configFile);
            setNewCreated(true);
            // The first time. We will create a new configuration file.
            iniFile = new Ini();
            iniFile.add(REPOSITORY_CONFIG_SECTION);
            configData = iniFile.get(REPOSITORY_CONFIG_SECTION).as(
                    IReposConfig.class);
            configData.setFromrev(Long.toString(reposConfig.getFromRev()));
            configData.setTorev(reposConfig.getToRev());
            try {
                if (f.createNewFile()) {
                    FileWriter out = new FileWriter(f);
                    iniFile.store(out);
                    out.close();
                }
            } catch (Exception e) {
                LOGGER.error(
                        "We had a problem to write the new configuration file ",
                        e);
            }
        }
    }

    public RepositoryConfiguration getReposConfig() {
        return reposConfig;
    }

    public void setReposConfig(RepositoryConfiguration reposConfig) {
        this.reposConfig = reposConfig;
    }

    public void save() {
        try {
            LOGGER.debug("Trying to write new configuration.");
            FileWriter out = new FileWriter(new File(getConfigFile()));
            Section sec = iniFile.get(REPOSITORY_CONFIG_SECTION);
            sec.from(configData);
            iniFile.store(out);
            out.close();
            LOGGER.debug("Writing of new configuration file '"
                    + getConfigFile() + "'sucessful.");
        } catch (Exception e) {
            LOGGER.error("Unexpected exception: ", e);
        }
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public boolean isNewCreated() {
        return newCreated;
    }

    public void setNewCreated(boolean newCreated) {
        this.newCreated = newCreated;
    }

    public IReposConfig getConfigData() {
        return configData;
    }

    public void setConfigData(IReposConfig configData) {
        this.configData = configData;
    }

}

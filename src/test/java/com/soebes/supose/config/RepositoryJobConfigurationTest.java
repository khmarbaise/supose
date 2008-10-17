/*
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008 by Karl Heinz Marbaise

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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

@Test
public class RepositoryJobConfigurationTest {
	private static Logger LOGGER = Logger.getLogger(RepositoryJobConfiguration.class);

	
	private RepositoryJobConfiguration jobConfig = null;

	public void testInitialization() throws Exception {
		File f = new File(System.getenv("user.dir") + File.separator + "repojobconfig.ini");
		if (f.exists()) {
			f.delete();
		}
//		String f = getFileResource("/com/soebes/supose/config/configuration-repository.ini");
        ConfigurationRepositories confRepos = new ConfigurationRepositories("/com/soebes/supose/config/configuration-repository.ini");
        assertNotNull(confRepos, "We had expected to get the configuration!");
        String repositoryName = confRepos.getNames()[0];
    	RepositoryConfiguration reposConfig = confRepos.getRepositoryConfiguration(repositoryName);
    	assertNotNull(reposConfig, "We had expected to get a configuration for the repository '" + repositoryName + "'");

    	//HACK: Remove hard coded path
		jobConfig = new RepositoryJobConfiguration(System.getenv("user.dir") + File.separator + "repojobconfig.ini", reposConfig);
		assertNotNull(jobConfig, "We had expected to get an instance of RepositoryJobConfiguration");
		assertTrue(f.exists(), "We had expected that the file /repojobconfig.ini has been created.");
	}
	
}

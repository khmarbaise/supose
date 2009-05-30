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
package com.soebes.supose.repository;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.soebes.supose.config.ConfigurationRepositories;
import com.soebes.supose.config.RepositoryConfiguration;
import com.soebes.supose.config.RepositoryFactory;
import com.soebes.supose.repository.Repository;

@Test
public class RepositoryTest {
	private ConfigurationRepositories confRepos = null; 
	
	@BeforeClass
	public void beforeClass() {
		confRepos = new ConfigurationRepositories("/repositories-test.ini");
		assertNotNull(confRepos, "We had expected to get an instance");
	}

	@Test(enabled = false)
	public void testConnection() {
    	RepositoryConfiguration reposConfig = confRepos.getRepositoryConfiguration(confRepos.getNames()[0]);
    	assertNotNull(reposConfig, "We had expected to get a configuration for a single repository.");
    	Repository repository = RepositoryFactory.createInstance(reposConfig);
    	assertEquals(repository.validConnection(), true, "We have expected to get an connection");
	}

}

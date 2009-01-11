/**
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
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
package com.soebes.supose.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.soebes.supose.TestBase;

/**
 * @author Karl Heinz Marbaise
 *
 */
@Test
public class ConfigurationRepositoriesTest extends TestBase {
	private ConfigurationRepositories confRepos = null; 
	
	@BeforeClass
	public void beforeClass() {
		confRepos = new ConfigurationRepositories(getFileResource("/repositories-test.ini"));		
		assertNotNull(confRepos, "We had expected to get an instance");
	}

	public void testGetNames() {
		assertNotNull(confRepos.getNames(), "We had expected to get an list of available repositories.");
		assertEquals(confRepos.getNames().length, 1, "We had expected to get an array with one element");
	}

	@Test(dependsOnMethods={"testGetNames"})
	public void testGetRepositoryConfiguration() {
		String repositoryName = confRepos.getNames()[0];
		RepositoryConfiguration rconfig = confRepos.getRepositoryConfiguration(repositoryName);
		assertNotNull(rconfig, "We had expected to get an instance of RepositoryConfiguration");
	}

	@Test(dependsOnMethods={"testGetRepositoryConfiguration"})
	public void testRepositoryConfiguration10() {
		String repositoryName = confRepos.getNames()[0];
		RepositoryConfiguration rconfig = confRepos.getRepositoryConfiguration(repositoryName);
		assertEquals(rconfig.getUrl(), "http://svn.traveler/jagosi", "We had expected to get an URL");
		assertEquals(rconfig.getIndexUsername(), "kama", "We had expected to get the username 'kama'");
		assertEquals(rconfig.getIndexPassword(), "kama", "We had expected to get the username 'kama'");
		assertEquals(rconfig.getFromRev(), 1, "We had expected to get the revision 1");
		assertEquals(rconfig.getToRev(), "HEAD", "We had expected to get the revision HEAD");
		assertEquals(rconfig.getCron(), "0 * * ? * *", "We had expected to get the cron expression '0 * * ? * *'");
	}

}

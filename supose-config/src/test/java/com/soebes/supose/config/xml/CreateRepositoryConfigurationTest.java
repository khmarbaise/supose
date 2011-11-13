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
package com.soebes.supose.config.xml;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.testng.annotations.Test;

import com.soebes.supose.config.Configuration;
import com.soebes.supose.config.model.RepositoryItem;
import com.soebes.supose.config.model.ScheduledRepositoryItem;

public class CreateRepositoryConfigurationTest {

    @Test
    public void createTest() throws IOException {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<repositoryConfiguration>\n"
                + "  <repositories>\n"
                + "    <repository>\n"
                + "      <id>SupoSE</id>\n"
                + "      <name>SupoSE Repository</name>\n"
                + "      <username>username</username>\n"
                + "      <password>Password</password>\n"
                + "      <fromRevision>1</fromRevision>\n"
                + "      <toRevision>1000</toRevision>\n"
                + "      <blockSize>10000</blockSize>\n"
                + "    </repository>\n"
                + "    <repository>\n"
                + "      <id>JaGOSi</id>\n"
                + "      <name>JaGOSiRepository</name>\n"
                + "      <username>username</username>\n"
                + "      <password>Password</password>\n"
                + "      <fromRevision>1</fromRevision>\n"
                + "      <toRevision>1000</toRevision>\n"
                + "      <blockSize>10000</blockSize>\n"
                + "    </repository>\n"
                + "  </repositories>\n"
                + "  <scheduler>\n"
                + "    <scheduledRepository>\n"
                + "      <repositoryId>SupoSE</repositoryId>\n"
                + "      <schedulerTrigger>0 0 0 00 </schedulerTrigger>\n"
                + "    </scheduledRepository>\n"
                + "  </scheduler>\n"
                + "  <baseDirectory>/home/test</baseDirectory>\n"
                + "</repositoryConfiguration>\n";

        Configuration config = new Configuration();

        RepositoryItem ri1 = new RepositoryItem();
        ri1.setBlockSize(10000);
        ri1.setFromRevision("1");
        ri1.setToRevision("1000");
        ri1.setId("SupoSE");
        ri1.setName("SupoSE Repository");
        ri1.setUsername("username");
        ri1.setPassword("Password");

        config.getRepositories().addRepository(ri1);

        RepositoryItem ri2 = new RepositoryItem();
        ri2.setBlockSize(10000);
        ri2.setFromRevision("1");
        ri2.setToRevision("1000");
        ri2.setId("JaGOSi");
        ri2.setName("JaGOSiRepository");
        ri2.setUsername("username");
        ri2.setPassword("Password");

        config.getRepositories().addRepository(ri2);

        ScheduledRepositoryItem sri = new ScheduledRepositoryItem();
        sri.setRepositoryId("SupoSE");
        sri.setSchedulerTrigger("0 0 0 00 ");

        config.getScheduler().addScheduledRepository(sri);

        config.setBaseDirectory("/home/test");

        assertEquals(config.toString(), expected);
    }

    @Test
    public void createWriteSingleReposTest() throws IOException {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<repositoryConfiguration>\n"
                + "  <repositories>\n"
                + "    <repository>\n"
                + "      <id>SupoSE</id>\n"
                + "      <name>SupoSE Repository</name>\n"
                + "      <username>username</username>\n"
                + "      <password>Password</password>\n"
                + "      <fromRevision>1</fromRevision>\n"
                + "      <toRevision>1000</toRevision>\n"
                + "      <blockSize>10000</blockSize>\n"
                + "    </repository>\n"
                + "  </repositories>\n"
                + "  <baseDirectory>/home/test</baseDirectory>\n"
                + "</repositoryConfiguration>\n";

        Configuration config = new Configuration();

        RepositoryItem ri1 = new RepositoryItem();
        ri1.setBlockSize(10000);
        ri1.setFromRevision("1");
        ri1.setToRevision("1000");
        ri1.setId("SupoSE");
        ri1.setName("SupoSE Repository");
        ri1.setUsername("username");
        ri1.setPassword("Password");

        config.getRepositories().addRepository(ri1);
        config.setBaseDirectory("/home/test");
        assertEquals(config.toString(), expected);
    }
}

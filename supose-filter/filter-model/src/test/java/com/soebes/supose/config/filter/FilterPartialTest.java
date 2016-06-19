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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html
 * If you have any questions about the Software or about the license
 * just write an email to license@soebes.de
 */
package com.soebes.supose.config.filter;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import com.soebes.supose.config.filter.model.Filter;
import com.soebes.supose.config.filter.model.Repository;
import com.soebes.supose.test.TestBase;

/**
 * @author Karl Heinz Marbaise
 * 
 */
public class FilterPartialTest extends TestBase {

    public Filter getFiltering() throws FileNotFoundException, IOException, XmlPullParserException {
        File filterFile = new File(getTestResourcesDirectory() + File.separatorChar + "filter-partial.xml");
        Filter filterConfiguration = FilterFile.getFilter(filterFile);
        return filterConfiguration;
    }

    @Test
    public void hasExcludesIncludesTest() throws FileNotFoundException, IOException, XmlPullParserException {
        Filter filtering = getFiltering();

        Repository repository = filtering.getRepository("default");

        assertEquals(repository.hasFilenames(), false);

        assertEquals(repository.hasPaths(), true);
        assertEquals(repository.getPaths().hasExcludes(), false);
        assertEquals(repository.getPaths().hasIncludes(), true);

        assertEquals(repository.hasProperties(), true);
        assertEquals(repository.getProperties().hasExcludes(), false);
        assertEquals(repository.getProperties().hasIncludes(), true);
    }

}

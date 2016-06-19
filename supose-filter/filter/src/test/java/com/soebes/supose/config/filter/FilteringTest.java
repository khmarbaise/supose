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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.soebes.supose.config.filter.model.Filter;
import com.soebes.supose.test.TestBase;

/**
 * @author Karl Heinz Marbaise
 * 
 */
public class FilteringTest extends TestBase {

    private Filtering filter = null;

    @BeforeClass
    public void beforeClass() throws FileNotFoundException, IOException, XmlPullParserException {
        filter = getFiltering();
    }

    public Filtering getFiltering() throws FileNotFoundException, IOException, XmlPullParserException {
        File filterFile = new File(getTestResourcesDirectory() + File.separatorChar + "filter.xml");
        Filter filterConfiguration = FilterFile.getFilter(filterFile);
        Filtering filtering = new Filtering(filterConfiguration);
        return filtering;
    }

    @Test
    public void filenameIgnoreTest() throws FileNotFoundException, IOException, XmlPullParserException {
        String fileName = "test.pas";
        assertEquals(filter.ignoreFilename(fileName), false);
    }

    @Test
    public void filenameIncludeTest() throws FileNotFoundException, IOException, XmlPullParserException {
        String fileName = "test.pas";
        assertEquals(filter.includeFilename(fileName), true);
    }

    @Test
    public void pathIgnoreTest() throws FileNotFoundException, IOException, XmlPullParserException {
        String path = "/trunk";
        assertEquals(filter.ignorePath(path), false);
    }

    @Test
    public void pathIncludeTest() throws FileNotFoundException, IOException, XmlPullParserException {
        String path = "/trunk";
        assertEquals(filter.includePath(path), true);
    }

    @Test
    public void propertyIgnoreTest() throws FileNotFoundException, IOException, XmlPullParserException {
        String property = "svn:executable";
        assertEquals(filter.ignoreProperty(property), false);
    }

    @Test
    public void propertyIncludeTest() throws FileNotFoundException, IOException, XmlPullParserException {
        String property = "svn:ignore";
        assertEquals(filter.includeProperty(property), true);
    }
}

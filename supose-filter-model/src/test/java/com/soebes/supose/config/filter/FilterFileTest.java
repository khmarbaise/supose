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
package com.soebes.supose.config.filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.soebes.supose.TestBase;
import com.soebes.supose.config.filter.model.Filter;
import com.soebes.supose.config.filter.model.IncludeExcludeList;
import com.soebes.supose.config.filter.model.Repositories;
import com.soebes.supose.config.filter.model.Repository;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class FilterFileTest extends TestBase {

    @Test
    public void createEnhancedFilterTest() throws IOException {
    	String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    		+ "<filter>\n"
    		+ "  <repositories>\n"
    		+ "    <repository>\n"
    		+ "      <id>ThisIsId</id>\n"
    		+ "      <filenames>\n"
    		+ "        <includes>\n"
    		+ "          <include>*</include>\n"
    		+ "        </includes>\n"
    		+ "        <excludes>\n"
    		+ "          <exclude>*.doc</exclude>\n"
    		+ "        </excludes>\n"
    		+ "      </filenames>\n"
    		+ "      <paths>\n"
    		+ "        <includes>\n"
    		+ "          <include>*</include>\n"
    		+ "        </includes>\n"
    		+ "        <excludes>\n"
    		+ "          <exclude>/tags/*</exclude>\n"
    		+ "        </excludes>\n"
    		+ "      </paths>\n"
    		+ "      <properties>\n"
    		+ "        <includes>\n"
    		+ "          <include>*</include>\n"
    		+ "        </includes>\n"
    		+ "        <excludes>\n"
    		+ "          <exclude>svm:*</exclude>\n"
    		+ "        </excludes>\n"
    		+ "      </properties>\n"
    		+ "    </repository>\n"
    		+ "  </repositories>\n"
    		+ "</filter>\n";
        Repository repos = new Repository ();
        
        IncludeExcludeList includeList = new IncludeExcludeList();
        includeList.addInclude("*");
        includeList.addExclude("*.doc");

        repos.setId("ThisIsId");
        repos.setFilenames(includeList);
        
        IncludeExcludeList il = new IncludeExcludeList();
        il.addInclude("*");
        il.addExclude("/tags/*");
        repos.setPaths(il);
        
        il = new IncludeExcludeList();
        il.addInclude("*");
        il.addExclude("svm:*");
        repos.setProperties(il);

        Repositories repositories = new Repositories();
        repositories.addRepository(repos);
        Filter filter = new Filter();
        filter.setRepositories(repositories);

        Assert.assertEquals(FilterFile.toString(filter), expectedXML);
    }

    @Test
    public void createDefaultFilterTest() throws IOException {
    	String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    		+ "<filter>\n"
    		+ "  <repositories>\n"
    		+ "    <repository>\n"
    		+ "      <id>default</id>\n"
    		+ "      <filenames>\n"
    		+ "        <includes>\n"
    		+ "          <include>.*</include>\n"
    		+ "        </includes>\n"
    		+ "      </filenames>\n"
    		+ "      <paths>\n"
    		+ "        <includes>\n"
    		+ "          <include>*</include>\n"
    		+ "        </includes>\n"
    		+ "      </paths>\n"
    		+ "      <properties>\n"
    		+ "        <includes>\n"
    		+ "          <include>*</include>\n"
    		+ "        </includes>\n"
    		+ "      </properties>\n"
    		+ "    </repository>\n"
    		+ "  </repositories>\n"
    		+ "</filter>\n";
        Repository repos = new Repository ();
        
        IncludeExcludeList includeList = new IncludeExcludeList();
        includeList.addInclude(".*");

        repos.setId("default");
        repos.setFilenames(includeList);

        IncludeExcludeList il = new IncludeExcludeList();
        il.addInclude("*");
        repos.setPaths(il);
        
        il = new IncludeExcludeList();
        il.addInclude("*");
        repos.setProperties(il);

        Repositories repositories = new Repositories();
        repositories.addRepository(repos);
        Filter filter = new Filter();
        filter.setRepositories(repositories);
        Assert.assertEquals(FilterFile.toString(filter), expectedXML);
    }

    @Test
    public void readFilterTest() throws IOException, XmlPullParserException {
    	File filterFile = new File(getTestResourcesDirectory() + File.separatorChar + "filter.xml");
    	
    	Filter filter = FilterFile.getFilter(filterFile);
    	Repositories repositories = filter.getRepositories();
    	Assert.assertEquals(repositories.getRepository().size(), 1);

    	Repository repository = repositories.getRepository().get(0);
    	Assert.assertEquals(repository.getId(), "default");
    	
    	Assert.assertEquals(repository.getFilenames().getIncludes().size(), 1);
    	Assert.assertEquals(repository.getFilenames().getExcludes().size(), 0);

    	Assert.assertEquals(repository.getPaths().getIncludes().size(), 1);
    	Assert.assertEquals(repository.getPaths().getExcludes().size(), 0);

    	Assert.assertEquals(repository.getProperties().getIncludes().size(), 1);
    	Assert.assertEquals(repository.getProperties().getExcludes().size(), 0);
    }


    @Test
    public void hasFilenamesTest() {
        Repository repos = new Repository ();
        repos.setId("ThisIsId");
        
        IncludeExcludeList il = new IncludeExcludeList();
        il.addInclude("*");
        il.addExclude("/tags/*");
        
        Assert.assertEquals(repos.hasFilenames(), false);
        
        repos.setFilenames(il);

        Assert.assertEquals(repos.hasFilenames(), true);
    }

    @Test
    public void hasPathsTest() {
        Repository repos = new Repository ();
        repos.setId("ThisIsId");
        
        IncludeExcludeList il = new IncludeExcludeList();
        il.addInclude("*");
        il.addExclude("/tags/*");
        
        Assert.assertEquals(repos.hasPaths(), false);
        
        repos.setPaths(il);

        Assert.assertEquals(repos.hasPaths(), true);
    }

    @Test
    public void hasPropertiesTest() {
        Repository repos = new Repository ();
        repos.setId("ThisIsId");
        
        IncludeExcludeList il = new IncludeExcludeList();
        il.addInclude("*");
        il.addExclude("/tags/*");
        
        Assert.assertEquals(repos.hasProperties(), false);
        
        repos.setProperties(il);

        Assert.assertEquals(repos.hasProperties(), true);
    }

    @Test
    public void hasMethodsTest() {
        Repository repos = new Repository ();
        
        repos.setId("ThisIsId");
        
        IncludeExcludeList il = new IncludeExcludeList();
        il.addInclude("*");
        il.addExclude("/tags/*");
        repos.setPaths(il);
        
        il = new IncludeExcludeList();
        il.addInclude("*");
        il.addExclude("svm:*");
        repos.setProperties(il);

        Repositories repositories = new Repositories();
        repositories.addRepository(repos);
        Filter filter = new Filter();
        filter.setRepositories(repositories);

        Assert.assertEquals(repos.hasFilenames(), false);
        Assert.assertEquals(repos.hasPaths(), true);
        Assert.assertEquals(repos.hasProperties(), true);
    }
    
    
	public Filter getFiltering() throws FileNotFoundException, IOException, XmlPullParserException {
    	File filterFile = new File(getTestResourcesDirectory() + File.separatorChar + "filter.xml");
    	Filter filterConfiguration = FilterFile.getFilter(filterFile);
		return filterConfiguration;
	}

	@Test
	public void hasRepositoryTest() throws FileNotFoundException, IOException, XmlPullParserException {
		Filter filtering = getFiltering();

		Assert.assertEquals(filtering.hasRepository("default"), true);
		Assert.assertEquals(filtering.hasRepository("Default"), false);
	}
	
	@Test
	public void hasExcludesIncludesTest() throws FileNotFoundException, IOException, XmlPullParserException {
		Filter filtering = getFiltering();

		Repository repository = filtering.getRepository("default");

		Assert.assertEquals(repository.getFilenames().hasExcludes(), false);
		Assert.assertEquals(repository.getFilenames().hasIncludes(), true);

		Assert.assertEquals(repository.getPaths().hasExcludes(), false);
		Assert.assertEquals(repository.getPaths().hasIncludes(), true);

		Assert.assertEquals(repository.getProperties().hasExcludes(), false);
		Assert.assertEquals(repository.getProperties().hasIncludes(), true);
	}

    
}

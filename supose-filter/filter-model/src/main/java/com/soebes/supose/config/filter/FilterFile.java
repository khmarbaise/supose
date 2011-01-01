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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.soebes.supose.config.filter.model.Filter;
import com.soebes.supose.config.filter.model.io.xpp3.FilterXpp3Reader;
import com.soebes.supose.config.filter.model.io.xpp3.FilterXpp3Writer;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class FilterFile {

	public static Filter getFilter(File filterFile) throws FileNotFoundException, IOException, XmlPullParserException {
		FilterXpp3Reader read = new FilterXpp3Reader();
		Filter filter = read.read(new FileInputStream(filterFile));
		return filter;
	}

	public static Filter getFilter(InputStream filterFile) throws FileNotFoundException, IOException, XmlPullParserException {
		FilterXpp3Reader read = new FilterXpp3Reader();
		Filter filter = read.read(filterFile);
		return filter;
	}

	public static String toString (Filter filter) throws IOException {
		StringWriter stringWriter = new StringWriter();
		FilterXpp3Writer xmlWriter = new FilterXpp3Writer();
		xmlWriter.write(stringWriter, filter);
		return stringWriter.toString();
	}
}

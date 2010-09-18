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

import com.soebes.supose.config.filter.model.Filter;
import com.soebes.supose.config.filter.model.Repository;

/**
 * This will handle the filtering of artifacts (filenames,
 * paths and properties) during the scan process.
 * 
 * You can define the repository id which is used to search 
 * in the configuration file.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public class Filtering {

	public final String DEFAULT_REPOSITORY = "default";

	private Filter filter;
	
	private String repositoryId;

	public Filtering() {
		setRepositoryId(DEFAULT_REPOSITORY);
	}

	public Filtering(Filter filter) {
		setFilter(filter);
		setRepositoryId(DEFAULT_REPOSITORY);
	}

	public Filtering(Filter filter, String repositoryId) {
		setFilter(filter);
		setRepositoryId(repositoryId);
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	
	private Repository getRepository() {
		return getFilter().getRepository(getRepositoryId());
	}

	/**
	 * This method will check if the given filename 
	 * should be ignored based on the given configuration.
	 * 
	 * @param fileName The name you would like to check if ignored or not.
	 * @return true if the given filename has to be ignored false
	 * 	otherwise.
	 */
	public boolean ignoreFilename(String fileName) {
		boolean result = false;
		if (getRepository().getFilenames().hasIncludes()) {
			for(String regex : getRepository().getFilenames().getIncludes()) {
				if (fileName.matches(regex)) {
					result = false;
				}
			}
		}

		if (getRepository().getFilenames().hasExcludes()) {
			for(String regex : getRepository().getFilenames().getExcludes()) {
				if (fileName.matches(regex)) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * This method will check if the given
	 * path should be ignored or not.
	 * 
	 * @param pathName The path which will be checked.
	 * @return true if the given path will be ignored false otherwise.
	 */
	public boolean ignorePath(String pathName) {
		boolean result = false;
		if (getRepository().getPaths().hasIncludes()) {
			for(String regex : getRepository().getPaths().getIncludes()) {
				if (pathName.matches(regex)) {
					result = false;
				}
			}
		}

		if (getRepository().getPaths().hasExcludes()) {
			for(String regex : getRepository().getPaths().getExcludes()) {
				if (pathName.matches(regex)) {
					result = true;
				}
			}
		}

		return result;
	}

	public boolean ignoreProperty(String propertyName) {
		boolean result = false;
		if (getRepository().getProperties().hasIncludes()) {
			for(String regex : getRepository().getProperties().getIncludes()) {
				if (propertyName.matches(regex)) {
					result = false;
				}
			}
		}

		if (getRepository().getPaths().hasExcludes()) {
			for(String regex : getRepository().getProperties().getExcludes()) {
				if (propertyName.matches(regex)) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * This will check if the given filename matches
	 * the include list or not.
	 * 
	 * @param fileName The filename which will be check if included or not.
	 * @return true if filename matches the include list or false otherwise.
	 */
	public boolean includeFilename(String fileName) {
		boolean result = false;
		if (getRepository().getFilenames().hasIncludes()) {
			for(String regex : getRepository().getFilenames().getIncludes()) {
				if (fileName.matches(regex)) {
					result = true;
				}
			}
		}
		return result;
	}

	public boolean includePath(String pathName) {
		boolean result = false;
		if (getRepository().getPaths().hasIncludes()) {
			for(String regex : getRepository().getPaths().getIncludes()) {
				if (pathName.matches(regex)) {
					result = true;
				}
			}
		}
		return result;
	}

	public boolean includeProperty(String propertyName) {
		boolean result = false;
		if (getRepository().getProperties().hasIncludes()) {
			for(String regex : getRepository().getProperties().getIncludes()) {
				if (propertyName.matches(regex)) {
					result = true;
				}
			}
		}
		return result;
	}
	
	public boolean excludeFilename(String fileName) {
		boolean result = false;
		if (getRepository().getFilenames().hasExcludes()) {
			for(String regex : getRepository().getFilenames().getExcludes()) {
				if (fileName.matches(regex)) {
					result = true;
				}
			}
		}
		return result;
	}
	public boolean excludePath(String pathName) {
		boolean result = false;
		if (getRepository().getPaths().hasExcludes()) {
			for(String regex : getRepository().getPaths().getExcludes()) {
				if (pathName.matches(regex)) {
					result = true;
				}
			}
		}
		return result;
	}
	public boolean excludeProperty(String propertyName) {
		boolean result = false;
		if (getRepository().getProperties().hasExcludes()) {
			for(String regex : getRepository().getProperties().getExcludes()) {
				if (propertyName.matches(regex)) {
					result = true;
				}
			}
		}
		return result;
	}

}

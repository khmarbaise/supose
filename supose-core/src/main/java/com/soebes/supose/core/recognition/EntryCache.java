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
package com.soebes.supose.core.recognition;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;

import com.soebes.supose.core.repository.Repository;
import com.soebes.supose.core.scan.RepositoryInformation;

public class EntryCache {
    private static Logger LOGGER = Logger.getLogger(EntryCache.class);

    private static Long cacheQueries = new Long(0);
    private static Long cacheResult = new Long(0);
    private static Long cacheEntries = new Long(0);

    private Repository repository;

    private Map<String, SVNDirEntry> cacheItems;

    public EntryCache(Repository repository) {
        setRepository(repository);
        cacheItems = new HashMap<String, SVNDirEntry>();
        LOGGER.debug("New initilization of the Cache");
    }

    public SVNDirEntry getEntry(Long revision, String path) {
        String key = path + "(" + revision.toString() + ")";
        SVNDirEntry result = null;
        cacheQueries++;
        if (cacheItems.containsKey(key)) {
            cacheResult++;
            result = cacheItems.get(key);
        } else {
            cacheEntries++;
            result = RepositoryInformation.getInformationAboutEntry(
                    getRepository(), revision, path);
            cacheItems.put(key, result);
        }
        LOGGER.debug("cacheQueries:" + cacheQueries + " cacheResult: "
                + cacheResult + " cacheEntries:" + cacheEntries);
        return result;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

}

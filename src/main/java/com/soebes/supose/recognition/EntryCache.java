package com.soebes.supose.recognition;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;

import com.soebes.supose.repository.Repository;
import com.soebes.supose.scan.RepositoryInformation;

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

	public SVNDirEntry getEntry (Long revision, String path) {
		String key = path + "(" + revision.toString() + ")";
		SVNDirEntry result = null;
		cacheQueries++;
		if (cacheItems.containsKey(key)) {
			cacheResult++;
			result = cacheItems.get(key);
		} else {
			cacheEntries++;
			result = RepositoryInformation.getInformationAboutEntry(getRepository(), revision, path);
			cacheItems.put(key, result);
		}
		LOGGER.debug("cacheQueries:" + cacheQueries + " cacheResult: " + cacheResult + " cacheEntries:" + cacheEntries);
		return result;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public Repository getRepository() {
		return repository;
	}

}

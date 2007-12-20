package com.soebes.supose.config;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.soebes.supose.repository.Repository;

public class RepositoryFactory {
	private static Logger LOGGER = Logger.getLogger(RepositoryFactory.class);

	public static Repository createInstance (RepositoryConfiguration reposConfig) {

		LOGGER.info("Trying to make an authorization (" 
			+ reposConfig.getIndexUsername() + "/" 
			+ reposConfig.getIndexPassword()  + ") to the Repository: " 
			+ reposConfig.getUrl());
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
			reposConfig.getIndexUsername(), 
			reposConfig.getIndexPassword()
		);
		return new Repository (reposConfig.getUrl(), authManager);
	}
}

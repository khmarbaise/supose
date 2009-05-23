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
// SupoSE
package com.soebes.supose.repository;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class Repository {
	private static Logger LOGGER = Logger.getLogger(Repository.class);

	private String url = null;
	private SVNRepository repository = null;
	private ISVNAuthenticationManager authManager = null;

	private String uuid = null;

	/**
	 * This will initialize the SVNKit library.
	 * @param url The url of the repository we would 
	 *   like to do a connection to.
	 * @param authManager This will define the Authentication manager which 
	 *   is used.
	 * @see ISVNAuthenticationManager
	 */
	public Repository (String url, ISVNAuthenticationManager authManager) {
		setUrl(url);
		setAuthenticationManager(authManager);

		/*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();
        
        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();
        
        initRepository();
        repository.setAuthenticationManager(getAuthManager());

	}


	/**
	 * This will try to do a connection to the repository.
	 * This is used to check the the user name/password combination.
	 * @return If true the connection could be established success full
	 *  false otherwise.
	 */
	public boolean validConnection() {
		LOGGER.debug("Trying to make a connection to the repository.");
		try {
			repository.testConnection();
			setUuid(repository.getRepositoryUUID(false));
		} catch (SVNException e) {
			LOGGER.error("Connection to the repository has failed. URL or username/password is not correct. ", e);
			return false;
		}
		LOGGER.info("Successfull connection made to the repository.");
		return true;
	}

	/**
	 * This will initialize the repository access, based on the given 
	 * <code>repositoryURL</code>.
	 */
	private void initRepository () {
        try {
        	LOGGER.info("We are trying to create an Repository instance.");
            /*
             * Creates an instance of SVNRepository to work with the repository.
             * All user's requests to the repository are relative to the
             * repository location used to create this SVNRepository.
             * SVNURL is a wrapper for URL strings that refer to repository locations.
             */
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(getUrl()));
        } catch (SVNException svne) {
        	//This can only happen if we use a protocol which is not registered.
        	//Missing initialization of the library.
        	LOGGER.error("Error while creationg SVNRepository for location '" + getUrl() + "' ", svne);
        }
	}

	public void close() {
		LOGGER.debug("Trying to close the repository Session.");
		if (repository != null) {
			repository.closeSession();
			LOGGER.debug("Repository session closes.");
		} else {
			LOGGER.error("You've tried to close a repository session, but you didn't open one!");
		}
	}

	public void setAuthenticationManager(ISVNAuthenticationManager authManager) {
		this.authManager = authManager;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public SVNRepository getRepository() {
		return repository;
	}

	public void setRepository(SVNRepository repository) {
		this.repository = repository;
	}

	public ISVNAuthenticationManager getAuthManager() {
		return authManager;
	}


	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}

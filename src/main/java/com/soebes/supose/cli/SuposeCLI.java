/*
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (C) 2007 by Karl Heinz Marbaise

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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA *
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html
 * If you have any questions about the Software or about the license
 * just write an email to license@soebes.de
 *
 */
// SupoSE
package com.soebes.supose.cli;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.OptionException;
import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.soebes.supose.scan.ScanRepository;

/**
 * This will define the Command Line Version of SupoSE.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public class SuposeCLI {
	private static Logger LOGGER = Logger.getLogger(SuposeCLI.class);

	private static SuposeCommandLine suposecli = new SuposeCommandLine();
	private static ScanRepository scanRepository = new ScanRepository();

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		CommandLine cl = null;
		try {
			cl = suposecli.doParseArgs(args);
		} catch (OptionException e) {
			System.err.println("Error: Unexpected Option given on command line.");
			return;
		}

		runScan();
		
	}
	
	private static void runScan() {
		LOGGER.info("Scanning started...");
		scanRepository.setRepositoryURL("file:///usr/local/tracrepos/etas");
		//We start from the first revision.
		scanRepository.setStartRevision(1); 
		scanRepository.setEndRevision(SVNRevision.HEAD.getNumber());
		scanRepository.setIndexDirectory("indexDir.Test");
		scanRepository.scan();
	}
	
	@SuppressWarnings("unused")
	private static void runSearch() {
		LOGGER.info("Searching started...");
	}

}

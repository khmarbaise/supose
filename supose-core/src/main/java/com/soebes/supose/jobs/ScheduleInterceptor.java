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

package com.soebes.supose.jobs;

import org.apache.log4j.Logger;

import com.soebes.supose.scan.interceptors.ScanInterceptor;

public class ScheduleInterceptor implements ScanInterceptor {
	private static Logger LOGGER = Logger.getLogger(ScheduleInterceptor.class);

	private Integer numberOfRevisions;
	private Long startTime;
	private Long stopTime;

	public void scanStart(Integer revision) {
		startTime = System.currentTimeMillis();
		numberOfRevisions = revision;
		LOGGER.info("We will scan " + revision + " revisions.");
	}
	
	public void scanStop() {
		//Nothing will be done at the end.
		stopTime = System.currentTimeMillis();
		long ms = (stopTime-startTime);
		long seconds = ms / 1000;
		double result = ms / 1000.0;
		double average = (double)numberOfRevisions / (double)seconds;
		LOGGER.info(String.format("We have taken %.3f seconds for %d revisions.\n", result, numberOfRevisions));
		LOGGER.info(String.format("This is an average of %.3f revisions/second.\n", average));
	}
	public void scanBeginRevision(Long count, Long revision, Integer changeSetSize) {
//		double divd = (double)count * 100.0 / (double)numberOfRevisions;
//		System.out.printf("%6.2f %% of %7d (Revisions:%7d Revision:%7d) Changeset: %4d ", divd, numberOfRevisions, count, revision, changeSetSize);
	}
	
	public void scanEndRevision(Long count, Long revision, Integer changeSetSize) {
		//We will do nothing.
	}

}

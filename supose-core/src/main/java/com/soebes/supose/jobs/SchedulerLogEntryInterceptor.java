/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2011 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2011 by Karl Heinz Marbaise
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
import org.tmatesoft.svn.core.SVNLogEntry;

import com.soebes.supose.scan.interceptors.LogEntryInterceptor;

public class SchedulerLogEntryInterceptor implements LogEntryInterceptor {
    private static Logger LOGGER = Logger
            .getLogger(SchedulerLogEntryInterceptor.class);

    public void LogEntryStart() {
        LOGGER.info("Started scanning of the Log entries.");
    }

    public void LogEntry(SVNLogEntry logEntry) {
    }

    public void LogEntryStop() {
        LOGGER.info("Scanning of the Log entries done.");
    }

}

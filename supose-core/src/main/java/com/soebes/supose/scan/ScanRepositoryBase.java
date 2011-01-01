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
package com.soebes.supose.scan;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.soebes.supose.scan.interceptors.ScanInterceptor;

/**
 * This base class will handle the whole interceptor calling and their
 * maintenance.
 * 
 * @author Karl Heinz Marbaise
 * 
 */
public class ScanRepositoryBase extends ScanRepositoryBaseLogEntry implements
        ScanInterceptor {
    private static Logger LOGGER = Logger.getLogger(ScanRepositoryBase.class);

    private ArrayList<ScanInterceptor> interceptors;

    public ScanRepositoryBase() {
        super();
        setInterceptors(new ArrayList<ScanInterceptor>());
    }

    public void scanEndRevision(Long count, Long revision, Integer changeSetSize) {
        LOGGER.trace("scanEndRevision()");
        for (ScanInterceptor item : getInterceptors()) {
            item.scanEndRevision(count, revision, changeSetSize);
        }
    }

    public void scanStart(Integer revision) {
        LOGGER.trace("scanStart()");
        for (ScanInterceptor item : getInterceptors()) {
            item.scanStart(revision);
        }
    }

    public void scanStop() {
        LOGGER.trace("scanStop()");
        for (ScanInterceptor item : getInterceptors()) {
            item.scanStop();
        }
    }

    public void scanBeginRevision(Long count, Long revision,
            Integer changeSetSize) {
        LOGGER.trace("scanBeginRevision()");
        for (ScanInterceptor item : getInterceptors()) {
            item.scanBeginRevision(count, revision, changeSetSize);
        }
    }

    public void setInterceptors(ArrayList<ScanInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public ArrayList<ScanInterceptor> getInterceptors() {
        return interceptors;
    }

    public void registerScanInterceptor(ScanInterceptor interceptor) {
        getInterceptors().add(interceptor);
    }
}

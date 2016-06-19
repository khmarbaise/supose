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
package com.soebes.supose.core.scan;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;

import com.soebes.supose.core.scan.interceptors.ChangeSetInterceptor;

public class ScanRepositoryBaseChangeSet implements ChangeSetInterceptor {
    private static Logger LOGGER = Logger
            .getLogger(ScanRepositoryBaseChangeSet.class);
    private ArrayList<ChangeSetInterceptor> changeSetInterceptors;

    public ScanRepositoryBaseChangeSet() {
        setChangeSetInterceptors(new ArrayList<ChangeSetInterceptor>());
    }

    public void beginIndexChangeSetItem(SVNDirEntry dirEntry) {
        LOGGER.trace("beginIndexChangeSetItem()");
        for (ChangeSetInterceptor item : getChangeSetInterceptors()) {
            item.beginIndexChangeSetItem(dirEntry);
        }
    }

    public void endIndexChangeSetItem(SVNDirEntry dirEntry) {
        LOGGER.trace("endIndexChangeSetItem()");
        for (ChangeSetInterceptor item : getChangeSetInterceptors()) {
            item.endIndexChangeSetItem(dirEntry);
        }
    }

    public void startIndexChangeSet() {
        LOGGER.trace("startIndexChangeSet()");
        for (ChangeSetInterceptor item : getChangeSetInterceptors()) {
            item.startIndexChangeSet();
        }
    }

    public void stopIndexChangeSet() {
        LOGGER.trace("stopIndexChangeSet()");
        for (ChangeSetInterceptor item : getChangeSetInterceptors()) {
            item.stopIndexChangeSet();
        }
    }

    public void registerChangeSetInterceptor(ChangeSetInterceptor interceptor) {
        getChangeSetInterceptors().add(interceptor);
    }

    public void setChangeSetInterceptors(
            ArrayList<ChangeSetInterceptor> changeSetInterceptors) {
        this.changeSetInterceptors = changeSetInterceptors;
    }

    public ArrayList<ChangeSetInterceptor> getChangeSetInterceptors() {
        return changeSetInterceptors;
    }

}

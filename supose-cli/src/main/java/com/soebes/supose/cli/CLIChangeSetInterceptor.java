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

package com.soebes.supose.cli;

import org.tmatesoft.svn.core.SVNDirEntry;

import com.soebes.supose.scan.interceptors.ChangeSetInterceptor;

public class CLIChangeSetInterceptor implements ChangeSetInterceptor {

    private int counter;

    public void beginIndexChangeSetItem(SVNDirEntry dirEntry) {
        if (dirEntry != null) {
            System.out.printf("[%5d item size: %10d]", counter,
                    dirEntry.getSize());
        }
    }

    public void endIndexChangeSetItem(SVNDirEntry dirEntry) {
        if (dirEntry != null) {
            System.out
            .printf("\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010");
        }
        counter++;
    }

    public void startIndexChangeSet() {
        counter = 1;
    }

    public void stopIndexChangeSet() {
    }

}

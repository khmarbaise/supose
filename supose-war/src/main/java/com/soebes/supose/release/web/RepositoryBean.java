/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2016 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2016 by Karl Heinz Marbaise
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html If
 * you have any questions about the Software or about the license just write an
 * email to license@soebes.de
 */
package com.soebes.supose.release.web;

import java.io.Serializable;

public class RepositoryBean implements Serializable {
    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = -421205045432802931L;

    private String index;

    private String url;
    private String urlviewvc;

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        if (url.endsWith("/")) {
            return url;
        } else {
            return url + "/";
        }
    }

    public void setUrlviewvc(String urlviewvc) {
        this.urlviewvc = urlviewvc;
    }

    public String getUrlviewvc() {
        if (urlviewvc.endsWith("/")) {
            return urlviewvc;
        } else {
            return urlviewvc + "/";
        }
    }

}

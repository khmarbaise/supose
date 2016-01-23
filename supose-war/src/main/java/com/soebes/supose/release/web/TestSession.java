/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2011 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2011 by Karl Heinz Marbaise
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
import java.util.List;

import javax.faces.component.html.HtmlDataTable;

import org.apache.log4j.Logger;

import com.soebes.supose.core.search.ResultEntry;
import com.soebes.supose.core.search.SearchRepository;

public class TestSession implements Serializable {
    private static Logger LOGGER = Logger.getLogger(TestSession.class);

    private static final long serialVersionUID = 120672378912357403L;

    private HtmlDataTable dataTable;

    private String index = null;

    private String query;

    private RepositoryBean repositoryBean = null;

    public TestSession() {
// ApplicationContext ctx = FacesContextUtils
// .getWebApplicationContext(FacesContext.getCurrentInstance());
// setRepositoryBean((RepositoryBean) ctx.getBean("RepositoryBean"));
// setIndex(getRepositoryBean().getIndex());
    }

    public String searchRevision() {
        LOGGER.info("searchRevision() called.");
        ResultEntry result = (ResultEntry) getDataTable().getRowData();
        LOGGER.info("searchRevision() rev:" + result.getRevision());
        setQuery("+revision:" + result.getRevision());
        return "SUCCESS";
    }

    public String searchFilename() {
        LOGGER.info("searchFilename() called.");
        ResultEntry result = (ResultEntry) getDataTable().getRowData();
        LOGGER.info("searchFilename() filename:" + result.getFilename());
        setQuery("+filename:" + result.getFilename());
        return "SUCCESS";
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public List<ResultEntry> getResult() {
        LOGGER.info("getResult(" + getQuery() + ")");
        SearchRepository searchRepository = new SearchRepository(getIndex());
        List<ResultEntry> result = searchRepository.getResult(getQuery());
        return result;
    }

    public String query() {
        LOGGER.info("query() we start");
        LOGGER.info("The query:" + getQuery());
        // Do some things here...
        return "SUCCESS";
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public void setRepositoryBean(RepositoryBean repositoryBean) {
        this.repositoryBean = repositoryBean;
    }

    public RepositoryBean getRepositoryBean() {
        return repositoryBean;
    }

    public HtmlDataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(HtmlDataTable dataTable) {
        this.dataTable = dataTable;
    }
}

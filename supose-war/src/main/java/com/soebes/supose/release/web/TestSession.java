package com.soebes.supose.release.web;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import com.soebes.supose.search.ResultEntry;
import com.soebes.supose.search.SearchRepository;

public class TestSession implements Serializable {
    private static Logger LOGGER = Logger.getLogger(TestSession.class);

    private static final long serialVersionUID = 120672378912357403L;

    private HtmlDataTable dataTable;

    private String index = null;

    private String query;

    private RepositoryBean repositoryBean = null;

    public TestSession() {
        ApplicationContext ctx = FacesContextUtils
                .getWebApplicationContext(FacesContext.getCurrentInstance());
        setRepositoryBean((RepositoryBean) ctx.getBean("RepositoryBean"));
        setIndex(getRepositoryBean().getIndex());
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

package com.soebes.supose.release.web;

public class RepositoryBean {
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

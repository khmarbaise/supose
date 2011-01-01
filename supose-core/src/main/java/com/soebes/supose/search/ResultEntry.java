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

package com.soebes.supose.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResultEntry {
    private String repository;
    private String repositoryUUID;

    private Long revision;
    private String contents; // Multiple times ?
    private String node;
    private String kind; // A added, M Modified, R Replaced, D deleted...?
    private Date date;
    private String path;
    private String filename;
    private String author;
    private String message;

    private String from;
    private Long fromRev;

    private Long size;

    private String tag;
    private String mavenTag;
    private String subversionTag;
    private String branch;

    private String xlsAuthor;

    private String pdfAuthor;
    private String pdfKeywords;
    private String pdfTitle;
    private String pdfSubject;

    private String methods;

    private String comments;

    private Map<String, String> properties; // Things like svn:externals,
                                            // svn:ignore etc.

    public ResultEntry() {
        properties = new HashMap<String, String>();
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void addProperty(String name, String value) {
        this.properties.put(name, value);
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getRepositoryUUID() {
        return repositoryUUID;
    }

    public void setRepositoryUUID(String repositoryUUID) {
        this.repositoryUUID = repositoryUUID;
    }

    public Long getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = Long.parseLong(revision);
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS");
        this.date = sdf.parse(date);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getFromRev() {
        return fromRev;
    }

    public void setFromRev(String fromRev) {
        this.fromRev = Long.parseLong(fromRev);
    }

    public Long getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = Long.parseLong(size);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMavenTag() {
        return mavenTag;
    }

    public void setMavenTag(String mavenTag) {
        this.mavenTag = mavenTag;
    }

    public String getSubversionTag() {
        return subversionTag;
    }

    public void setSubversionTag(String subversionTag) {
        this.subversionTag = subversionTag;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getXlsAuthor() {
        return xlsAuthor;
    }

    public void setXlsAuthor(String xlsAuthor) {
        this.xlsAuthor = xlsAuthor;
    }

    public String getPdfAuthor() {
        return pdfAuthor;
    }

    public void setPdfAuthor(String pdfAuthor) {
        this.pdfAuthor = pdfAuthor;
    }

    public String getPdfKeywords() {
        return pdfKeywords;
    }

    public void setPdfKeywords(String pdfKeywords) {
        this.pdfKeywords = pdfKeywords;
    }

    public String getPdfTitle() {
        return pdfTitle;
    }

    public void setPdfTitle(String pdfTitle) {
        this.pdfTitle = pdfTitle;
    }

    public String getPdfSubject() {
        return pdfSubject;
    }

    public void setPdfSubject(String pdfSubject) {
        this.pdfSubject = pdfSubject;
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}

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
package com.soebes.supose.core.recognition;

/**
 * This class will store the information about a tag/branch.
 * 
 * If it is a branch the Type.BRANCH whereas the TagType.NONE.
 * 
 * If it is a Tag the Type.TAG whereas the TagType can be TAG, MAVENTAG or
 * SUBVERSIONTAG.
 * 
 * @author Karl Heinz Marbaise
 * 
 */
public class TagBranch {

    public enum Type {
        BRANCH, TAG
    }

    public enum TagType {
        NONE, TAG, MAVENTAG, SUBVERSIONTAG
    }

    private Type type;
    private TagType tagType;
    private String name;
    private long revision;
    private long copyFromRevision;

    public TagBranch(String name, long revision, long copyFromRevision,
            Type type) {
        super();
        this.copyFromRevision = copyFromRevision;
        this.name = name;
        this.revision = revision;
        this.type = type;
        this.tagType = TagType.NONE;
    }

    public TagBranch() {
        this.name = null;
        this.type = null;
        this.revision = -1;
        this.copyFromRevision = -1;
        this.tagType = TagType.NONE;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public long getCopyFromRevision() {
        return copyFromRevision;
    }

    public void setCopyFromRevision(long copyFromRevision) {
        this.copyFromRevision = copyFromRevision;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TagType getTagType() {
        return tagType;
    }

    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }

}

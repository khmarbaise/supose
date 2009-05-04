/**
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008, 2009 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008, 2009 by Karl Heinz Marbaise
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

public class TagType {

	public enum Type {
		BRANCH,
		TAG
	}

	private Type type;
	private String name;
	private long revision;
	private long copyFromRevision;
	private boolean mavenTag;

	public TagType(String name, long revision, long copyFromRevision, Type type) {
		super();
		this.copyFromRevision = copyFromRevision;
		this.name = name;
		this.revision = revision;
		this.type = type;
		this.mavenTag = false;
	}

	public TagType() {
		this.name = null;
		this.type = null;
		this.revision = -1;
		this.copyFromRevision = -1;
		this.mavenTag = false;
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

	public void setMavenTag(boolean mavenTag) {
		this.mavenTag = mavenTag;
	}

	public boolean isMavenTag() {
		return mavenTag;
	}

	
}

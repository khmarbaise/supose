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
/**
 * 
 */
package com.soebes.supose.release.web;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.tree2.HtmlTree;

/**
 * Class represents the content for the JSF tree.
 * 
 * @author Karl Heinz Marbaise
 * @see http://technology.amis.nl/blog/?p=983
 * @see http://wiki.apache.org/myfaces/Tree2 => See for information about
 *      expanding nodes via Bean
 * @see http://support.teamdev.com/message/2591#2591 (Node expansion!)
 * @see http://support.teamdev.com/message/5540 (Test implementaion?)
 */
public class TreeModel {

    // Logger
    private static Logger log = Logger.getLogger(TreeModel.class);

    private HtmlTree tree;
    private String nodePath;

    public TreeModel() {
        log.debug("TreeModel::TreeModel()");
    }

    public void setTree(HtmlTree tree) {
        this.tree = tree;
    }

    public HtmlTree getTree() {
        return tree;
    }

    public void expandAll() {
        tree.expandAll();
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void expandPath(ActionEvent event) {
        tree.expandPath(tree.getPathInformation(nodePath));
    }

}

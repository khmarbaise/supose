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
 * @author Karl Heinz Marbaise
 */
package com.soebes.supose.release.web;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeNode;

/**
 * Managed bean for the tree page
 * 
 * @author Karl Heinz Marbaise
 * 
 */
public class TreeHandler {
    private static Logger log = Logger.getLogger(TreeHandler.class);

    private TreeModel treeModel;

    private String reposRelativePath;
    private String folder;
    private String revisionStr;

    public TreeHandler() {
        log.debug("TreeHandler::TreeHandler()");
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }

    public TreeNode getRoot() {
        log.debug("TreeHandler::getRoot()");
        return null;
    }

    public String expandAll() {
        treeModel.expandAll();
        return null;
    }

    public void processAction(ActionEvent event)
            throws AbortProcessingException {
        log.debug("TreeHandler::processAction()");
        UIComponent component = (UIComponent) event.getSource();
        while (!(component != null && component instanceof HtmlTree)) {
            component = component.getParent();
        }
        if (component != null) {
            HtmlTree tree = (HtmlTree) component;
            tree.setNodeSelected(event);
        }
    }

    public String getRepository() {
        return reposRelativePath;
    }

    public String getFolder() {
        return folder;
    }

    public String getRevision() {
        return revisionStr;
    }

    public String ok() {
        log.info("ok()");

        return "SUCCESS";
    }
}

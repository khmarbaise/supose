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

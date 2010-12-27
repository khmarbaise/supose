/**
 * 
 */
package com.soebes.supose.release.web;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.tree2.HtmlTree;

/**
 * Class represents the content for the JSF tree.
 * @author Karl Heinz Marbaise
 * @see http://technology.amis.nl/blog/?p=983
 * @see http://wiki.apache.org/myfaces/Tree2
 *  => See for information about expanding nodes via Bean
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

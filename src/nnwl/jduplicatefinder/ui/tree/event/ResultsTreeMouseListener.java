package nnwl.jduplicatefinder.ui.tree.event;

import nnwl.jduplicatefinder.ui.tree.ResultsTree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class ResultsTreeMouseListener extends MouseAdapter {
	ResultsTree tree;

	public ResultsTreeMouseListener(ResultsTree tree) {
		this.tree = tree;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.popupMenu(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.popupMenu(e);
		}
	}

	private void popupMenu(MouseEvent e) {
		//ResultsTree tree = (ResultsTree) e.getSource();
		TreePath tp = this.tree.getPathForLocation(e.getX(), e.getY());
		tree.setSelectionPath(tp);

		if (tp != null) {
			DefaultMutableTreeNode node = this.tree.getSelectedNode();
			this.tree.getContextMenuHelper().popupMenu(this.tree, node, e.getX(), e.getY());
		}
	}
}
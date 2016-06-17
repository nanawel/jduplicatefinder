package nnwl.jduplicatefinder.ui.tree.action;

import nnwl.jduplicatefinder.ui.tree.ResultsTree;

public abstract class AbstractAction extends javax.swing.AbstractAction {
	private static final long serialVersionUID = 130059619751663690L;

	protected ResultsTree tree;

	public AbstractAction(ResultsTree tree) {
		this.tree = tree;
	}

	public ResultsTree getTree() {
		return this.tree;
	}
}

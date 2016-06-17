package nnwl.jduplicatefinder.ui.tree.action;

import nnwl.jduplicatefinder.ui.DialogHelper;
import nnwl.jduplicatefinder.ui.ResultsTreeModel;
import nnwl.jduplicatefinder.ui.tree.ResultsTree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class DeleteFileAction extends AbstractAction implements ActionListener {
	private static final long serialVersionUID = -990561941359051314L;

	public DeleteFileAction(ResultsTree tree) {
		super(tree);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		Path targetPath = this.getTree().getEventTargetPath();
		if (targetPath == null) {
			return;
		}

		int choice = DialogHelper.showConfirmDeleteDialog(targetPath);
		if (choice == JOptionPane.YES_OPTION) {
			// FIXME Do it better, model might not be the best to handle that
			((ResultsTreeModel) this.getTree().getModel()).deleteFileAndTreeNode(targetPath);
		}
	}
}
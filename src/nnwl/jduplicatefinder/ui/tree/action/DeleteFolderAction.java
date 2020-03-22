package nnwl.jduplicatefinder.ui.tree.action;

import nnwl.jduplicatefinder.ui.Constants;
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
public class DeleteFolderAction extends AbstractAction implements ActionListener {
	private static final long serialVersionUID = 1743121607774122798L;

	public DeleteFolderAction(ResultsTree tree) {
		super(tree);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		Path targetPath = this.getTree().getEventTargetPath();
		if (!targetPath.toFile().isDirectory()) {
			return;
		}

		int choice = DialogHelper.showConfirmDeleteDialog(targetPath,
				"Are you sure you want to delete <b>this folder and its content</b>?<br/><br/>" + Constants.HTML_TAB
						+ targetPath.toAbsolutePath());

		if (choice == JOptionPane.YES_OPTION) {
			if (((ResultsTreeModel) this.getTree().getModel()).deleteFileAndTreeNode(targetPath, true)) {
				JOptionPane.showMessageDialog(null, "<html>Folder deleted successfully</html>", "Folder deletion",
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
								DeleteFolderAction.class.getResource("/icons/i32x32/dialog-information.png")));
			} else {
				JOptionPane.showMessageDialog(null, "<html>Cannot delete the folder or part of its content</html>", "Folder deletion",
						JOptionPane.ERROR_MESSAGE,
						new ImageIcon(DeleteFolderAction.class.getResource("/icons/i32x32/dialog-error")));
			}
		}
	}
}
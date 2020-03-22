package nnwl.jduplicatefinder.ui.tree.action;

import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.ui.Constants;
import nnwl.jduplicatefinder.ui.DialogHelper;
import nnwl.jduplicatefinder.ui.ResultsTreeModel;
import nnwl.jduplicatefinder.ui.tree.ResultsTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class DeleteFileAndAllSimilarFilesAction extends AbstractAction implements ActionListener {
	private static final long serialVersionUID = 2567458273608199767L;

	public DeleteFileAndAllSimilarFilesAction(ResultsTree tree) {
		super(tree);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		Path targetPath = this.getTree().getEventTargetPath();
		File targetFile = targetPath.toFile();
		if (!targetFile.isFile()) {
			return;
		}
		DefaultMutableTreeNode selectedNode = this.getTree().getSelectedNode();

		int choice = DialogHelper.showConfirmDeleteDialog(
				targetPath,
				"Are you sure you want to delete <b>this file</b> and <b>all files similar</b> to this one?<br/><br/>"
						+ Constants.HTML_TAB + targetPath.toAbsolutePath() + "<br/><br/>("
						+ (selectedNode.getChildCount() + 1) + " path(s) will be deleted)");

		int filesCount = selectedNode.getChildCount() + 1;
		int filesDeleted = 0;
		if (choice == JOptionPane.YES_OPTION) {
			for (int i = filesCount - 2; i >= 0; i--) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
				SimilarityResult sr = ((SimilarityResult) childNode.getUserObject());
				// FIXME Do it better, model might not be the best to handle that
				if (((ResultsTreeModel) this.getTree().getModel()).deleteFileAndTreeNode(sr.getSimilarFile())) {
					filesDeleted++;
				}
			}
			// FIXME Do it better, model might not be the best to handle that
			if (((ResultsTreeModel) this.getTree().getModel()).deleteFileAndTreeNode(targetPath)) {
				filesDeleted++;
			}
			if (filesCount == filesDeleted) {
				JOptionPane.showMessageDialog(
						null,
						"<html>" + filesCount + " path(s) deleted successfully</html>",
						"File deletion",
						JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon(DeleteFileAndAllSimilarFilesAction.class
								.getResource("/icons/i32x32/dialog-information.png")));
			} else {
				JOptionPane.showMessageDialog(
						null,
						"<html>" + filesDeleted + " path(s) deleted successfully.<br/>" + (filesCount - filesDeleted)
								+ " could not be deleted.</html>",
						"File deletion",
						JOptionPane.WARNING_MESSAGE,
						new ImageIcon(DeleteFileAndAllSimilarFilesAction.class
								.getResource("/icons/i32x32/dialog-warning")));
			}
		}
	}
}

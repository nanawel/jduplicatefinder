package nnwl.jduplicatefinder.ui.tree.action;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.filesystem.FileSystemInterface;
import nnwl.jduplicatefinder.ui.Constants;
import nnwl.jduplicatefinder.ui.DialogHelper;
import nnwl.jduplicatefinder.ui.ResultsTreeModel;
import nnwl.jduplicatefinder.ui.tree.ResultsTree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class DeleteAllReferenceFilesInFolderAction extends AbstractAction implements ActionListener {
	private static final long serialVersionUID = 5719625610665981655L;

	public DeleteAllReferenceFilesInFolderAction(ResultsTree tree) {
		super(tree);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		Path targetPath = this.getTree().getEventTargetPath();
		File targetFile = targetPath.toFile();
		if (!targetFile.isDirectory()) {
			return;
		}

		// FIXME Do it better, model might not be the best to handle that
		List<FileResult> fileResults = ((ResultsTreeModel) this.getTree().getModel())
				.getFileResultsInFolder(targetPath);
		int referenceFilesCount = fileResults.size();

		int choice = DialogHelper
				.showConfirmDeleteDialog(
						targetPath,
						"Are you sure you want to delete <b>all reference files</b> in this folder?"
								+ " (= all files having duplicates elsewhere)<br/><br/>"
								+ Constants.HTML_TAB
								+ targetPath.toAbsolutePath()
								+ "<br/><br/>(approx. <b>"
								+ referenceFilesCount
								+ " files</b> will be deleted)"
								+ "<br/><br/>"
								+ "<u><b>Warning:</b></u> Similar files will NOT be deleted UNLESS they appear as reference files in this folder too.");

		int deletionSuccess = 0;
		int deletionFail = 0;
		if (choice == JOptionPane.YES_OPTION) {
			for (FileResult fr : fileResults) {
				// FIXME Do it better, model might not be the best to handle that
				if (((ResultsTreeModel) this.getTree().getModel()).deleteFileAndTreeNode(fr.getReferenceFile())) {
					deletionSuccess++;
				} else {
					deletionFail++;
				}
			}

			if (deletionFail == 0) {
				JOptionPane.showMessageDialog(
						null,
						"<html>Approx. " + deletionSuccess + " path(s) deleted successfully</html>",
						"File deletion",
						JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon(DeleteAllReferenceFilesInFolderAction.class
								.getResource("/icons/i32x32/dialog-information.png")));

				if (targetFile.listFiles().length == 0) {
					choice = JOptionPane.showConfirmDialog(null,
							"<html>This folder is now empty, do you want to delete it too?</html>", "Empty folder",
							JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.YES_OPTION) {
						if (FileSystemInterface.getFileSystemInterface().deleteFolder(targetFile)) {
							JOptionPane.showMessageDialog(
									null,
									"<html>Folder deleted successfully</html>",
									"Folder deletion",
									JOptionPane.INFORMATION_MESSAGE,
									new ImageIcon(DeleteAllReferenceFilesInFolderAction.class
											.getResource("/icons/i32x32/dialog-information.png")));
						} else {
							JOptionPane.showMessageDialog(
									null,
									"<html>Cannot delete folder</html>",
									"Folder deletion",
									JOptionPane.ERROR_MESSAGE,
									new ImageIcon(DeleteAllReferenceFilesInFolderAction.class
											.getResource("/icons/i32x32/dialog-error.png")));
						}
					}
				}
			} else {
				JOptionPane.showMessageDialog(
						null,
						"<html>" + deletionSuccess + " path(s) deleted successfully.<br/>" + deletionFail
								+ " could not be deleted.</html>",
						"File deletion",
						JOptionPane.WARNING_MESSAGE,
						new ImageIcon(DeleteAllReferenceFilesInFolderAction.class
								.getResource("/icons/i32x32/dialog-warning.png")));

			}
		}
	}
}
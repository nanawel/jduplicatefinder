package nnwl.jduplicatefinder.ui.tree.action;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.ui.Constants;
import nnwl.jduplicatefinder.ui.DialogHelper;
import nnwl.jduplicatefinder.ui.ResultsTreeModel;
import nnwl.jduplicatefinder.ui.tree.ResultsTree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.List;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class DeleteAllSimilarFilesInFolderAction extends AbstractAction implements ActionListener {
	private static final long serialVersionUID = -112638723619941325L;

	public DeleteAllSimilarFilesInFolderAction(ResultsTree tree) {
		super(tree);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		Path targetFile = this.getTree().getEventTargetPath();
		if (!targetFile.toFile().isDirectory()) {
			return;
		}

		// FIXME Do it better, model might not be the best to handle that
		List<FileResult> fileResults = ((ResultsTreeModel) this.getTree().getModel())
				.getFileResultsInFolder(targetFile);
		int similarFilesCount = 0;
		for (FileResult fr : fileResults) {
			similarFilesCount += fr.getSimilarityResults().size();
		}

		int choice = DialogHelper
				.showConfirmDeleteDialog(
						targetFile,
						"Are you sure you want to delete <b>all similar files</b> in this folder?"
								+ " (= all files elsewhere that are duplicates of these ones)<br/><br/>"
								+ Constants.HTML_TAB
								+ targetFile.toAbsolutePath()
								+ "<br/><br/>(approx. <b>"
								+ similarFilesCount
								+ " files</b> will be deleted)"
								+ "<br/><br/>"
								+ "<u><b>Warning:</b></u> Reference files will NOT be deleted UNLESS they appear as similar files in this folder too.");

		int deletionSuccess = 0;
		int deletionFail = 0;
		if (choice == JOptionPane.YES_OPTION) {
			for (FileResult fr : fileResults) {
				for (SimilarityResult sr : fr.getSimilarityResults()) {
					// FIXME Do it better, model might not be the best to handle that
					if (((ResultsTreeModel) this.getTree().getModel()).deleteFileAndTreeNode(sr.getSimilarFile())) {
						deletionSuccess++;
					} else {
						deletionFail++;
					}
				}
			}

			if (deletionFail == 0) {
				JOptionPane.showMessageDialog(
						null,
						"<html>Approx. " + deletionSuccess + " path(s) deleted successfully</html>",
						"File deletion",
						JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon(DeleteAllSimilarFilesInFolderAction.class
								.getResource("/icons/i32x32/dialog-information.png")));
			} else {
				JOptionPane.showMessageDialog(
						null,
						"<html>" + deletionSuccess + " path(s) deleted successfully.<br/>" + deletionFail
								+ " could not be deleted.</html>",
						"File deletion",
						JOptionPane.WARNING_MESSAGE,
						new ImageIcon(DeleteAllSimilarFilesInFolderAction.class
								.getResource("/icons/i32x32/dialog-warning")));
			}
		}
	}
}
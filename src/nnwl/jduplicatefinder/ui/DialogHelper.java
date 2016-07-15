package nnwl.jduplicatefinder.ui;

import nnwl.jduplicatefinder.ui.tree.event.ResultsTreeMouseListener;

import javax.swing.*;
import java.nio.file.Path;

public class DialogHelper {
	public static int showConfirmDeleteDialog(Path path) {
		int choice = JOptionPane.showConfirmDialog(null,
				"<html>Are you sure you want to delete <b>this file</b>?<br/><br/>" + Constants.HTML_TAB + path.toAbsolutePath()
						+ "</html>", "File deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
				new ImageIcon(ResultsTreeMouseListener.class.getResource("/icons/i32x32/dialog-warning.png")));
		return choice;
	}

	public static int showConfirmDeleteDialog(Path path, String msg) {
		int choice = JOptionPane.showConfirmDialog(null, "<html>" + msg + "</html>", "File deletion",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(
						ResultsTreeMouseListener.class.getResource("/icons/i32x32/dialog-warning.png")));
		return choice;
	}
}

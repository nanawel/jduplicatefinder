package nnwl.jduplicatefinder.ui.tree.action;

import nnwl.jduplicatefinder.ui.tree.ResultsTree;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
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
public class OpenFileAction extends AbstractAction implements ActionListener {
	private static final long serialVersionUID = -1917255944339743439L;

	public OpenFileAction(ResultsTree tree) {
		super(tree);
	}

	private final Logger logger = Logger.getLogger(OpenFileAction.class);

	@Override
	public void actionPerformed(ActionEvent ev) {
		Path targetPath = this.getTree().getEventTargetPath();
		File targetFile = targetPath.toFile();

		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(targetFile);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JOptionPane
					.showMessageDialog(
							null,
							"Cannot open file.",
							"Error",
							JOptionPane.ERROR_MESSAGE,
							new ImageIcon(OpenFileAction.class
									.getResource("/icons/i16x16/dialog-error.png")));
		}
	}
}
package nnwl.jduplicatefinder.ui.tree.renderer;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class ResultsTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final Logger logger = Logger.getLogger(ResultsTreeCellRenderer.class);

	private static final long serialVersionUID = 6586675126338268276L;

	//protected static ImageIcon fileIcon = new ImageIcon(ResultsTreeCellRenderer.class.getResource("/icons/i16x16/text-x-generic.png"));
	protected static ImageIcon collapsedFolderIcon = new ImageIcon(ResultsTreeCellRenderer.class.getResource("/icons/i16x16/folder.png"));
	protected static ImageIcon expandedFolderIcon = new ImageIcon(ResultsTreeCellRenderer.class.getResource("/icons/i16x16/folder-open.png"));
	protected static ImageIcon[] similarityIcons = new ImageIcon[101];

	static {
		for (int i = 0; i <= 100; i += 25) {
			similarityIcons[i] = new ImageIcon(ResultsTreeCellRenderer.class.getResource("/icons/i16x16/similarity-" + i + ".png"));
		}
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
												  boolean leaf, int row, boolean hasFocus) {
		try {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		} catch (NullPointerException e) {
			// That randomly happens I don't know why...
			logger.error("ResultsTreeCellRenderer.getTreeCellRendererComponent() NullPointerException: " + ((DefaultMutableTreeNode) value).getUserObject(), e);
			//throw e;
		}

		ImageIcon icon = null;
		String stringValue;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object userObject = node.getUserObject();
		if (userObject instanceof FileResult) {
			Path referenceFile = ((FileResult) userObject).getReferenceFile();
			stringValue = tree.convertValueToText(referenceFile.getFileName(), sel, expanded, leaf, row, hasFocus);

			//icon = fileIcon;
			try {
				//TODO Improve that by caching mimetype icons at startup
				String mimetype = Files.probeContentType(referenceFile).replace('/', '-');
				icon = new ImageIcon(ResultsTreeCellRenderer.class.getResource("/icons/i16x16/mimetypes/" + mimetype + ".png"));
			} catch (Exception e) {
				icon = new ImageIcon(ResultsTreeCellRenderer.class.getResource("/icons/i16x16/mimetypes/unknown.png"));
			}

			stringValue = "<html><b>" + stringValue + "</b></html>";
		} else if (userObject instanceof SimilarityResult) {
			SimilarityResult sr = (SimilarityResult) userObject;
			stringValue = "<html><b>[" + sr.getSimilarity() + "%]</b> " + sr.getSimilarFile().toAbsolutePath() + "</html>";
			float similarity = (float) ((SimilarityResult) userObject).getSimilarity();
			int index = (int) Math.floor(similarity / 25) * 25;
			switch (index) {
				case 0:
				case 25:
				case 50:
				case 75:
				case 100:
					icon = similarityIcons[index];
					break;
			}
		} else if (userObject instanceof Path) {
			if (((DefaultMutableTreeNode) node.getParent()).isRoot()) {
				// Subroot => display the full path
				stringValue = userObject.toString();
			} else {
				// other node => display only the filename
				stringValue = ((Path) userObject).getFileName().toString();
			}
			stringValue = tree.convertValueToText(stringValue, sel, expanded, leaf, row, hasFocus);

			if (expanded) {
				icon = expandedFolderIcon;
			} else {
				icon = collapsedFolderIcon;
			}
		} else {
			stringValue = tree.convertValueToText(userObject, sel, expanded, leaf, row, hasFocus);
		}
		this.setText(stringValue);
		this.setIcon(icon);

		return this;
	}
}

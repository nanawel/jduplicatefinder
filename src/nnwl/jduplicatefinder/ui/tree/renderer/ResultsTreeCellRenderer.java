package nnwl.jduplicatefinder.ui.tree.renderer;

import java.awt.Component;
import java.io.File;
import java.nio.file.Files;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.SimilarityResult;

import org.apache.log4j.Logger;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class ResultsTreeCellRenderer extends DefaultTreeCellRenderer
{
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
		}
		catch (NullPointerException e) {
			// That randomly happens I don't know why...
			logger.error("ResultsTreeCellRenderer.getTreeCellRendererComponent() NullPointerException: " + ((DefaultMutableTreeNode) value).getUserObject(), e);
			//throw e;
		}
		
		ImageIcon icon;
		String stringValue;
		
		Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
		if (userObject instanceof FileResult) {
			File referenceFile = ((FileResult) userObject).getReferenceFile();
			stringValue = tree.convertValueToText(referenceFile.getName(), sel, expanded, leaf, row, hasFocus);
			
			//icon = fileIcon;
			try {
				//TODO Improve that by caching mimetype icons at startup
				String mimetype = Files.probeContentType(referenceFile.toPath()).replace('/', '-');
				icon = new ImageIcon(ResultsTreeCellRenderer.class.getResource("/icons/i16x16/mimetypes/" + mimetype + ".png"));
			}
			catch (Exception e) {
				icon = new ImageIcon(ResultsTreeCellRenderer.class.getResource("/icons/i16x16/mimetypes/unknown.png"));
			}
			
			stringValue = "<html><b>" + stringValue + "</b></html>";
		}
		else if (userObject instanceof SimilarityResult) {
			stringValue = tree.convertValueToText(userObject, sel, expanded, leaf, row, hasFocus);
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
				default:
					icon = null;
			}
		}
		else {
			stringValue = tree.convertValueToText(userObject, sel, expanded, leaf, row, hasFocus);
			
			if (expanded) {
				icon = expandedFolderIcon;
			}
			else {
				icon = collapsedFolderIcon;
			}
		}
		this.setText(stringValue);
		this.setIcon(icon);
		
		return this;
	}
}

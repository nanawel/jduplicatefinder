package nnwl.jduplicatefinder.ui.tree;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.ui.tree.action.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

public class ContextMenuHelper {
	private final Logger logger = Logger.getLogger(ContextMenuHelper.class);

	protected ResultsTree tree;

	protected JPopupMenu similarFilesContextMenu;
	protected JPopupMenu fileResultContextMenu;
	protected JPopupMenu folderContextMenu;

	public ContextMenuHelper(ResultsTree tree) {
		this.tree = tree;

		this.initFileResultContextMenu();
		this.initSimilarFilesContextMenu();
		this.initFolderContextMenu();
	}

	protected void initFileResultContextMenu() {
		// FileResult Node
		this.fileResultContextMenu = new JPopupMenu();
		JMenuItem openItem = new JMenuItem("Open file", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/document-open.png")));
		openItem.addActionListener(new OpenFileAction(this.tree));
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		this.fileResultContextMenu.add(openItem);
		JMenuItem browseToItem = new JMenuItem("Open in file browser", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/folder-open.png")));
		browseToItem.addActionListener(new OpenInFileBrowserAction(this.tree));
		browseToItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		this.fileResultContextMenu.add(browseToItem);
		this.fileResultContextMenu.addSeparator();
		JMenuItem deleteItem = new JMenuItem("Delete", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/edit-delete-shred.png")));
		deleteItem.addActionListener(new DeleteFileAction(this.tree));
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		this.fileResultContextMenu.add(deleteItem);
		JMenuItem deleteAllSimilarItem = new JMenuItem("Delete all similar files", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/edit-delete-shred.png")));
		deleteAllSimilarItem.addActionListener(new DeleteAllSimilarFilesAction(this.tree));
		deleteAllSimilarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK));
		this.fileResultContextMenu.add(deleteAllSimilarItem);
		this.fileResultContextMenu.addSeparator();
		JMenuItem deleteFileAndAllSimilarItem = new JMenuItem("Delete this path and all similar", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/edit-delete-shred-all.png")));
		deleteFileAndAllSimilarItem.addActionListener(new DeleteFileAndAllSimilarFilesAction(this.tree));
		deleteFileAndAllSimilarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK
				| InputEvent.CTRL_MASK));
		this.fileResultContextMenu.add(deleteFileAndAllSimilarItem);
	}

	protected void initSimilarFilesContextMenu() {
		// SimilarFile Node
		this.similarFilesContextMenu = new JPopupMenu();
		JMenuItem openItem = new JMenuItem("Open file", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/document-open.png")));
		openItem.addActionListener(new OpenFileAction(this.tree));
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		this.similarFilesContextMenu.add(openItem);
		JMenuItem browseToItem = new JMenuItem("Open in file browser", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/folder-open.png")));
		browseToItem.addActionListener(new OpenInFileBrowserAction(this.tree));
		browseToItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		this.similarFilesContextMenu.add(browseToItem);
		this.similarFilesContextMenu.addSeparator();
		JMenuItem deleteItem = new JMenuItem("Delete", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/edit-delete-shred.png")));
		deleteItem.addActionListener(new DeleteFileAction(this.tree));
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		this.similarFilesContextMenu.add(deleteItem);
	}

	protected void initFolderContextMenu() {
		// Folder Node
		this.folderContextMenu = new JPopupMenu();
		JMenuItem browseToItem = new JMenuItem("Open in file browser", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/folder-open.png")));
		browseToItem.addActionListener(new OpenInFileBrowserAction(this.tree));
		browseToItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		this.folderContextMenu.add(browseToItem);
		this.folderContextMenu.addSeparator();
		JMenuItem deleteAllReferenceFilesInFolderItem = new JMenuItem("Delete all reference files", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/edit-delete-shred-all.png")));
		deleteAllReferenceFilesInFolderItem.addActionListener(new DeleteAllReferenceFilesInFolderAction(this.tree));
		this.folderContextMenu.add(deleteAllReferenceFilesInFolderItem);
		JMenuItem deleteAllSimilarFilesInFolderItem = new JMenuItem("Delete all similar files", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/edit-delete-shred-all.png")));
		deleteAllSimilarFilesInFolderItem.addActionListener(new DeleteAllSimilarFilesInFolderAction(this.tree));
		this.folderContextMenu.add(deleteAllSimilarFilesInFolderItem);
		this.folderContextMenu.addSeparator();
		JMenuItem deleteFolderItem = new JMenuItem("Delete folder", new ImageIcon(
				ContextMenuHelper.class.getResource("/icons/i16x16/edit-delete-folder.png")));
		deleteFolderItem.addActionListener(new DeleteFolderAction(this.tree));
		this.folderContextMenu.add(deleteFolderItem);
	}

	public void popupMenu(ResultsTree tree) {
		TreePath tp = tree.getSelectionPath();
		if (tp != null) {
			DefaultMutableTreeNode node = tree.getSelectedNode();
			Rectangle rect = tree.getPathBounds(tp);
			this.popupMenu(tree, node, (int) rect.getX(), (int) rect.getY());
		}
	}

	public void popupMenu(ResultsTree tree, DefaultMutableTreeNode node) {
		TreePath tp = tree.getSelectionPath();
		if (tp != null) {
			Rectangle rect = tree.getPathBounds(tp);
			this.popupMenu(tree, node, (int) rect.getX(), (int) rect.getY());
		}
	}

	public void popupMenu(ResultsTree tree, DefaultMutableTreeNode node, int x, int y) {
		if (node.getUserObject() instanceof FileResult) {
			fileResultContextMenu.show(tree, x, y);
		} else if (node.getUserObject() instanceof SimilarityResult) {
			similarFilesContextMenu.show(tree, x, y);
		} else if (node.getUserObject() instanceof Path) {
			folderContextMenu.show(tree, x, y);
		} else {
			logger.warn("Invalid node, cancelling context menu");
		}
	}
}

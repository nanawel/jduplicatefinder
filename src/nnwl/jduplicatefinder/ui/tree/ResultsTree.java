package nnwl.jduplicatefinder.ui.tree;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.ui.ResultsTreeModel;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class ResultsTree extends JTree {
	private static Logger logger = Logger.getLogger(ResultsTree.class);

	private static final long serialVersionUID = 3820592884777798998L;

	protected ContextMenuHelper contextMenuHelper;

	public ContextMenuHelper getContextMenuHelper() {
		return contextMenuHelper;
	}

	public void setContextMenuHelper(ContextMenuHelper contextMenuHelper) {
		this.contextMenuHelper = contextMenuHelper;
	}

	public void expand() {
		for (int i = 0; i < this.getRowCount(); i++) {
			this.expandRow(i);
		}
	}

	public void expand(int rows) {
		for (int i = 0; i < rows; i++) {
			this.expandRow(i);
		}
	}

	public void collapse() {
		for (int i = this.getRowCount(); i >= 0; i--) {
			this.collapseRow(i);
		}
	}

	public DefaultMutableTreeNode getSelectedNode() {
		if (this.getSelectionPath() != null) {
			return (DefaultMutableTreeNode) this.getSelectionPath().getLastPathComponent();
		}
		return null;
	}

	/**
	 * FIXME That method is simply not right!
	 *
	 * @return The path corresponding to the selected node, or null if none is found
	 */
	public Path getEventTargetPath() {
		Path targetFile = null;
		DefaultMutableTreeNode selectedNode = this.getSelectedNode();

		if (selectedNode == null) {
			return null;
		}
		if (selectedNode.getUserObject() instanceof SimilarityResult) {
			SimilarityResult sr = ((SimilarityResult) selectedNode.getUserObject());
			targetFile = sr.getSimilarFile();
		} else if (selectedNode.getUserObject() instanceof FileResult) {
			FileResult fr = ((FileResult) selectedNode.getUserObject());
			targetFile = fr.getReferenceFile();
		} else if (selectedNode.getUserObject() instanceof Path) {
			// FIXME View should not know about model type
			targetFile = (Path) selectedNode.getUserObject();
		} else {
			logger.error("Invalid user object: " + selectedNode.getUserObject());
		}
		return targetFile;
	}

	public class Expander implements Runnable {
		private final Logger logger = Logger.getLogger(Expander.class);

		public static final int LISTENERS_ROWS_STEP = 50;

		private List<ExpanderListener> listeners = new ArrayList<>();

		private boolean interrupt;

		@Override
		public void run() {
			this.interrupt = false;
			try {
				int totalRowCount = this.getTotalRowCount();
				logger.debug("Starting expander thread (" + this.getTotalRowCount() + " rows to expand)");
				for (int i = 0; i < totalRowCount; i++) {
					if (this.interrupt) {
						throw new InterruptedException();
					}
					ResultsTree.this.expandRow(i);

					if (i % LISTENERS_ROWS_STEP == 0) {
						this.fireExpandProgress(i, totalRowCount);
					}
				}
				logger.debug("Expanding completed. Terminating thread.");
			} catch (InterruptedException e) {
				// nothing, just exit
			}
		}

		public int getTotalRowCount() {
			return ((ResultsTreeModel) ResultsTree.this.getModel()).getTotalNodesCount();
		}

		public void addListener(ExpanderListener l) {
			this.listeners.add(l);
		}

		private void fireExpandProgress(int currentRow, int totalRow) {
			ExpanderEvent ev = new ExpanderEvent(currentRow, totalRow);
			for (ExpanderListener l : this.listeners) {
				l.expandProgress(ev);
			}
		}

		public void interrupt() {
			this.interrupt = true;
		}
	}

	public interface ExpanderListener {
		void expandProgress(ExpanderEvent ev);
	}

	public class ExpanderEvent {
		private int currentRow = 0;

		private int totalRow = 0;

		public ExpanderEvent(int currentRow, int totalRow) {
			this.currentRow = currentRow;
			this.totalRow = totalRow;
		}

		public int getCurrentRow() {
			return currentRow;
		}

		public int getTotalRow() {
			return totalRow;
		}

		public int getProgressPercent() {
			return Math.round((float) this.currentRow / (float) this.totalRow * 100);
		}
	}
}

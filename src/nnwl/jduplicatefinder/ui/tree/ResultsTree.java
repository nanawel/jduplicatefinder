package nnwl.jduplicatefinder.ui.tree;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;

import nnwl.jduplicatefinder.ui.ResultsTreeModel;

import org.apache.log4j.Logger;

/**
 * 
 * @author Anael Ollier
 */
public class ResultsTree extends JTree
{
	private static final long serialVersionUID = 3820592884777798998L;

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

	public class Expander implements Runnable
	{
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
			}
			catch (InterruptedException e) {
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

	public interface ExpanderListener
	{
		public void expandProgress(ExpanderEvent ev);
	}

	public class ExpanderEvent
	{
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

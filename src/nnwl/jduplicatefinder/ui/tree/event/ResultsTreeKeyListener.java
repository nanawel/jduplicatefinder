package nnwl.jduplicatefinder.ui.tree.event;

import nnwl.jduplicatefinder.ui.tree.ResultsTree;
import nnwl.jduplicatefinder.ui.tree.action.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class ResultsTreeKeyListener implements KeyListener {
	ResultsTree tree;

	public ResultsTreeKeyListener(ResultsTree tree) {
		this.tree = tree;
	}

	@Override
	public void keyPressed(KeyEvent ev) {
		AbstractAction action = null;

		switch (ev.getKeyCode()) {
			case KeyEvent.VK_DELETE:
				if (ev.getModifiers() != 0) {
					if (ev.isShiftDown()) {
						// CTRL + SHIFT + DELETE
						if (ev.isControlDown()) {
							action = new DeleteFileAndAllSimilarFilesAction(this.tree);
						}
						// SHIFT + DELETE
						else {
							action = new DeleteAllSimilarFilesAction(this.tree);
						}
					}
				}
				// DELETE
				else {
					action = new DeleteFileAction(this.tree);
				}
				break;

			case KeyEvent.VK_ENTER:
				action = new OpenInFileBrowserAction(this.tree);
				break;
		}
		if (action != null) {
			action.actionPerformed(new ActionEvent(this.tree, ActionEvent.ACTION_PERFORMED, ""));
		}
	}

	@Override
	public void keyReleased(KeyEvent ev) {}

	@Override
	public void keyTyped(KeyEvent ev) {}
}

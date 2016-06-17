package nnwl.jduplicatefinder.ui;

import javax.swing.*;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class MemoryWatcher implements Runnable {
	private JLabel label;

	private int period = 1000;

	private int threshold = 75;

	public MemoryWatcher(JLabel label) {
		this.label = label;
	}

	@Override
	public void run() {
		Runtime runtime = Runtime.getRuntime();
		int mb = 1024 * 1024;
		while (true) {
			long max = runtime.maxMemory() / mb;
			long used = (runtime.totalMemory() - runtime.freeMemory()) / mb;

			if (this.label != null) {
				String status = used + "/" + max + "MB";
				if (used >= (max * this.threshold) / 100) {
					status = "<font color=\"red\">" + status + "</font>";
				}
				this.label.setText("<html>Mem: " + status + "</html>");
			}

			try {
				Thread.sleep(this.period);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

}

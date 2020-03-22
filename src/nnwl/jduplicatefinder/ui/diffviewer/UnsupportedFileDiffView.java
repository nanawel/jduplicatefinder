package nnwl.jduplicatefinder.ui.diffviewer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

class UnsupportedFileDiffView implements FileDiffView {
	@Override
	public boolean canHandle(File file) {
		return true;
	}

	@Override
	public JComponent getViewForFile(File file) throws IOException {
		JLabel label = new JLabel("<File preview is not supported>");
		label.setEnabled(false);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		return label;
	}
}
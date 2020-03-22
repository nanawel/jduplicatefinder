package nnwl.jduplicatefinder.ui.diffviewer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public interface FileDiffView {
	boolean canHandle(File file) throws IOException;

	JComponent getViewForFile(File file) throws IOException;
}

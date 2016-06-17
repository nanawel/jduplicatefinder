package nnwl.jduplicatefinder.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class FileStackPanel extends JPanel {
	private static final long serialVersionUID = -2913181581548208542L;

	public JScrollPane scrollPane;

	public JPanel stackContainer;

	protected JFileChooser fc;

	protected Path defaultDirectory;


	/**
	 * Create the panel.
	 */
	public FileStackPanel() {
		this(null, new JFileChooser());
	}

	/**
	 * Create the panel.
	 */
	public FileStackPanel(Path defaultDirectory, JFileChooser fileChooser) {
		super();
		if (defaultDirectory == null) {
			this.defaultDirectory = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
		} else {
			this.defaultDirectory = defaultDirectory;
		}
		fc = fileChooser;

		setLayout(new BorderLayout(0, 0));

		stackContainer = new JPanel();
		stackContainer.setLayout(new BoxLayout(stackContainer, BoxLayout.Y_AXIS));

		scrollPane = new JScrollPane(stackContainer);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

		this.addEmptyLine();

		Dimension preferredSize = scrollPane.getPreferredSize();
		scrollPane.setPreferredSize(preferredSize);
	}

	public FileButton addEmptyLine() {
		FileButton fb = new FileButton();
		stackContainer.add(fb);
		return fb;
	}

	public void addPath(Path path) {
		FileButton newPath = this.addEmptyLine();
		newPath.setText(path.toAbsolutePath().toString());
		newPath.enableDelete();
	}

	public Path[] getPaths() {
		Path[] paths = new Path[stackContainer.getComponentCount() - 1];
		for (int i = 0; i < stackContainer.getComponentCount() - 1; i++) {
			paths[i] = FileSystems.getDefault().getPath(((JButton) stackContainer.getComponent(i)).getText());
		}
		return paths;
	}

	public boolean pathExists(Path path) {
		for (Path f : this.getPaths()) {
			if (f.toAbsolutePath().equals(path.toAbsolutePath())) {
				return true;
			}
		}
		return false;
	}

	protected void keepAtLeastOneLine() {
		if (stackContainer.getComponentCount() < 1) {
			this.addEmptyLine();
		}
	}

	public void clearPaths() {
		stackContainer.removeAll();
		//this.keepAtLeastOneLine();
	}

	private class FileButton extends JButton {
		private static final long serialVersionUID = -8685544916590007133L;

		private JButton btnDelete;

		public FileButton() {
			super();

			this.setHorizontalAlignment(LEFT);

			GridBagLayout gbl_this = new GridBagLayout();
			gbl_this.columnWidths = new int[]{0, 0, 0, 0};
			gbl_this.rowHeights = new int[]{0, 0};
			gbl_this.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_this.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			this.setLayout(gbl_this);

			this.setAction(new BrowseAction(this));

			btnDelete = new JButton();
			btnDelete.setAction(new DeleteAction(this));
			btnDelete.setEnabled(false);
			btnDelete.setIcon(new ImageIcon(FileStackPanel.class.getResource("/icons/i16x16/edit-delete.png")));
			GridBagConstraints gbc_btnDelete = new GridBagConstraints();
			gbc_btnDelete.gridx = 1;
			gbc_btnDelete.gridy = 0;
			this.add(btnDelete, gbc_btnDelete);

			// Avoid growing vertically
			Dimension preferredSize = this.getPreferredSize();
			preferredSize.width = this.getMaximumSize().width;
			this.setMaximumSize(preferredSize);
		}

		public void enableDelete() {
			this.btnDelete.setEnabled(true);
		}
	}

	private class BrowseAction extends AbstractAction {
		private static final long serialVersionUID = 8321007256535416876L;

		protected FileButton panel;

		public BrowseAction(FileButton panel) {
			this.panel = panel;
			putValue(NAME, "(Click to browse)");
			putValue(SHORT_DESCRIPTION, "Select target");
		}

		public void actionPerformed(ActionEvent e) {
			File currentFile = new File(this.panel.getText());
			boolean wasEmpty = true;
			if (currentFile.isAbsolute() && currentFile.exists() && currentFile.canRead()) {
				if (currentFile.isDirectory()) {
					fc.setCurrentDirectory(currentFile);
				} else {
					fc.setCurrentDirectory(currentFile.getParentFile());
				}
				wasEmpty = false;
			} else {
				fc.setCurrentDirectory(FileStackPanel.this.defaultDirectory.toFile());
			}

			int returnVal = fc.showOpenDialog(this.panel);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Path path = fc.getSelectedFile().toPath();

				if (FileStackPanel.this.pathExists(path)) {
					JOptionPane.showMessageDialog(FileStackPanel.this, "This path is already in the list.",
							"Path already exists", JOptionPane.ERROR_MESSAGE,
							new ImageIcon(App.class.getResource("/icons/i32x32/dialog-error.png")));
				} else {
					this.panel.setText(path.toAbsolutePath().toString());
					this.panel.enableDelete();

					if (wasEmpty) {
						FileStackPanel.this.addEmptyLine();
					}
				}
			}
		}
	}

	private class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 6972138790090701414L;

		protected JButton panel;

		public DeleteAction(JButton panel) {
			this.panel = panel;
			// putValue(NAME, "Delete");
			// putValue(SHORT_DESCRIPTION, "Delete path");
		}

		public void actionPerformed(ActionEvent e) {
			FileStackPanel.this.stackContainer.remove(this.panel);
			FileStackPanel.this.keepAtLeastOneLine();
			FileStackPanel.this.revalidate();
		}
	}
}

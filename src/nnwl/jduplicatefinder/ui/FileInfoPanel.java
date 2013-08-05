package nnwl.jduplicatefinder.ui;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import nnwl.jduplicatefinder.util.Files;

import org.apache.log4j.Logger;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class FileInfoPanel extends JPanel
{
	private static final long serialVersionUID = -1297040469251939023L;

	private static final Logger logger = Logger.getLogger(FileInfoPanel.class);

	private JTextField txtFilenameValue;
	private JTextField txtPathValue;

	private JLabel lblFilesizevalue;

	private JLabel lblCreatedvalue;

	private JLabel lblMimetypevalue;

	private JLabel lblModifiedvalue;

	private JButton btnBrowseTo;

	private final Action openInBrowserAction = new SwingAction();

	private File currentFile;

	/**
	 * Create the panel.
	 */
	public FileInfoPanel() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblFileInfo = new JLabel("File info");
		lblFileInfo.setIcon(new ImageIcon(FileInfoPanel.class.getResource("/icons/document-preview.png")));
		lblFileInfo.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblFileInfo.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblFileInfo = new GridBagConstraints();
		gbc_lblFileInfo.insets = new Insets(0, 0, 5, 0);
		gbc_lblFileInfo.anchor = GridBagConstraints.WEST;
		gbc_lblFileInfo.gridwidth = 6;
		gbc_lblFileInfo.gridx = 0;
		gbc_lblFileInfo.gridy = 0;
		add(lblFileInfo, gbc_lblFileInfo);

		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 6;
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 1;
		add(separator, gbc_separator);

		JLabel lblFilename = new JLabel("Filename:");
		lblFilename.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblFilename = new GridBagConstraints();
		gbc_lblFilename.anchor = GridBagConstraints.EAST;
		gbc_lblFilename.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilename.gridx = 0;
		gbc_lblFilename.gridy = 2;
		add(lblFilename, gbc_lblFilename);

		txtFilenameValue = new JTextField();
		txtFilenameValue.setEditable(false);
		GridBagConstraints gbc_txtFilenameValue = new GridBagConstraints();
		gbc_txtFilenameValue.gridwidth = 5;
		gbc_txtFilenameValue.insets = new Insets(0, 0, 5, 0);
		gbc_txtFilenameValue.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFilenameValue.gridx = 1;
		gbc_txtFilenameValue.gridy = 2;
		add(txtFilenameValue, gbc_txtFilenameValue);
		txtFilenameValue.setColumns(10);

		JLabel lblPath = new JLabel("Path:");
		lblPath.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblPath = new GridBagConstraints();
		gbc_lblPath.anchor = GridBagConstraints.EAST;
		gbc_lblPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblPath.gridx = 0;
		gbc_lblPath.gridy = 3;
		add(lblPath, gbc_lblPath);

		txtPathValue = new JTextField();
		txtPathValue.setEditable(false);
		GridBagConstraints gbc_txtPathValue = new GridBagConstraints();
		gbc_txtPathValue.gridwidth = 5;
		gbc_txtPathValue.insets = new Insets(0, 0, 5, 0);
		gbc_txtPathValue.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPathValue.gridx = 1;
		gbc_txtPathValue.gridy = 3;
		add(txtPathValue, gbc_txtPathValue);
		txtPathValue.setColumns(10);

		JLabel lblSize = new JLabel("Size:");
		lblSize.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblSize = new GridBagConstraints();
		gbc_lblSize.anchor = GridBagConstraints.EAST;
		gbc_lblSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblSize.gridx = 0;
		gbc_lblSize.gridy = 4;
		add(lblSize, gbc_lblSize);

		lblFilesizevalue = new JLabel("FilesizeValue");
		GridBagConstraints gbc_lblFilesizevalue = new GridBagConstraints();
		gbc_lblFilesizevalue.anchor = GridBagConstraints.WEST;
		gbc_lblFilesizevalue.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilesizevalue.gridx = 1;
		gbc_lblFilesizevalue.gridy = 4;
		add(lblFilesizevalue, gbc_lblFilesizevalue);

		JLabel lblCreated = new JLabel("Created:");
		lblCreated.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblCreated = new GridBagConstraints();
		gbc_lblCreated.anchor = GridBagConstraints.EAST;
		gbc_lblCreated.insets = new Insets(0, 0, 5, 5);
		gbc_lblCreated.gridx = 3;
		gbc_lblCreated.gridy = 4;
		add(lblCreated, gbc_lblCreated);

		lblCreatedvalue = new JLabel("CreatedValue");
		GridBagConstraints gbc_lblCreatedvalue = new GridBagConstraints();
		gbc_lblCreatedvalue.anchor = GridBagConstraints.WEST;
		gbc_lblCreatedvalue.insets = new Insets(0, 0, 5, 5);
		gbc_lblCreatedvalue.gridx = 4;
		gbc_lblCreatedvalue.gridy = 4;
		add(lblCreatedvalue, gbc_lblCreatedvalue);

		JLabel lblMimetype = new JLabel("Mimetype:");
		lblMimetype.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblMimetype = new GridBagConstraints();
		gbc_lblMimetype.anchor = GridBagConstraints.EAST;
		gbc_lblMimetype.insets = new Insets(0, 0, 5, 5);
		gbc_lblMimetype.gridx = 0;
		gbc_lblMimetype.gridy = 5;
		add(lblMimetype, gbc_lblMimetype);

		lblMimetypevalue = new JLabel("MimetypeValue");
		GridBagConstraints gbc_lblMimetypevalue = new GridBagConstraints();
		gbc_lblMimetypevalue.anchor = GridBagConstraints.WEST;
		gbc_lblMimetypevalue.insets = new Insets(0, 0, 5, 5);
		gbc_lblMimetypevalue.gridx = 1;
		gbc_lblMimetypevalue.gridy = 5;
		add(lblMimetypevalue, gbc_lblMimetypevalue);

		JLabel lblModified = new JLabel("Modified:");
		lblModified.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblModified = new GridBagConstraints();
		gbc_lblModified.anchor = GridBagConstraints.EAST;
		gbc_lblModified.insets = new Insets(0, 0, 5, 5);
		gbc_lblModified.gridx = 3;
		gbc_lblModified.gridy = 5;
		add(lblModified, gbc_lblModified);

		lblModifiedvalue = new JLabel("ModifiedValue");
		GridBagConstraints gbc_lblModifiedvalue = new GridBagConstraints();
		gbc_lblModifiedvalue.anchor = GridBagConstraints.WEST;
		gbc_lblModifiedvalue.insets = new Insets(0, 0, 5, 5);
		gbc_lblModifiedvalue.gridx = 4;
		gbc_lblModifiedvalue.gridy = 5;
		add(lblModifiedvalue, gbc_lblModifiedvalue);

		btnBrowseTo = new JButton();
		btnBrowseTo.setAction(openInBrowserAction);
		btnBrowseTo.setIcon(new ImageIcon(FileInfoPanel.class.getResource("/icons/i16x16/system-file-manager.png")));
		GridBagConstraints gbc_btnOpenInBrowser = new GridBagConstraints();
		gbc_btnOpenInBrowser.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOpenInBrowser.gridwidth = 3;
		gbc_btnOpenInBrowser.insets = new Insets(0, 0, 0, 5);
		gbc_btnOpenInBrowser.gridx = 3;
		gbc_btnOpenInBrowser.gridy = 6;
		add(btnBrowseTo, gbc_btnOpenInBrowser);
		
		this.clear();
	}

	public void display(File f) {
		this.currentFile = f;

		this.txtFilenameValue.setText(f.getName());
		this.txtPathValue.setText(f.getAbsolutePath());
		this.lblFilesizevalue.setText(Files.humanReadableByteCount(f.length(), true) + " (" + f.length() + " bytes)");
		try {
			this.lblMimetypevalue.setText(java.nio.file.Files.probeContentType(f.toPath()));
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
			this.lblMimetypevalue.setText("<Error>");
		}

		BasicFileAttributes attr;
		try {
			attr = java.nio.file.Files.readAttributes(f.toPath(), BasicFileAttributes.class);
			FileTime creationDate = attr.creationTime();
			FileTime modificationDate = attr.lastModifiedTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

			this.lblCreatedvalue.setText(dateFormat.format(new Date(creationDate.toMillis())));
			this.lblModifiedvalue.setText(dateFormat.format(new Date(modificationDate.toMillis())));
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);

			this.lblCreatedvalue.setText("<Error>");
			this.lblModifiedvalue.setText("<Error>");
		}

		this.btnBrowseTo.setEnabled(true);
	}

	public void clear() {
		this.txtFilenameValue.setText("");
		this.txtPathValue.setText("");
		this.lblFilesizevalue.setText("");
		this.lblMimetypevalue.setText("");
		this.lblCreatedvalue.setText("");
		this.lblModifiedvalue.setText("");

		this.btnBrowseTo.setEnabled(false);
	}

	private class SwingAction extends AbstractAction
	{

		private static final long serialVersionUID = -8786434483497463261L;

		public SwingAction() {
			putValue(NAME, "Browse to...");
			putValue(SHORT_DESCRIPTION, "Open parent folder with system's file browser");
		}

		public void actionPerformed(ActionEvent ev) {
			try {
				File target = FileInfoPanel.this.currentFile.isDirectory() ? FileInfoPanel.this.currentFile : FileInfoPanel.this.currentFile.getParentFile();
				Desktop.getDesktop().browse(target.toURI());
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
				JOptionPane.showMessageDialog(
						FileInfoPanel.this,
						"Cannot open file browser.",
						"Error",
						JOptionPane.ERROR_MESSAGE,
						new ImageIcon(FileInfoPanel.class
								.getResource("/icons/i16x16/dialog-error.png")));
			}
		}
	}
}

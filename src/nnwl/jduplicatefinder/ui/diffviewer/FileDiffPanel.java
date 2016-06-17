package nnwl.jduplicatefinder.ui.diffviewer;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class FileDiffPanel extends JPanel {
	private static final Logger logger = Logger.getLogger(FileDiffPanel.class);

	private static final long serialVersionUID = -2849473817058259997L;

	@SuppressWarnings("rawtypes")
	protected static final Class[] AVAILABLE_VIEWS = {
			ImageDiffView.class,
			TextDiffView.class
	};
	@SuppressWarnings("rawtypes")
	protected static final Class UNSUPPORTED_FILE_VIEW = UnsupportedFileDiffView.class;


	protected File left;
	protected File right;

	protected JSplitPane splitPane;
	protected JComponent leftView;
	protected JComponent rightView;
	private JPanel leftPanel;
	private JPanel rightPanel;

	protected FileDiffView[] viewInstances;
	private JLabel lblLeftlabel;
	private JLabel lblRightlabel;

	private JComponent leftOpenFileButton;
	private JComponent rightOpenFileButton;

	public FileDiffPanel() {
		setLayout(new BorderLayout(0, 0));

		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		add(splitPane, BorderLayout.CENTER);

		leftPanel = new JPanel();
		leftPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		splitPane.setLeftComponent(leftPanel);
		leftPanel.setLayout(new BorderLayout(0, 0));

		lblLeftlabel = new JLabel("<no path>");
		lblLeftlabel.setBorder(new BevelBorder(BevelBorder.RAISED));
		lblLeftlabel.setHorizontalAlignment(SwingConstants.CENTER);
		leftPanel.add(lblLeftlabel, BorderLayout.SOUTH);

		rightPanel = new JPanel();
		rightPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		splitPane.setRightComponent(rightPanel);
		rightPanel.setLayout(new BorderLayout(0, 0));

		lblRightlabel = new JLabel("<no path>");
		lblRightlabel.setBorder(new BevelBorder(BevelBorder.RAISED));
		lblRightlabel.setHorizontalAlignment(SwingConstants.CENTER);
		rightPanel.add(lblRightlabel, BorderLayout.SOUTH);

		try {
			this.initViewInstances();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		}

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Preserve user ratio if divider has been moved
				FileDiffPanel.this.splitPane.setDividerLocation(0.5);
			}
		});
	}

	private void initViewInstances() throws InstantiationException, IllegalAccessException {
		this.viewInstances = new FileDiffView[AVAILABLE_VIEWS.length];
		for (int i = 0; i < AVAILABLE_VIEWS.length; i++) {
			this.viewInstances[i] = (FileDiffView) AVAILABLE_VIEWS[i].newInstance();
		}
	}

	public void update(File left, File right) {
		this.clear();
		this.left = left;
		this.right = right;

		this.updateViews();
	}

	private void updateViews() {
		if (left == null || right == null) {
			this.lblLeftlabel.setText("<no path>");
			if (this.leftView != null) {
				leftPanel.remove(this.leftView);
				leftPanel.remove(this.leftOpenFileButton);
			}
			this.lblRightlabel.setText("<no path>");
			if (this.rightView != null) {
				rightPanel.remove(this.rightView);
				rightPanel.remove(this.rightOpenFileButton);
			}
		} else {
			this.lblLeftlabel.setText(this.left.getName());
			this.leftView = this.getViewForFile(left);
			this.leftPanel.add(leftView, BorderLayout.CENTER);
			this.leftOpenFileButton = this.createOpenFileButton(left);
			this.leftPanel.add(this.leftOpenFileButton, BorderLayout.NORTH);

			this.lblRightlabel.setText(this.right.getName());
			this.rightView = this.getViewForFile(right);
			this.rightPanel.add(rightView, BorderLayout.CENTER);
			this.rightOpenFileButton = this.createOpenFileButton(right);
			this.rightPanel.add(this.rightOpenFileButton, BorderLayout.NORTH);
		}

		this.invalidate();
		this.repaint();
	}

	private JComponent createOpenFileButton(File f) {
		JButton button = new JButton("Open file");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(f);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					JOptionPane
							.showMessageDialog(
									null,
									"Cannot open file.",
									"Error",
									JOptionPane.ERROR_MESSAGE,
									new ImageIcon(FileDiffPanel.class
											.getResource("/icons/i16x16/dialog-error.png")));
				}
			}
		});
		button.setHorizontalAlignment(SwingConstants.CENTER);
		return button;
	}

	private JComponent getViewForFile(File f) {
		JComponent view = null;
		try {
			for (FileDiffView v : this.viewInstances) {
				if (v.canHandle(f)) {
					view = v.getViewForFile(f);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (view == null) {
			try {
				view = ((FileDiffView) UNSUPPORTED_FILE_VIEW.newInstance()).getViewForFile(f);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return view;
	}

	public void clear() {
		this.left = null;
		this.right = null;

		this.updateViews();
	}
}

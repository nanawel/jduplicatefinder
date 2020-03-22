package nnwl.jduplicatefinder.ui;

import nnwl.jduplicatefinder.engine.FileFilter;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class FileFilterStackPanel extends JPanel {
	private static final long serialVersionUID = -5631292906017075812L;

	private JPanel filtersContainer;

	private JButton btnAdd;

	private JPopupMenu addMenu;

	private final Action addAction = new AddFilterAction();

	private List<FileFilter> predefinedFilters;

	/**
	 * Create the panel.
	 */
	public FileFilterStackPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new TitledBorder(null, "Ignore files and directories matching the following filters:",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		JPanel leftPanel = new JPanel();
		mainPanel.add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new GridLayout(1, 1, 0, 0));

		btnAdd = new JButton("");
		btnAdd.setAction(addAction);
		btnAdd.setIcon(new ImageIcon(FileFilterStackPanel.class.getResource("/icons/list-add.png")));
		leftPanel.add(btnAdd);

		this.createAddMenu();

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		filtersContainer = new JPanel();
		filtersContainer.setBorder(new EmptyBorder(2, 2, 2, 2));

		scrollPane.setViewportView(filtersContainer);
		filtersContainer.setLayout(new BoxLayout(filtersContainer, BoxLayout.Y_AXIS));

		// DEBUG
		//this.addEmptyFilter();
	}

	public void setPredefinedFilters(List<FileFilter> filters) {
		this.predefinedFilters = filters;
		this.createAddMenu();
	}

	protected void createAddMenu() {
		addMenu = new JPopupMenu();
		JMenuItem it = new JMenuItem("(Empty)",
				new ImageIcon(FileFilterStackPanel.class.getResource("/icons/i16x16/view-filter.png")));
		it.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileFilterStackPanel.this.addEmptyFilter();
			}
		});
		addMenu.add(it);

		addMenu.addSeparator();

		JMenu m = new JMenu("Predefined...");
		addMenu.add(m);

		if (this.predefinedFilters == null || this.predefinedFilters.isEmpty()) {
			it = new JMenuItem("<None>");
			it.setEnabled(false);
			m.add(it);
		} else {
			for (FileFilter ff : this.predefinedFilters) {
				it = new JMenuItem(ff.getTitle(),
						new ImageIcon(FileFilterStackPanel.class.getResource("/icons/i16x16/view-filter.png")));
				it.putClientProperty("filtermodel", ff);
				it.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						FileFilterStackPanel.this.addFilter((FileFilter) ((JMenuItem) e.getSource()).getClientProperty("filtermodel"));
					}
				});
				m.add(it);
			}
		}
	}

	protected Filter addFilter() {
		Filter f = new Filter();
		this.filtersContainer.add(f);
		this.filtersContainer.revalidate();
		return f;
	}

	protected Filter addFilter(FileFilter ff) {
		Filter f = new Filter(ff);
		this.filtersContainer.add(f);
		this.filtersContainer.revalidate();
		return f;
	}

	public void addEmptyFilter() {
		this.addFilter();
	}

	public void addFilter(String pattern, String matches, String type) {
		Filter f = this.addFilter();
		f.setPattern(pattern);
		f.setMatches(matches);
		f.setType(type);
	}

	public FileFilter[] getFilters() {
		List<FileFilter> filters = new ArrayList<>();
		for (int i = 0; i < this.filtersContainer.getComponentCount(); i++) {
			Filter filterPanel = (Filter) this.filtersContainer.getComponent(i);
			FileFilter ff = filterPanel.getFileFilter();
			if (ff != null) {
				filters.add(ff);
			}
		}
		return filters.toArray(new FileFilter[0]);
	}

	private class Filter extends JPanel {
		private static final long serialVersionUID = 8064480803876696578L;

		private JTextField txtPattern;

		protected ButtonGroup rdbtngrpType;

		protected JRadioButton rdbtnType[];

		protected JComboBox<String> cbboxMatches;

		@SuppressWarnings({"unchecked", "rawtypes"})
		public Filter() {
			super();

			this.setBorder(new BevelBorder(BevelBorder.RAISED));

			int gridx = 0;

			GridBagLayout gbl_this = new GridBagLayout();
			gbl_this.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
			gbl_this.rowHeights = new int[]{0, 0};
			gbl_this.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0};
			gbl_this.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			this.setLayout(gbl_this);

			JLabel lblFilter = new JLabel("Filter:");
			GridBagConstraints gbc_lblFilter = new GridBagConstraints();
			gbc_lblFilter.insets = new Insets(0, 5, 0, 5);
			gbc_lblFilter.gridx = gridx++;
			gbc_lblFilter.gridy = 0;
			this.add(lblFilter, gbc_lblFilter);

			txtPattern = new JTextField();
			txtPattern.setText("");
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 0, 5);
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = gridx++;
			gbc_textField.gridy = 0;
			this.add(txtPattern, gbc_textField);
			txtPattern.setColumns(10);

			JLabel lblMatches = new JLabel("Matches:");
			GridBagConstraints gbc_lblMatches = new GridBagConstraints();
			gbc_lblMatches.anchor = GridBagConstraints.EAST;
			gbc_lblMatches.insets = new Insets(0, 5, 0, 5);
			gbc_lblMatches.gridx = gridx++;
			gbc_lblMatches.gridy = 0;
			this.add(lblMatches, gbc_lblMatches);

			cbboxMatches = new JComboBox<String>();
			cbboxMatches.setModel(new DefaultComboBoxModel(FileFilter.MATCH_CHOICES));
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.insets = new Insets(0, 0, 0, 5);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = gridx++;
			gbc_comboBox.gridy = 0;
			this.add(cbboxMatches, gbc_comboBox);

			JLabel lblType = new JLabel("Type:");
			GridBagConstraints gbc_lblType = new GridBagConstraints();
			gbc_lblType.insets = new Insets(0, 5, 0, 5);
			gbc_lblType.gridx = gridx++;
			gbc_lblType.gridy = 0;
			this.add(lblType, gbc_lblType);

			rdbtngrpType = new ButtonGroup();
			rdbtnType = new JRadioButton[FileFilter.TYPE_CHOICES.length];
			int n = 0;
			for (int i = 0; i < FileFilter.TYPE_CHOICES.length; i++) {
				String type = FileFilter.TYPE_CHOICES[i];
				String tooltip = FileFilter.TYPE_TOOLTIPS[i];

				rdbtnType[i] = new JRadioButton(type);
				rdbtnType[i].setToolTipText(tooltip);
				rdbtnType[i].setActionCommand(type);
				if (n++ == 0) {
					rdbtnType[i].setSelected(true);
				}
				GridBagConstraints gbc_rdbtnType = new GridBagConstraints();
				gbc_rdbtnType.insets = new Insets(0, 0, 0, 5);
				gbc_rdbtnType.gridx = gridx++;
				gbc_rdbtnType.gridy = 0;
				this.add(rdbtnType[i], gbc_rdbtnType);
				rdbtngrpType.add(rdbtnType[i]);
			}

			JButton btnRemove = new JButton("");
			btnRemove.setMargin(new Insets(0, 0, 0, 0));
			GridBagConstraints gbc_btnRemove = new GridBagConstraints();
			btnRemove.setIcon(new ImageIcon(FileFilterStackPanel.class.getResource("/icons/i16x16/list-remove.png")));
			gbc_btnRemove.gridx = gridx++;
			gbc_btnRemove.gridy = 0;
			gbc_btnRemove.insets = new Insets(0, 5, 0, 5);
			btnRemove.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					((JButton) e.getSource()).setEnabled(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							Container parent = Filter.this.getParent();
							parent.remove(Filter.this);
							parent.revalidate();
							parent.repaint();
						}
					});
				}
			});
			this.add(btnRemove);

			// Avoid growing vertically
			Dimension preferredSize = this.getPreferredSize();
			preferredSize.width = this.getMaximumSize().width;
			this.setMaximumSize(preferredSize);
		}

		public Filter(FileFilter ff) {
			this();
			this.setPattern(ff.getPattern());
			this.setMatches(ff.getMatches());
			this.setType(ff.getType());
		}

		public void setPattern(String pattern) {
			this.txtPattern.setText(pattern);
		}

		public void setMatches(String matches) {
			for (int i = 0; i < FileFilter.MATCH_CHOICES.length; i++) {
				if (FileFilter.MATCH_CHOICES[i].equals(matches)) {
					this.cbboxMatches.setSelectedIndex(i);
					break;
				}
			}
		}

		public void setType(String type) {
			for (int i = 0; i < FileFilter.TYPE_CHOICES.length; i++) {
				if (FileFilter.TYPE_CHOICES[i].equals(type)) {
					this.rdbtngrpType.setSelected(rdbtnType[i].getModel(), true);
					break;
				}
			}
		}

		public FileFilter getFileFilter() {
			if (this.txtPattern.getText().isEmpty()) {
				return null;
			}
			FileFilter ff = new FileFilter(this.txtPattern.getText(), (String) this.cbboxMatches.getSelectedItem(),
					this.rdbtngrpType.getSelection().getActionCommand());
			return ff;
		}
	}

	@SuppressWarnings("serial")
	private class AddFilterAction extends AbstractAction {
		public AddFilterAction() {
			putValue(NAME, "");
			putValue(SHORT_DESCRIPTION, "Add new filter");
		}

		public void actionPerformed(ActionEvent e) {
			FileFilterStackPanel p = FileFilterStackPanel.this;
			p.addMenu.show(FileFilterStackPanel.this, p.btnAdd.getWidth(), p.btnAdd.getHeight() / 2);
		}
	}
}

package nnwl.jduplicatefinder.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import nnwl.jduplicatefinder.engine.FileFilter;

public class FileFilterStackPanel extends JPanel
{
	private static final long serialVersionUID = -5631292906017075812L;

	private JPanel filtersContainer;

	private final Action addAction = new AddFilterAction();

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

		JButton btnAdd = new JButton("");
		btnAdd.setAction(addAction);
		btnAdd.setIcon(new ImageIcon(FileFilterStackPanel.class.getResource("/icons/list-add.png")));
		leftPanel.add(btnAdd);

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

	public void addEmptyFilter() {
		Filter f = new Filter();
		this.filtersContainer.add(f);
		this.filtersContainer.revalidate();
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

	private class Filter extends JPanel
	{
		private static final long serialVersionUID = 8064480803876696578L;

		private JTextField txtPattern;

		protected ButtonGroup rdbtngrpType;

		protected JComboBox<String> cbboxMatches;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Filter() {
			super();

			this.setBorder(new BevelBorder(BevelBorder.RAISED));

			int gridx = 0;

			GridBagLayout gbl_this = new GridBagLayout();
			gbl_this.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			gbl_this.rowHeights = new int[] { 0, 0 };
			gbl_this.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0 };
			gbl_this.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
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
			int n = 0;
			for (String type : FileFilter.TYPE_CHOICES) {
				JRadioButton rdbtnType = new JRadioButton(type);
				rdbtnType.setActionCommand(type);
				if (n++ == 0) {
					rdbtnType.setSelected(true);
				}
				GridBagConstraints gbc_rdbtnType = new GridBagConstraints();
				gbc_rdbtnType.insets = new Insets(0, 0, 0, 5);
				gbc_rdbtnType.gridx = gridx++;
				gbc_rdbtnType.gridy = 0;
				this.add(rdbtnType, gbc_rdbtnType);
				rdbtngrpType.add(rdbtnType);
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
			Dimension preferedSize = this.getPreferredSize();
			preferedSize.width = this.getMaximumSize().width;
			this.setMaximumSize(preferedSize);
		}

		public FileFilter getFileFilter() {
			if (this.txtPattern.getText().isEmpty()) {
				return null;
			}
			return new FileFilter(this.txtPattern.getText(), (String) this.cbboxMatches.getSelectedItem(),
					this.rdbtngrpType.getSelection().getActionCommand());
		}
	}

	@SuppressWarnings("serial")
	private class AddFilterAction extends AbstractAction
	{
		public AddFilterAction() {
			putValue(NAME, "");
			putValue(SHORT_DESCRIPTION, "Add new filter");
		}

		public void actionPerformed(ActionEvent e) {
			FileFilterStackPanel.this.addEmptyFilter();
		}
	}
}

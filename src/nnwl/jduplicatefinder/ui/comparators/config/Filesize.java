package nnwl.jduplicatefinder.ui.comparators.config;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class Filesize extends JPanel implements ComparatorConfigPanel {
	private static final long serialVersionUID = 272080702154953921L;

	public static final String TAB_TITLE = "Filesize Comparator";

	private JTextField txtFilesizeMargin;

	private JComboBox<String> cbboxFilesizeMarginType;
	private JLabel lblComparatorWeight;
	private JTextField txtComparatorWeight;

	public Filesize() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		this.initialize();
	}

	public void initialize() {
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{120, 54, 0, 110, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gbl_panel_2);

		lblComparatorWeight = new JLabel("Comparator weight");
		lblComparatorWeight.setVerticalAlignment(SwingConstants.BOTTOM);
		GridBagConstraints gbc_lblComparatorWeight = new GridBagConstraints();
		gbc_lblComparatorWeight.anchor = GridBagConstraints.EAST;
		gbc_lblComparatorWeight.insets = new Insets(5, 5, 5, 5);
		gbc_lblComparatorWeight.gridx = 0;
		gbc_lblComparatorWeight.gridy = 0;
		add(lblComparatorWeight, gbc_lblComparatorWeight);
		lblComparatorWeight.setLabelFor(txtComparatorWeight);

		txtComparatorWeight = new JTextField();
		txtComparatorWeight.setText("1");
		txtComparatorWeight.setColumns(4);
		GridBagConstraints gbc_txtComparatorWeight = new GridBagConstraints();
		gbc_txtComparatorWeight.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtComparatorWeight.insets = new Insets(5, 5, 5, 5);
		gbc_txtComparatorWeight.gridx = 1;
		gbc_txtComparatorWeight.gridy = 0;
		add(txtComparatorWeight, gbc_txtComparatorWeight);

		JLabel lblFilesizeMargin = new JLabel("Filesize margin");
		GridBagConstraints gbc_lblFilesizeMargin = new GridBagConstraints();
		gbc_lblFilesizeMargin.anchor = GridBagConstraints.EAST;
		gbc_lblFilesizeMargin.insets = new Insets(5, 5, 5, 5);
		gbc_lblFilesizeMargin.gridx = 0;
		gbc_lblFilesizeMargin.gridy = 1;
		this.add(lblFilesizeMargin, gbc_lblFilesizeMargin);

		txtFilesizeMargin = new JTextField();
		lblFilesizeMargin.setLabelFor(txtFilesizeMargin);
		txtFilesizeMargin.setText("0");
		GridBagConstraints gbc_txtFilesizeMargin = new GridBagConstraints();
		gbc_txtFilesizeMargin.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFilesizeMargin.insets = new Insets(5, 5, 5, 5);
		gbc_txtFilesizeMargin.gridx = 1;
		gbc_txtFilesizeMargin.gridy = 1;
		this.add(txtFilesizeMargin, gbc_txtFilesizeMargin);
		txtFilesizeMargin.setColumns(10);

		cbboxFilesizeMarginType = new JComboBox<String>();
		cbboxFilesizeMarginType.setModel(new DefaultComboBoxModel<String>(new String[]{"bytes", "kibibytes", "percent"}));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(5, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 1;
		add(cbboxFilesizeMarginType, gbc_comboBox);
	}

	@Override
	public Map<String, Object> getComparatorParameters() throws Exception {
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		switch (this.cbboxFilesizeMarginType.getSelectedIndex()) {
			// bytes
			case 0:
				parameters.put("filesize.margin_type", nnwl.jduplicatefinder.engine.comparators.Filesize.MARGIN_TYPE_BYTES);
				parameters.put("filesize.margin", this.txtFilesizeMargin.getText());
				break;

			// kibibytes
			case 1:
				parameters.put("filesize.margin_type", nnwl.jduplicatefinder.engine.comparators.Filesize.MARGIN_TYPE_BYTES);
				parameters.put("filesize.margin", Long.valueOf(this.txtFilesizeMargin.getText()) * 1024);
				break;

			// percent
			case 2:
				parameters.put("filesize.margin_type", nnwl.jduplicatefinder.engine.comparators.Filesize.MARGIN_TYPE_PERCENTAGE);
				parameters.put("filesize.margin", this.txtFilesizeMargin.getText());
				break;
		}

		return parameters;
	}

	@Override
	public String getTitle() {
		return TAB_TITLE;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(Filesize.class.getResource("/icons/i16x16/find-duplicate.png"));
	}

	@Override
	public int getComparatorWeight() {
		return Integer.valueOf(this.txtComparatorWeight.getText());
	}

	@Override
	public AbstractDuplicateComparator getComparatorInstance() {
		return new nnwl.jduplicatefinder.engine.comparators.Filesize();
	}

	@Override
	public AbstractDuplicateComparator getConfiguredComparatorInstance() throws Exception {
		AbstractDuplicateComparator f = this.getComparatorInstance();
		f.setWeight(this.getComparatorWeight());
		f.configure(this.getComparatorParameters());

		return f;
	}
}

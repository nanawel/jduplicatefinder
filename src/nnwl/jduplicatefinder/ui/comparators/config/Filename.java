package nnwl.jduplicatefinder.ui.comparators.config;

import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
@SuppressWarnings("rawtypes")
public class Filename extends JPanel implements ComparatorConfigPanel {
	private static final long serialVersionUID = 959865421345674739L;

	public static final String TAB_TITLE = "Filename Comparator";
	private JLabel lblComparatorWeight;
	private JTextField txtComparatorWeight;
	private JLabel lblDateForComparison;
	private JTextField txtIgnorePattern;
	private JLabel lblLocale;
	private JComboBox cboxLocale;
	private JLabel lblMinimalSimilarity;
	private JTextField txtMinimalSimilarity;
	private JLabel lblSearchIn;
	private JComboBox cboxSearchIn;
	private JLabel lblAlphanumericOnly;
	private JCheckBox cboxAlphanumericOnly;

	public Filename() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		this.initialize();
	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] {120, 120, 30, 120, 120};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gbl_panel_2);

		lblComparatorWeight = new JLabel("Comparator weight", SwingConstants.RIGHT);
		lblComparatorWeight.setVerticalAlignment(SwingConstants.BOTTOM);
		GridBagConstraints gbc_lblComparatorWeight = new GridBagConstraints();
		gbc_lblComparatorWeight.anchor = GridBagConstraints.EAST;
		gbc_lblComparatorWeight.insets = new Insets(5, 5, 5, 5);
		gbc_lblComparatorWeight.gridx = 0;
		gbc_lblComparatorWeight.gridy = 0;
		add(lblComparatorWeight, gbc_lblComparatorWeight);

		txtComparatorWeight = new JTextField();
		lblComparatorWeight.setLabelFor(txtComparatorWeight);
		txtComparatorWeight.setText("1");
		GridBagConstraints gbc_txtComparatorWeight = new GridBagConstraints();
		gbc_txtComparatorWeight.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtComparatorWeight.insets = new Insets(5, 5, 5, 5);
		gbc_txtComparatorWeight.gridx = 1;
		gbc_txtComparatorWeight.gridy = 0;
		add(txtComparatorWeight, gbc_txtComparatorWeight);
		
		lblLocale = new JLabel("Locale", SwingConstants.RIGHT);
		GridBagConstraints gbc_lblLocale = new GridBagConstraints();
		gbc_lblLocale.anchor = GridBagConstraints.EAST;
		gbc_lblLocale.insets = new Insets(5, 5, 5, 5);
		gbc_lblLocale.gridx = 3;
		gbc_lblLocale.gridy = 0;
		add(lblLocale, gbc_lblLocale);
		
		cboxLocale = new JComboBox();
		lblLocale.setLabelFor(cboxLocale);
		GridBagConstraints gbc_cboxLocale = new GridBagConstraints();
		cboxLocale.setModel(new DefaultComboBoxModel<String>(nnwl.jduplicatefinder.engine.comparators.Filename.getAvailableLocales()));
		cboxLocale.setSelectedItem(Locale.getDefault().toLanguageTag());
		gbc_cboxLocale.insets = new Insets(5, 5, 5, 5);
		gbc_cboxLocale.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboxLocale.gridx = 4;
		gbc_cboxLocale.gridy = 0;
		add(cboxLocale, gbc_cboxLocale);

		JLabel lblTimeMargin = new JLabel("Ignore pattern", SwingConstants.RIGHT);
		GridBagConstraints gbc_lblTimeMargin = new GridBagConstraints();
		gbc_lblTimeMargin.anchor = GridBagConstraints.EAST;
		gbc_lblTimeMargin.insets = new Insets(5, 5, 5, 5);
		gbc_lblTimeMargin.gridx = 0;
		gbc_lblTimeMargin.gridy = 1;
		this.add(lblTimeMargin, gbc_lblTimeMargin);
		
		txtIgnorePattern = new JTextField();
		lblTimeMargin.setLabelFor(txtIgnorePattern);
		GridBagConstraints gbc_txtIgnorePattern = new GridBagConstraints();
		gbc_txtIgnorePattern.insets = new Insets(5, 5, 5, 5);
		gbc_txtIgnorePattern.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtIgnorePattern.gridx = 1;
		gbc_txtIgnorePattern.gridy = 1;
		add(txtIgnorePattern, gbc_txtIgnorePattern);
		
		lblMinimalSimilarity = new JLabel("Minimal similarity (%)", SwingConstants.RIGHT);
		GridBagConstraints gbc_lblMinimalSimilarity = new GridBagConstraints();
		gbc_lblMinimalSimilarity.anchor = GridBagConstraints.EAST;
		gbc_lblMinimalSimilarity.insets = new Insets(5, 5, 5, 5);
		gbc_lblMinimalSimilarity.gridx = 3;
		gbc_lblMinimalSimilarity.gridy = 1;
		add(lblMinimalSimilarity, gbc_lblMinimalSimilarity);
		
		txtMinimalSimilarity = new JTextField();
		lblMinimalSimilarity.setLabelFor(txtMinimalSimilarity);
		txtMinimalSimilarity.setText("80");
		GridBagConstraints gbc_txtMinimalSimilarity = new GridBagConstraints();
		gbc_txtMinimalSimilarity.insets = new Insets(5, 5, 5, 5);
		gbc_txtMinimalSimilarity.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMinimalSimilarity.gridx = 4;
		gbc_txtMinimalSimilarity.gridy = 1;
		add(txtMinimalSimilarity, gbc_txtMinimalSimilarity);
		txtMinimalSimilarity.setColumns(10);
		
		lblSearchIn = new JLabel("Search in", SwingConstants.RIGHT);
		GridBagConstraints gbc_lblSearchIn = new GridBagConstraints();
		gbc_lblSearchIn.insets = new Insets(0, 0, 0, 5);
		gbc_lblSearchIn.gridx = 0;
		gbc_lblSearchIn.gridy = 2;
		add(lblSearchIn, gbc_lblSearchIn);
		
		cboxSearchIn = new JComboBox();
		lblSearchIn.setLabelFor(cboxSearchIn);
		GridBagConstraints gbc_cboxSearchIn = new GridBagConstraints();
		cboxSearchIn.setModel(new DefaultComboBoxModel<String>(nnwl.jduplicatefinder.engine.comparators.Filename.getAvailableSearchInOptions()));
		cboxSearchIn.setSelectedItem("filename");
		gbc_cboxSearchIn.insets = new Insets(5, 5, 5, 5);
		gbc_cboxSearchIn.fill = GridBagConstraints.HORIZONTAL;
		gbc_cboxSearchIn.gridx = 1;
		gbc_cboxSearchIn.gridy = 2;
		add(cboxSearchIn, gbc_cboxSearchIn);

		cboxAlphanumericOnly = new JCheckBox();
		cboxAlphanumericOnly.setSelected(true);
		GridBagConstraints gbc_cboxAlphanumericOnly = new GridBagConstraints();
		gbc_cboxAlphanumericOnly.insets = new Insets(5, 5, 0, 5);
		gbc_cboxAlphanumericOnly.gridx = 3;
		gbc_cboxAlphanumericOnly.gridy = 2;
		gbc_cboxAlphanumericOnly.anchor = GridBagConstraints.LINE_END;
		add(cboxAlphanumericOnly, gbc_cboxAlphanumericOnly);

		lblAlphanumericOnly = new JLabel("Ignore non-alphanumeric characters", SwingConstants.LEFT);
		lblAlphanumericOnly.setLabelFor(cboxAlphanumericOnly);
		GridBagConstraints gbc_lblAlphanumericOnly = new GridBagConstraints();
		gbc_lblAlphanumericOnly.insets = new Insets(5, 5, 0, 0);
		gbc_lblAlphanumericOnly.gridx = 4;
		gbc_lblAlphanumericOnly.gridy = 2;
		gbc_lblAlphanumericOnly.anchor = GridBagConstraints.LINE_START;
		add(lblAlphanumericOnly, gbc_lblAlphanumericOnly);
	}
	
	@Override
	public Map<String, Object> getComparatorParameters() throws Exception {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("filename.locale", this.cboxLocale.getSelectedItem());
		parameters.put("filename.ignore_pattern", this.txtIgnorePattern.getText());
		parameters.put("filename.min_similarity", this.txtMinimalSimilarity.getText());
		parameters.put("filename.search_in", this.cboxSearchIn.getSelectedItem());
		parameters.put("filename.alphanumeric_only", this.cboxAlphanumericOnly.isSelected());

		return parameters;
	}

	@Override
	public String getTitle() {
		return TAB_TITLE;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(Filename.class.getResource("/icons/i16x16/find-duplicate.png"));
	}

	@Override
	public int getComparatorWeight() {
		return Integer.valueOf(this.txtComparatorWeight.getText());
	}

	@Override
	public AbstractDuplicateComparator getComparatorInstance() {
		return new nnwl.jduplicatefinder.engine.comparators.Filename();
	}

	@Override
	public AbstractDuplicateComparator getConfiguredComparatorInstance() throws Exception {
		AbstractDuplicateComparator f = this.getComparatorInstance();
		f.setWeight(this.getComparatorWeight());
		f.configure(this.getComparatorParameters());

		return f;
	}
}

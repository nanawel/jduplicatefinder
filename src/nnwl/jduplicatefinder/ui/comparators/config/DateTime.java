package nnwl.jduplicatefinder.ui.comparators.config;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;
import javax.swing.border.EmptyBorder;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
@SuppressWarnings("rawtypes")
public class DateTime extends JPanel implements ComparatorConfigPanel
{
	private static final long serialVersionUID = 959865421345674739L;

	public static final String TAB_TITLE = "Date-Time Comparator";
	
	private JTextField txtTimeMargin;

	private JComboBox<String> cbboxTimeMarginUnit;
	private JLabel lblComparatorWeight;
	private JTextField txtComparatorWeight;
//	private JCheckBox chckbxIgnoreHour;
	private JLabel lblDateForComparison;
	private JComboBox<String> cbboxDateType;

	public DateTime() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		this.initialize();
	}
	
	@SuppressWarnings("unchecked")
	public void initialize() {
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{120, 54, 0, 20, 110, 0, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
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
		
		lblDateForComparison = new JLabel("Date for comparison");
		lblDateForComparison.setVerticalAlignment(SwingConstants.BOTTOM);
		GridBagConstraints gbc_lblDateType = new GridBagConstraints();
		gbc_lblDateType.anchor = GridBagConstraints.EAST;
		gbc_lblDateType.insets = new Insets(0, 0, 5, 5);
		gbc_lblDateType.gridx = 4;
		gbc_lblDateType.gridy = 0;
		add(lblDateForComparison, gbc_lblDateType);
		
		cbboxDateType = new JComboBox();
		cbboxDateType.setModel(new DefaultComboBoxModel(new String[] {"creation", "modification"}));
		GridBagConstraints gbc_cbboxDateType;
		gbc_cbboxDateType = new GridBagConstraints();
		gbc_cbboxDateType.insets = new Insets(0, 0, 5, 5);
		gbc_cbboxDateType.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbboxDateType.gridx = 5;
		gbc_cbboxDateType.gridy = 0;
		add(cbboxDateType, gbc_cbboxDateType);
		
		JLabel lblTimeMargin = new JLabel("Time margin");
		GridBagConstraints gbc_lblTimeMargin = new GridBagConstraints();
		gbc_lblTimeMargin.anchor = GridBagConstraints.EAST;
		gbc_lblTimeMargin.insets = new Insets(5, 5, 5, 5);
		gbc_lblTimeMargin.gridx = 0;
		gbc_lblTimeMargin.gridy = 1;
		this.add(lblTimeMargin, gbc_lblTimeMargin);
		
		txtTimeMargin = new JTextField();
		lblTimeMargin.setLabelFor(txtTimeMargin);
		txtTimeMargin.setText("0");
		GridBagConstraints gbc_txtTimeMargin = new GridBagConstraints();
		gbc_txtTimeMargin.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTimeMargin.insets = new Insets(5, 5, 5, 5);
		gbc_txtTimeMargin.gridx = 1;
		gbc_txtTimeMargin.gridy = 1;
		this.add(txtTimeMargin, gbc_txtTimeMargin);
		txtTimeMargin.setColumns(10);
		
		cbboxTimeMarginUnit = new JComboBox<String>();
		cbboxTimeMarginUnit.setModel(new DefaultComboBoxModel(new String[] {"seconds", "minutes", "hours", "days"}));
		GridBagConstraints gbc_cbboxTimeMarginUnit = new GridBagConstraints();
		gbc_cbboxTimeMarginUnit.insets = new Insets(5, 0, 5, 5);
		gbc_cbboxTimeMarginUnit.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbboxTimeMarginUnit.gridx = 2;
		gbc_cbboxTimeMarginUnit.gridy = 1;
		add(cbboxTimeMarginUnit, gbc_cbboxTimeMarginUnit);
		
//		chckbxIgnoreHour = new JCheckBox("Ignore 1 hour difference");
//		GridBagConstraints gbc_chckbxIgnoreHour = new GridBagConstraints();
//		gbc_chckbxIgnoreHour.anchor = GridBagConstraints.WEST;
//		gbc_chckbxIgnoreHour.gridwidth = 2;
//		gbc_chckbxIgnoreHour.insets = new Insets(0, 0, 5, 5);
//		gbc_chckbxIgnoreHour.gridx = 4;
//		gbc_chckbxIgnoreHour.gridy = 1;
//		add(chckbxIgnoreHour, gbc_chckbxIgnoreHour);
	}

	@Override
	public Map<String, Object> getComparatorParameters() throws Exception {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		int margin = Integer.valueOf(this.txtTimeMargin.getText());
		switch (this.cbboxTimeMarginUnit.getSelectedIndex()) {
			// days
			case 3:
				margin *= 24;
				
			// hours
			case 2:
				margin *= 60;
				
			// minutes
			case 1:
				margin *= 60;
		}
		parameters.put("datetime.time_margin", margin);
		
		switch (this.cbboxDateType.getSelectedIndex()) {
			case 0:
				parameters.put("datetime.date_type", nnwl.jduplicatefinder.engine.comparators.DateTime.DATE_TYPE_CREATED);
				break;
				
			case 1:
				parameters.put("datetime.date_type", nnwl.jduplicatefinder.engine.comparators.DateTime.DATE_TYPE_MODIFIED);
				break;
		}
		
//		parameters.put("datetime.ignore_one_hour_diff", chckbxIgnoreHour.isSelected() ? true : false);
		
		return parameters;
	}

	@Override
	public String getTitle() {
		return TAB_TITLE;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(DateTime.class.getResource("/icons/i16x16/find-duplicate.png"));
	}

	@Override
	public int getComparatorWeight() {
		return Integer.valueOf(this.txtComparatorWeight.getText());
	}

	@Override
	public AbstractDuplicateComparator getComparatorInstance() {
		return new nnwl.jduplicatefinder.engine.comparators.DateTime();
	}
	
	@Override
	public AbstractDuplicateComparator getConfiguredComparatorInstance() throws Exception {
		AbstractDuplicateComparator f = this.getComparatorInstance();
		f.setWeight(this.getComparatorWeight());
		f.configure(this.getComparatorParameters());
		
		return f;
	}
}

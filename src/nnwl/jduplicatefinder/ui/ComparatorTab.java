package nnwl.jduplicatefinder.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class ComparatorTab extends JPanel
{
	private static final long serialVersionUID = -7370204583763588865L;
	
	private JCheckBox chckbxTitle;

	/**
	 * Create the panel.
	 */
	public ComparatorTab(String label, Icon icon) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 21, 0};
		gridBagLayout.rowHeights = new int[]{21, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		chckbxTitle = new JCheckBox("");
		GridBagConstraints gbc_chckbxTitle = new GridBagConstraints();
		gbc_chckbxTitle.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxTitle.fill = GridBagConstraints.VERTICAL;
		gbc_chckbxTitle.anchor = GridBagConstraints.WEST;
		gbc_chckbxTitle.gridx = 0;
		gbc_chckbxTitle.gridy = 0;
		add(chckbxTitle, gbc_chckbxTitle);
		
		JLabel lblTitle = new JLabel(label, icon, SwingConstants.LEFT);
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTitle.gridx = 1;
		gbc_lblTitle.gridy = 0;
		add(lblTitle, gbc_lblTitle);
	}

	public boolean isChecked() {
		return this.chckbxTitle.isSelected();
	}
	
	public void check(boolean c) {
		this.chckbxTitle.setSelected(c);
	}
}

package nnwl.jduplicatefinder.ui.comparators.config;

import java.awt.Color;
import java.awt.Font;
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
public class Digest extends JPanel implements ComparatorConfigPanel
{
	private static final long serialVersionUID = 272080702154953921L;
	
	public static final String TAB_TITLE = "Digest Comparator";

	private JComboBox<String> cbboxDigestAlgorithm;
	private JLabel lblComparatorWeight;
	private JTextField txtComparatorWeight;
	private JTextField txtChunksize;
	private JLabel lblChunkSize;
	private JComboBox<String> cbboxChunksizeUnit;
	private JLabel lblOnlyAChunk;

	public Digest() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		this.initialize();
	}
	
	public void initialize() {
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{120, 0, 20, 20, 0, 156, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		txtComparatorWeight.setText("5");
		txtComparatorWeight.setColumns(4);
		GridBagConstraints gbc_txtComparatorWeight = new GridBagConstraints();
		gbc_txtComparatorWeight.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtComparatorWeight.insets = new Insets(5, 5, 5, 5);
		gbc_txtComparatorWeight.gridx = 1;
		gbc_txtComparatorWeight.gridy = 0;
		add(txtComparatorWeight, gbc_txtComparatorWeight);
		
		lblChunkSize = new JLabel("Chunk size");
		GridBagConstraints gbc_lblChunkSize = new GridBagConstraints();
		gbc_lblChunkSize.anchor = GridBagConstraints.WEST;
		gbc_lblChunkSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblChunkSize.gridx = 3;
		gbc_lblChunkSize.gridy = 0;
		add(lblChunkSize, gbc_lblChunkSize);
		lblChunkSize.setLabelFor(txtChunksize);
		
		txtChunksize = new JTextField();
		txtChunksize.setText("512");
		GridBagConstraints gbc_txtChunksize = new GridBagConstraints();
		gbc_txtChunksize.insets = new Insets(5, 5, 5, 5);
		gbc_txtChunksize.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtChunksize.gridx = 4;
		gbc_txtChunksize.gridy = 0;
		add(txtChunksize, gbc_txtChunksize);
		txtChunksize.setColumns(10);
		
		cbboxChunksizeUnit = new JComboBox<String>();
		cbboxChunksizeUnit.setModel(new DefaultComboBoxModel<String>(new String[] {"bytes", "kibibytes", "mebibytes"}));
		cbboxChunksizeUnit.setSelectedIndex(1);
		GridBagConstraints gbc_cbboxChunksizeUnit = new GridBagConstraints();
		gbc_cbboxChunksizeUnit.anchor = GridBagConstraints.WEST;
		gbc_cbboxChunksizeUnit.insets = new Insets(0, 0, 5, 0);
		gbc_cbboxChunksizeUnit.gridx = 5;
		gbc_cbboxChunksizeUnit.gridy = 0;
		add(cbboxChunksizeUnit, gbc_cbboxChunksizeUnit);
		
		JLabel lblDigestAlgorithm = new JLabel("Digest algorithm");
		GridBagConstraints gbc_lblDigestAlgorithm = new GridBagConstraints();
		gbc_lblDigestAlgorithm.anchor = GridBagConstraints.EAST;
		gbc_lblDigestAlgorithm.insets = new Insets(5, 5, 5, 5);
		gbc_lblDigestAlgorithm.gridx = 0;
		gbc_lblDigestAlgorithm.gridy = 1;
		this.add(lblDigestAlgorithm, gbc_lblDigestAlgorithm);
		
		cbboxDigestAlgorithm = new JComboBox<String>();
		cbboxDigestAlgorithm.setModel(new DefaultComboBoxModel<String>(nnwl.jduplicatefinder.engine.comparators.Digest.getAvailableAlgorithms()));
		cbboxDigestAlgorithm.setSelectedIndex(1);
		GridBagConstraints gbc_cbboxDigestAlgorithm = new GridBagConstraints();
		gbc_cbboxDigestAlgorithm.insets = new Insets(5, 5, 5, 5);
		gbc_cbboxDigestAlgorithm.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbboxDigestAlgorithm.gridx = 1;
		gbc_cbboxDigestAlgorithm.gridy = 1;
		add(cbboxDigestAlgorithm, gbc_cbboxDigestAlgorithm);
		
		lblOnlyAChunk = new JLabel("Only a chunk of this size from the beginning of the file will be used in comparison");
		lblOnlyAChunk.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnlyAChunk.setForeground(Color.GRAY);
		lblOnlyAChunk.setFont(new Font("Tahoma", Font.ITALIC, 11));
		GridBagConstraints gbc_lblOnlyAChunk = new GridBagConstraints();
		gbc_lblOnlyAChunk.gridwidth = 3;
		gbc_lblOnlyAChunk.anchor = GridBagConstraints.WEST;
		gbc_lblOnlyAChunk.insets = new Insets(0, 0, 5, 0);
		gbc_lblOnlyAChunk.gridx = 3;
		gbc_lblOnlyAChunk.gridy = 1;
		add(lblOnlyAChunk, gbc_lblOnlyAChunk);
	}

	@Override
	public Map<String, Object> getComparatorParameters() throws Exception {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("digest.algorithm", this.cbboxDigestAlgorithm.getSelectedItem());
		switch (this.cbboxChunksizeUnit.getSelectedIndex()) {
			// bytes
			case 0:
				parameters.put("digest.chunksize", this.txtChunksize.getText());
				break;
				
			// kibibytes
			case 1:
				parameters.put("digest.chunksize", Long.valueOf(this.txtChunksize.getText()) * 1024);
				break;
				
			// mebibytes
			case 2:
				parameters.put("digest.chunksize", Long.valueOf(this.txtChunksize.getText()) * 1024 * 1024);
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
		return new ImageIcon(Digest.class.getResource("/icons/i16x16/find-duplicate.png"));
	}

	@Override
	public int getComparatorWeight() {
		return Integer.valueOf(this.txtComparatorWeight.getText());
	}

	@Override
	public AbstractDuplicateComparator getComparatorInstance() {
		return new nnwl.jduplicatefinder.engine.comparators.Digest();
	}
	
	@Override
	public AbstractDuplicateComparator getConfiguredComparatorInstance() throws Exception {
		AbstractDuplicateComparator f = this.getComparatorInstance();
		f.setWeight(this.getComparatorWeight());
		f.configure(this.getComparatorParameters());
		
		return f;
	}
}

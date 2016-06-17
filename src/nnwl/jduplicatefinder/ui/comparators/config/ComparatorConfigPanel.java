package nnwl.jduplicatefinder.ui.comparators.config;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;

import javax.swing.*;
import java.util.Map;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public interface ComparatorConfigPanel {
	String getTitle();

	Icon getIcon();

	Map<String, Object> getComparatorParameters() throws Exception;

	int getComparatorWeight();

	AbstractDuplicateComparator getComparatorInstance();

	AbstractDuplicateComparator getConfiguredComparatorInstance() throws Exception;
}

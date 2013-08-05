package nnwl.jduplicatefinder.ui.comparators.config;

import java.util.Map;

import javax.swing.Icon;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;

public interface ComparatorConfigPanel
{
	String getTitle();
	
	Icon getIcon();
	
	Map<String, Object> getComparatorParameters() throws Exception;

	int getComparatorWeight();
	
	AbstractDuplicateComparator getComparatorInstance();

	AbstractDuplicateComparator getConfiguredComparatorInstance() throws Exception;
}

package nnwl.jduplicatefinder.engine;

import java.io.File;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;

public class SimilarityResult
{
	protected File referenceFile;
	
	protected File similarFile;
	
	/**
	 * 0   : totally different
	 * 100 : exactly similar
	 */
	protected int similarity = 0;
	
	protected String code;

	protected float weight = 1;
	
	protected Object descObject = null;
	
	protected AbstractDuplicateComparator comparator;
	
	
	public File getSimilarFile() {
		return similarFile;
	}

	public void setSimilarFile(File file) {
		this.similarFile = file;
	}

	public long getSimilarity() {
		return similarity;
	}

	public void setSimilarity(int similarity) {
//		if (similarity < 0 || similarity > 100) {
//			throw new IllegalArgumentException("Similarity must be an integer between 0 and 100. " + similarity + " given.");
//		}
		this.similarity = similarity;
	}

	public String getDescription() {
		if (this.comparator == null) {
			return "";
		}
		else {
			return this.comparator.getSimilarityResultDescription(this);
		}
	}
	
	public AbstractDuplicateComparator getComparator() {
		return comparator;
	}
	
	public void setComparator(AbstractDuplicateComparator comparator) {
		this.comparator = comparator;
	}

	public File getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(File referenceFile) {
		this.referenceFile = referenceFile;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public Object getDescObject() {
		return descObject;
	}

	public void setDescObject(Object descObject) {
		this.descObject = descObject;
	}

	public String toString() {
		String code = this.code;
		if (this.code == null) {
			code = (this.comparator != null && !this.comparator.getCode().isEmpty()) ? this.comparator.getCode() : "<Unknown>";
		}
		String output = "[" + String.format("%3d", this.similarity) + "% / " + code + "] "
			+ this.similarFile.getPath();
		String desc = this.getDescription();
		if (!desc.isEmpty()) {
			output.concat(" (" + desc + ")");
		}
		return output;
	}
}

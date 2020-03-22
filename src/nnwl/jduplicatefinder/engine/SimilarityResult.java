package nnwl.jduplicatefinder.engine;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;

import java.nio.file.Path;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class SimilarityResult {
	protected Path referenceFile;

	protected Path similarFile;

	/**
	 * 0   : totally different
	 * 100 : exactly similar
	 */
	protected int similarity = 0;

	protected String code;

	protected float weight = 1;

	protected Object descObject = null;

	protected AbstractDuplicateComparator comparator;


	public Path getSimilarFile() {
		return similarFile;
	}

	public void setSimilarFile(Path file) {
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
		} else {
			return this.comparator.getSimilarityResultDescription(this);
		}
	}

	public AbstractDuplicateComparator getComparator() {
		return this.comparator;
	}

	public void setComparator(AbstractDuplicateComparator comparator) {
		this.comparator = comparator;
	}

	public Path getReferenceFile() {
		return this.referenceFile;
	}

	public void setReferenceFile(Path referenceFile) {
		this.referenceFile = referenceFile;
	}

	public String getCode() { return this.code; }

	public void setCode(String code) { this.code = code; }

	public float getWeight() {
		return this.weight;
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
				+ this.similarFile.toString();
		String desc = this.getDescription();
		if (!desc.isEmpty()) {
			output = output + " (" + desc + ")";
		}
		return output;
	}
}

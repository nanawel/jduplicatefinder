package nnwl.jduplicatefinder.engine;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.*;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class CombinedSimilarityResult extends SimilarityResult {

	private static final Logger logger = Logger.getLogger(CombinedSimilarityResult.class);

	public static final String FAKE_COMPARATOR_CODE = "total";

	protected Map<String, SimilarityResult> similarityResults = new HashMap<>();

	protected List<AbstractDuplicateComparator> comparators;

	public CombinedSimilarityResult(Path referenceFile, Path similarFile) {
		this.setReferenceFile(referenceFile);
		this.setSimilarFile(similarFile);
		this.code = FAKE_COMPARATOR_CODE;
		this.resetSimilarity();
	}

	public CombinedSimilarityResult(Path referenceFile, Path similarFile, Collection<SimilarityResult> similarityResults) {
		this(referenceFile, similarFile);
		this.addAllSimilarityResults(similarityResults);
	}

	public CombinedSimilarityResult(SimilarityResult sr) {
		this(sr.getReferenceFile(), sr.getSimilarFile());
		this.addSimilarityResult(sr);
	}

	public void addSimilarityResult(SimilarityResult sr) {
		if (!sr.getReferenceFile().equals(this.referenceFile)) {
			throw new IllegalArgumentException("Similarity result's reference file does not match this reference file");
		}
		if (!sr.getSimilarFile().equals(this.similarFile)) {
			throw new IllegalArgumentException("Similarity result's similar file does not match this similar file");
		}
		this.similarityResults.put(sr.getComparator().getCode(), sr);
		this.resetSimilarity();
	}

	public void addAllSimilarityResults(Collection<SimilarityResult> similarityResults) {
		for (SimilarityResult sr : similarityResults) {
			this.addSimilarityResult(sr);
		}
	}

	protected void resetSimilarity() {
		this.similarity = -1;
	}

	@Override
	public long getSimilarity() {
		if (this.similarity == -1) {
			this.similarity = this.calculateCombinedSimilarity();
		}
		return this.similarity;
	}

	@Override
	public void setSimilarity(int similarity) {
		throw new UnsupportedOperationException("Cannot set similarity on this type of object");
	}

	@Override
	public float getWeight() {
		return 0;
	}

	@Override
	public void setWeight(float weight) {
		throw new UnsupportedOperationException("Cannot set weight on this type of object");
	}

	public void setComparators(List<AbstractDuplicateComparator> comparators) {
		this.comparators = comparators;
	}

	protected int calculateCombinedSimilarity() {
		float similarity = 0;
		float totalWeight = 0;
		if (this.comparators != null) {
			for (AbstractDuplicateComparator comparator : this.comparators) {
				if (this.similarityResults.containsKey(comparator.getCode())) {
					SimilarityResult sr = this.similarityResults.get(comparator.getCode());
					logger.debug(sr);
					similarity += sr.getComparator().getWeight() * sr.getSimilarity();
					totalWeight += sr.getComparator().getWeight();
				} else {
					// Add null score of comparators that did not find any similarity while other did (for proper total score)
					totalWeight += comparator.getWeight();
				}
			}
		} else {
			for (Map.Entry<String, SimilarityResult> entry : this.similarityResults.entrySet()) {
				similarity += entry.getValue().getComparator().getWeight() * entry.getValue().getSimilarity();
				totalWeight += entry.getValue().getComparator().getWeight();
			}
		}
		logger.debug("Calculating combined similarity with: " + similarity + " / " + totalWeight);
		return Math.round(similarity / totalWeight);
	}
}

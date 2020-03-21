package nnwl.jduplicatefinder.engine;

import nnwl.jduplicatefinder.engine.comparators.AbstractDuplicateComparator;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class FileResult {

	private static final Logger logger = Logger.getLogger(FileResult.class);

	public static final int UNIQUE = 0;
	public static final int NOT_UNIQUE = 1;

	protected Path referenceFile;

	protected List<SimilarityResult> similarityResults = new ArrayList<SimilarityResult>();

	protected int status;

	protected List<AbstractDuplicateComparator> currentRunComparators;

	protected Map<String, ? extends SimilarityResult> combinedSimilarityResults;

	public FileResult(Path file) {
		this.referenceFile = file;
	}

	public FileResult(Path file, List<SimilarityResult> similarityResults) {
		this(file);
		this.appendSimilarityResults(similarityResults);
	}

	public Path getReferenceFile() {
		return referenceFile;
	}

	public List<SimilarityResult> getSimilarityResults() {
		return similarityResults;
	}

	public int getStatus() {
		return status;
	}

	public void appendSimilarityResult(SimilarityResult similarResult) {
		this.similarityResults.add(similarResult);
		this.updateStatus();
		this.combinedSimilarityResults = null;
	}

	public void appendSimilarityResults(List<SimilarityResult> similarityResults) {
		this.similarityResults.addAll(similarityResults);
		this.updateStatus();
		this.combinedSimilarityResults = null;
	}

	protected void updateStatus() {
		if (this.similarityResults.isEmpty()) {
			this.status = UNIQUE;
		} else {
			this.status = NOT_UNIQUE;
		}
	}

	public void setCurrentRunComparators(List<AbstractDuplicateComparator> comparators) {
		this.currentRunComparators = comparators;
	}

	public Map<String, ? extends SimilarityResult> getCombinedSimilarityResults() {
		if (this.combinedSimilarityResults == null) {
			Map<String, CombinedSimilarityResult> combinedSimilarityResults = new TreeMap<>();
			for (SimilarityResult sr : this.similarityResults) {
				CombinedSimilarityResult csr;
				if (combinedSimilarityResults.containsKey(sr.getSimilarFile().toString())) {
					csr = combinedSimilarityResults.get(sr.getSimilarFile().toString());
					csr.addSimilarityResult(sr);
				} else {
					csr = new CombinedSimilarityResult(sr);
					csr.setComparators(this.currentRunComparators);
					combinedSimilarityResults.put(csr.getSimilarFile().toString(), csr);
				}
			}
			this.combinedSimilarityResults = combinedSimilarityResults;
		}
		return this.combinedSimilarityResults;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(this.getClass())
			.append(": ")
			.append(this.referenceFile.toString())
			.append(" {\n");
		for (Map.Entry<String, ? extends SimilarityResult> csr : this.getCombinedSimilarityResults().entrySet()) {
			sb.append(String.format("  [%3d] ", csr.getValue().getSimilarity()))
				.append(csr.getValue().getSimilarFile().toString())
				.append("\n");
		}
		sb.append("}");

		return sb.toString();
	}
}

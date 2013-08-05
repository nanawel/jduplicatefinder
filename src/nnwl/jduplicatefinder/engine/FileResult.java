package nnwl.jduplicatefinder.engine;

import java.io.File;
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
public class FileResult
{
	public static final int UNIQUE = 0;
	public static final int NOT_UNIQUE = 1;
	
	public static final String COMPARATOR_CODE = "total";
	
	protected File referenceFile;
	
	protected List<SimilarityResult> similarityResults = new ArrayList<SimilarityResult>();
	
	protected SimilarityResult combinedSimilarityResult;
	
	protected int status;
	
	public FileResult(File file, List<SimilarityResult> similarityResults) {
		this.referenceFile = file;
		this.appendSimilarityResults(similarityResults);
	}
	
	public File getReferenceFile() {
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
	}
	
	public void appendSimilarityResults(List<SimilarityResult> similarityResults) {
		this.similarityResults.addAll(similarityResults);
		this.updateStatus();
	}
	
	protected void updateStatus() {
		if (this.similarityResults.isEmpty()) {
			this.status = UNIQUE;
		}
		else {
			this.status = NOT_UNIQUE;
		}
	}
	
	public Map<String, SimilarityResult> getCombinedSimilarityResults() {
		Map<String, SimilarityResult> combinedSimilarityResults = new TreeMap<String, SimilarityResult>();
		for (SimilarityResult sr : this.similarityResults) {
			SimilarityResult csr;
			if (combinedSimilarityResults.containsKey(sr.getSimilarFile().getPath())) {
				csr = combinedSimilarityResults.get(sr.getSimilarFile().getPath());
			}
			else {
				csr = new SimilarityResult();
				csr.setReferenceFile(this.referenceFile);
				csr.setSimilarFile(sr.getSimilarFile());
				csr.setCode(COMPARATOR_CODE);
				csr.setWeight(0);
				combinedSimilarityResults.put(sr.getSimilarFile().getPath(), csr);
			}
			csr.setSimilarity(Math.round(csr.getSimilarity() + sr.similarity * sr.getComparator().getWeight()));
			csr.setWeight(csr.getWeight() + sr.getComparator().getWeight());
		}

		for (Map.Entry<String, SimilarityResult> csr : combinedSimilarityResults.entrySet()) {
			csr.getValue().setSimilarity(Math.round(csr.getValue().getSimilarity() / csr.getValue().getWeight()));
			csr.getValue().setWeight(1);
		}
		
		return combinedSimilarityResults;
	}
}

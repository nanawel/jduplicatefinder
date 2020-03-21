package nnwl.jduplicatefinder.engine.comparators;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.apache.commons.text.similarity.FuzzyScore;
import org.apache.log4j.Logger;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.ResultsSet;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.engine.comparators.exception.ComparatorException;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class Filename extends AbstractDuplicateComparator {
	private static final Logger logger = Logger.getLogger(Filename.class);

	protected String ignorePattern;
	
	protected float minSimilarity;
	
	protected String searchIn = "filename";

	protected Locale locale;

	protected boolean alphanumericOnly = true;

	protected ArrayList<File> files = new ArrayList<File>();

	protected ArrayList<Filename.CacheUnit> fileCache = new ArrayList<Filename.CacheUnit>();
	
	protected FuzzyScore fuzzyScore;

	public Filename() {
		this.code = "filename";
		this.label = "Filename";
	}

	public void configure(Map<String, Object> parameters) {
		if (parameters.containsKey("filename.ignore_pattern")) {
			this.ignorePattern = String.valueOf(parameters.get("filename.ignore_pattern"));
		}
		if (parameters.containsKey("filename.min_similarity")) {
			this.minSimilarity =
					Float.valueOf(String.valueOf(parameters.get("filename.min_similarity"))) / (float) 100;
			this.minSimilarity = Math.max(0, Math.min(100, this.minSimilarity));
		}
		if (parameters.containsKey("filename.locale")) {
			this.locale = new Locale(String.valueOf(parameters.get("filename.locale")));
		}
		if (parameters.containsKey("filename.alphanumeric_only")) {
			this.alphanumericOnly = Boolean.valueOf(String.valueOf(parameters.get("filename.alphanumeric_only")));
		}
		if (parameters.containsKey("filename.search_in")) {
			this.searchIn = String.valueOf(parameters.get("filename.search_in"));
		}
	}

	@Override
	public void analyze(Path path) {
		File file = path.toFile();
		this.files.add(file);
		this.fileCache.add(new CacheUnit(path));
	}

	@Override
	public void run() {
		try {
			logger.info("Running Filename comparator...");
			this.interrupt = false;

			this.results = new ResultsSet();

			long n = 0;
			for (Filename.CacheUnit currentFile : this.fileCache) {
				if (this.interrupt) {
					throw new InterruptedException();
				}
				this.filesAnalyzeProgressed(this, n, this.fileCache.size());

				ArrayList<SimilarityResult> similarityResults = null;
				for (Filename.CacheUnit comparedFile : this.fileCache) {
					if (comparedFile.path.equals(currentFile.path)) {
						continue;
					}

					SimilarityResult sr = null;
					if (currentFile.fileString.equals(comparedFile.fileString)) {
						sr = new SimilarityResult();
						sr.setReferenceFile(currentFile.path);
						sr.setSimilarFile(comparedFile.path);
						sr.setComparator(this);
						sr.setDescObject("=");
					}
					else {
						int maxScore = this.getMaxScore(currentFile, comparedFile);
						int score = this.compare(currentFile, comparedFile);

						if (logger.isDebugEnabled()) {
							logger.debug(currentFile.path.toAbsolutePath() + " | " + comparedFile.path.toAbsolutePath()
							+ " = " + score + " (max = " + maxScore + ")");
						}
						if (score > ((float) maxScore * this.minSimilarity)) {
							sr = new SimilarityResult();
							sr.setReferenceFile(currentFile.path);
							sr.setSimilarFile(comparedFile.path);
							sr.setSimilarity((int) Math.round((float) score / (float) maxScore * 100));
							sr.setComparator(this);
							sr.setDescObject(score);

						}
						if (sr != null) {
							if (similarityResults == null) {
								similarityResults = new ArrayList<SimilarityResult>();
							}
							similarityResults.add(sr);
						}
					}

					if (similarityResults != null) {
						this.results.put(currentFile.path, new FileResult(currentFile.path, similarityResults));
					}
				}
				n++;
			}
			this.filesAnalyzeProgressed(this, n, this.fileCache.size());
			this.filesAnalyzeCompleted(this, n, this.fileCache.size());

			logger.info("Done");
		} catch (InterruptedException e) {
			logger.info("Interruption requested");
			;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			this.exceptionCaught(this, new ComparatorException(e.getMessage(), e));
		}
	}

	public String applyIgnorePattern(String filename) {
		Pattern p = Pattern.compile(this.ignorePattern, Pattern.CASE_INSENSITIVE);
		return p.matcher(filename).replaceAll("");
	}

	protected int getMaxScore(CacheUnit file, CacheUnit comparedFile) {
		return 100;
	}

	protected int compare(CacheUnit file, CacheUnit comparedFile) {
		String filename = this.prepareFilename(file.fileString);
		String comparedFilename = this.prepareFilename(comparedFile.fileString);

		logger.trace(String.format(
				"Comparing '%s' (from '%s') to '%s' (from '%s')",
				filename,
				file.fileString,
				comparedFilename,
				comparedFile.fileString
		));

		return FuzzySearch.ratio(filename,comparedFilename);
	}

	protected String prepareFilename(String filename) {
		if (this.alphanumericOnly) {
			return filename.replaceAll("[^\\p{L}\\p{Digit}]+", " ");
		}
		return filename;
	}

	public static String[] getAvailableLocales() {
		ArrayList<String> localeStrings = new ArrayList<String>();
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale l : locales) {
			localeStrings.add(l.toLanguageTag());
		}
		String[] result = localeStrings.toArray(new String[localeStrings.size()]);
		java.util.Arrays.sort(result);
		return result;
	}
	
	public static String[] getAvailableSearchInOptions() {
		return new String[]{"filename", "basename", "extension"};
	}

	@Override
	public String getSimilarityResultDescription(SimilarityResult similarityResult) {
		return "fuzzy score: " + similarityResult.getDescObject();
	}

	public static final Attribute<CacheUnit, String> SIZE = new SimpleAttribute<CacheUnit, String>("filename") {
		public String getValue(CacheUnit cu) {
			return cu.fileString;
		}
	};

	public class CacheUnit {
		public Path path;

		public String fileString;

		public CacheUnit(Path path) {
			this.path = path;
			
			String filename = path.getFileName().toString();

			// Filename
			if (Filename.this.searchIn.equals("filename")) {
				this.fileString = filename;
			}
			else {
				String[] tokens = filename.split("\\.(?=[^\\.]+$)");

				// Extension
				if (Filename.this.searchIn.equals("extension")) {
					if (tokens.length > 1) {
						this.fileString = tokens[1];
					}
					else {
						this.fileString = "";
					}
				}
				// Basename
				else {
					this.fileString = tokens[0];
				}
			}
			this.fileString = Filename.this.applyIgnorePattern(this.fileString);

			if (logger.isDebugEnabled()) {
				logger.debug(path + ": " + this.fileString);
			}
		}

		public String getFileString() {
			return this.fileString;
		}
	}
}

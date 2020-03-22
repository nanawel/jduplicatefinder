package nnwl.jduplicatefinder.engine.comparators;

import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.ResultsSet;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.engine.comparators.exception.ComparatorException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static com.googlecode.cqengine.query.QueryFactory.between;
import static com.googlecode.cqengine.query.QueryFactory.equal;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class DateTime extends AbstractDuplicateComparator {
	private static final Logger logger = Logger.getLogger(DateTime.class);

	public static final int DATE_TYPE_CREATED = 1;
	public static final int DATE_TYPE_MODIFIED = 2;

	protected ArrayList<Path> files = new ArrayList<Path>();

//	protected boolean ignoreOneHourDiff = false;

	/**
	 * In seconds
	 */
	protected long timeMargin = 0;

	protected int dateType = DATE_TYPE_MODIFIED;

	protected ArrayList<DateTime.CacheUnit> fileCache = new ArrayList<DateTime.CacheUnit>();

	public DateTime() {
		this.code = "datetime";
		this.label = "Date-Time";
	}

	public void configure(Map<String, Object> parameters) throws NoSuchAlgorithmException {
//		if (parameters.containsKey("datetime.ignore_one_hour_diff")) {
//			this.ignoreOneHourDiff = Boolean.valueOf(String.valueOf(parameters.get("datetime.ignore_one_hour_diff")));
//		}
		if (parameters.containsKey("datetime.time_margin")) {
			this.timeMargin = Long.valueOf(String.valueOf(parameters.get("datetime.time_margin")));
		}
		if (parameters.containsKey("datetime.date_type")) {
			this.dateType = Integer.valueOf(String.valueOf(parameters.get("datetime.date_type")));
		}
	}

	@Override
	public void analyze(Path path) throws IOException {
		this.files.add(path);
		this.fileCache.add(new CacheUnit(path));
	}

	@Override
	public void run() {
		try {
			logger.info("Running DateTime comparator...");
			logger.info("Time margin: " + this.timeMargin + " seconds");
			this.interrupt = false;

			// Prepare indexed collection
			IndexedCollection<CacheUnit> idxSimilarFiles = CQEngine.copyFrom(this.fileCache);
			if (this.timeMargin == 0) {
				idxSimilarFiles.addIndex(HashIndex.onAttribute(TIMESTAMP));
			} else {
				idxSimilarFiles.addIndex(NavigableIndex.onAttribute(TIMESTAMP));
			}

			this.results = new ResultsSet();

			long n = 0;
			for (DateTime.CacheUnit currentFile : this.fileCache) {
				if (this.interrupt) {
					throw new InterruptedException();
				}
				this.filesAnalyzeProgressed(this, n, this.fileCache.size());

				long lowerLimit = Math.max(0, currentFile.getTimestamp() - this.timeMargin);
				long upperLimit = currentFile.getTimestamp() + this.timeMargin;

				logger.debug(currentFile.path.toAbsolutePath() + ": " + lowerLimit + " <= date < " + upperLimit);

				Query<CacheUnit> query;
				if (this.timeMargin == 0) {
					query = equal(TIMESTAMP, currentFile.getTimestamp());
				} else {
					query = between(TIMESTAMP, lowerLimit, upperLimit);
				}

//				if (this.ignoreOneHourDiff) {
//					long oneHourLess = currentFile.getTimestamp() - 3600;
//					long oneHourMore = currentFile.getTimestamp() + 3600;
//					filter.filter("{'getTimestamp': {'$ne': '?1'}}", oneHourLess).filter("{'getTimestamp': {'$ne': '?1'}}", oneHourMore);
//				}

				ArrayList<SimilarityResult> similarityResults = null;
				for (CacheUnit sf : idxSimilarFiles.retrieve(query)) {
					if (this.interrupt) {
						throw new InterruptedException();
					}

					if (sf.path.equals(currentFile.path)) {
						continue;
					}
					//logger.debug("\tFound: " + sf.path.getAbsolutePath() + ": " + sf.timestamp);

					long filetimeDelta = this.timeMargin - Math.abs(currentFile.timestamp - sf.timestamp);

					SimilarityResult sr = new SimilarityResult();
					sr.setReferenceFile(currentFile.path);
					sr.setSimilarFile(sf.path);
					if (this.timeMargin == 0) {
						sr.setSimilarity(100);
					} else {
						sr.setSimilarity(Math.round((float) filetimeDelta / (float) this.timeMargin * 100));
					}
					sr.setComparator(this);
					sr.setDescObject(filetimeDelta);

					if (similarityResults == null) {
						similarityResults = new ArrayList<SimilarityResult>();
					}
					similarityResults.add(sr);
				}
				if (similarityResults != null) {
					this.results.put(currentFile.path, new FileResult(currentFile.path, similarityResults));
				}

				n++;
			}
			this.filesAnalyzeProgressed(this, n, this.fileCache.size());
			this.filesAnalyzeCompleted(this, n, this.fileCache.size());

			logger.info("Done");
		} catch (InterruptedException e) {
			logger.info("Interruption requested");
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			this.exceptionCaught(this, new ComparatorException(e.getMessage(), e));
		}
	}

	@Override
	public String getSimilarityResultDescription(SimilarityResult similarityResult) {
		return similarityResult.getDescObject() + " seconds delta";
	}

	public static final Attribute<CacheUnit, Long> TIMESTAMP = new SimpleAttribute<CacheUnit, Long>("timestamp") {
		public Long getValue(CacheUnit cu) {
			return cu.timestamp;
		}
	};

	public class CacheUnit {
		public Path path;

		public long timestamp;

		public CacheUnit(Path path) throws IOException {
			this.path = path;

			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			if (DateTime.this.dateType == DATE_TYPE_CREATED) {
				this.timestamp = attr.creationTime().toMillis();
			} else if (DateTime.this.dateType == DATE_TYPE_MODIFIED) {
				this.timestamp = attr.lastModifiedTime().toMillis();
			}
			Date date = new Date(this.timestamp);

			//To seconds
			this.timestamp /= 1000;

			logger.debug(path + ": " + this.getTimestamp() + " (" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(date) + ")");
		}

		public long getTimestamp() {
			return this.timestamp;
		}
	}
}

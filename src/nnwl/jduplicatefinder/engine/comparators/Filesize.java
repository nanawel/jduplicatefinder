package nnwl.jduplicatefinder.engine.comparators;

import static com.googlecode.cqengine.query.QueryFactory.between;
import static com.googlecode.cqengine.query.QueryFactory.equal;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.ResultsSet;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.engine.comparators.exception.ComparatorException;
import nnwl.jduplicatefinder.util.Files;

import org.apache.log4j.Logger;

import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;

public class Filesize extends AbstractDuplicateComparator
{
	private static final Logger logger = Logger.getLogger(Filesize.class);

	public static final int MARGIN_TYPE_PERCENTAGE = 1;
	public static final int MARGIN_TYPE_BYTES = 2;

	/**
	 * In percentage or bytes
	 */
	protected long margin = 128 * 1024; // 128KB

	protected int marginType = MARGIN_TYPE_BYTES;

	protected ArrayList<File> files = new ArrayList<File>();

	protected ArrayList<Filesize.CacheUnit> fileCache = new ArrayList<Filesize.CacheUnit>();

	public Filesize() {
		this.code = "filesize";
		this.label = "Filesize";
	}

	public void configure(Map<String, Object> parameters) {
		if (parameters.containsKey("filesize.margin")) {
			this.margin = Long.valueOf(String.valueOf(parameters.get("filesize.margin")));
		}
		if (parameters.containsKey("filesize.margin_type")) {
			this.marginType = Integer.valueOf(String.valueOf(parameters.get("filesize.margin_type")));
		}
	}

	@Override
	public void analyze(File file) {
		if (file.length() == 0) {
			logger.info(file.getPath() + " is empty, skipping.");
			return;
		}
		this.files.add(file);
		this.fileCache.add(new CacheUnit(file));
	}

	@Override
	public void run() {
		try {
			logger.info("Running Filesize comparator...");
			this.interrupt = false;

			// Prepare indexed collection
			IndexedCollection<CacheUnit> idxSimilarFiles = CQEngine.copyFrom(this.fileCache);
			if (this.margin == 0) {
				idxSimilarFiles.addIndex(HashIndex.onAttribute(SIZE));
			}
			else {
				idxSimilarFiles.addIndex(NavigableIndex.onAttribute(SIZE));
			}

			this.results = new ResultsSet();

			long n = 0;
			for (Filesize.CacheUnit currentFile : this.fileCache) {
				if (this.interrupt) {
					throw new InterruptedException();
				}
				this.filesAnalyzeProgressed(this, n, this.fileCache.size());

				long absoluteMargin = 0;
				switch (this.marginType) {
					case MARGIN_TYPE_BYTES:
						absoluteMargin = this.margin;
						break;

					case MARGIN_TYPE_PERCENTAGE:
						absoluteMargin = (long) ((float) currentFile.getSize() * (float) this.margin / 100);
						break;
				}
				long lowerLimit = Math.max(0, currentFile.getSize() - absoluteMargin);
				long upperLimit = currentFile.getSize() + absoluteMargin;

				if (logger.isDebugEnabled()) {
					logger.debug(currentFile.file.getAbsolutePath() + ": " + currentFile.size + " B (margin="
							+ absoluteMargin + " B)");
					logger.debug("lowerLimit=" + lowerLimit + " B | upperLimit=" + upperLimit + " B");
				}

				Query<CacheUnit> query;
				if (this.margin == 0) {
					query = equal(SIZE, currentFile.getSize());
				}
				else {
					query = between(SIZE, lowerLimit, upperLimit);
				}

				ArrayList<SimilarityResult> similarityResults = null;
				for (CacheUnit sf : idxSimilarFiles.retrieve(query)) {
					if (this.interrupt) {
						throw new InterruptedException();
					}

					if (sf.file.equals(currentFile.file)) {
						continue;
					}

					long filesizeDelta = absoluteMargin - Math.abs(currentFile.size - sf.size);

					SimilarityResult sr = new SimilarityResult();
					sr.setReferenceFile(currentFile.file);
					sr.setSimilarFile(sf.file);
					if (absoluteMargin == 0) {
						sr.setSimilarity(100);
					}
					else {
						sr.setSimilarity((int) Math.ceil((float) filesizeDelta / (float) absoluteMargin * 100));
					}
					sr.setComparator(this);
					sr.setDescObject(filesizeDelta);

					if (similarityResults == null) {
						similarityResults = new ArrayList<SimilarityResult>();
					}
					similarityResults.add(sr);
				}
				if (similarityResults != null) {
					this.results.put(currentFile.file, new FileResult(currentFile.file, similarityResults));
				}

				n++;
			}
			this.filesAnalyzeProgressed(this, n, this.fileCache.size());
			this.filesAnalyzeCompleted(this, n, this.fileCache.size());

			logger.info("Done");
		}
		catch (InterruptedException e) {
			logger.info("Interruption requested");
			;
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace();
			this.exceptionCaught(this, new ComparatorException(e.getMessage(), e));
		}
	}

	@Override
	public String getSimilarityResultDescription(SimilarityResult similarityResult) {
		return similarityResult.getDescObject() + " bytes delta";
	}

	public static final Attribute<CacheUnit, Long> SIZE = new SimpleAttribute<CacheUnit, Long>("size") {
		public Long getValue(CacheUnit cu) {
			return cu.size;
		}
	};

	public class CacheUnit
	{
		public File file;

		public long size;

		public CacheUnit(File file) {
			this.file = file;
			this.size = file.length();

			if (logger.isDebugEnabled()) {
				logger.debug(file.getPath() + ": " + this.size + " (" + Files.humanReadableByteCount(this.size, true)
						+ ")");
			}
		}

		public long getSize() {
			return this.size;
		}

	}
}

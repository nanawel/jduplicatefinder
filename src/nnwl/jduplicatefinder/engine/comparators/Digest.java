package nnwl.jduplicatefinder.engine.comparators;

import static com.googlecode.cqengine.query.QueryFactory.equal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.ResultsSet;
import nnwl.jduplicatefinder.engine.SimilarityResult;
import nnwl.jduplicatefinder.engine.comparators.exception.ComparatorException;

import org.apache.log4j.Logger;

import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.query.Query;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class Digest extends AbstractDuplicateComparator
{
	private static final Logger logger = Logger.getLogger(Digest.class);

	protected String algorithm = "MD5";

	/**
	 * Bytes
	 */
	protected long chunksize = 512 * 1024; // 512 KB

	protected ArrayList<File> files = new ArrayList<File>();

	protected Map<Long, List<Digest.CacheUnit>> fileCache = new HashMap<Long, List<Digest.CacheUnit>>();

	public Digest() {
		this.code = "digest";
		this.label = "Digest";
	}

	public void configure(Map<String, Object> parameters) throws NoSuchAlgorithmException {
		if (parameters.containsKey("digest.algorithm")) {
			this.algorithm = String.valueOf(parameters.get("digest.algorithm"));
			MessageDigest.getInstance(this.algorithm);
		}
		if (parameters.containsKey("digest.chunksize")) {
			this.chunksize = Long.valueOf(String.valueOf(parameters.get("digest.chunksize")));
		}
	}

	@Override
	public void analyze(File file) throws IOException, NoSuchAlgorithmException {
		if (file.length() == 0) {
			logger.warn(file.getPath() + " is empty, skipping.");
			return;
		}
		this.files.add(file);
		
		// Put the Digest.CacheUnit in the appropriate cell in the map based on file's size
		long fileSize = file.length();
		List<Digest.CacheUnit> fileCacheList = this.fileCache.get(fileSize);
		
		// Crate map cell if it does not exist
		if (fileCacheList == null) {
			fileCacheList = new ArrayList<Digest.CacheUnit>();
			this.fileCache.put(fileSize, fileCacheList);
		}
		
		fileCacheList.add(new CacheUnit(file, this.chunksize));
	}

	@Override
	public void run() {
		try {
			logger.info("Running Digest comparator...");
			this.interrupt = false;
			
			this.results = new ResultsSet();
	
			long n = 0;
			for (Map.Entry<Long, List<Digest.CacheUnit>> filesBySize : this.fileCache.entrySet()) {
				if (this.interrupt) {
					throw new InterruptedException();
				}
				this.filesAnalyzeProgressed(this, n, this.files.size());
				
				// Prepare indexed collection
				IndexedCollection<CacheUnit> idxSameSizeFiles = CQEngine.copyFrom(filesBySize.getValue());
				idxSameSizeFiles.addIndex(HashIndex.onAttribute(DIGEST));
				
				for (Digest.CacheUnit currentFile : filesBySize.getValue()) {
					if (this.interrupt) {
						throw new InterruptedException();
					}
					
					Query<CacheUnit> query = equal(DIGEST, currentFile.digest);
	
					ArrayList<SimilarityResult> similarityResults = null;
					for (CacheUnit sf : idxSameSizeFiles.retrieve(query)) {
						if (this.interrupt) {
							throw new InterruptedException();
						}
						
						if (sf.file.equals(currentFile.file)) {
							continue;
						}
						SimilarityResult sr = new SimilarityResult();
						sr.setReferenceFile(currentFile.file);
						sr.setSimilarFile(sf.file);
						sr.setSimilarity(100);
						sr.setComparator(this);
						sr.setDescObject(currentFile.digest);
						
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
			}
			this.filesAnalyzeProgressed(this, n, this.files.size());
			this.filesAnalyzeCompleted(this, n, this.fileCache.size());
			
			logger.info("Done");
		}
		catch (InterruptedException e) {
			logger.info("Interruption requested");
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace();
			this.exceptionCaught(this, new ComparatorException(e.getMessage(), e));
		}
	}

	@Override
	public String getSimilarityResultDescription(SimilarityResult similarityResult) {
		return "Digest: " + similarityResult.getDescObject();
	}

	public static String[] getAvailableAlgorithms() {
		ArrayList<String> algos = new ArrayList<String>();
		Provider[] providers = Security.getProviders();
		for (Provider p : providers) {
			Set<Service> services = p.getServices();
			for (Service s : services) {
				if ("MessageDigest".equals(s.getType())) {
					algos.add(s.getAlgorithm());
				}
			}
		}
		return (String[]) algos.toArray(new String[algos.size()]);
	}

	public static String getHexString(byte[] digest) {
		BigInteger bigInt = new BigInteger(1, digest);
		return bigInt.toString(16);
	}
	
	public static final Attribute<CacheUnit, String> DIGEST = new SimpleAttribute<CacheUnit, String>("digest") {
		public String getValue(CacheUnit cu) {
			return cu.digest;
		}
	};

	public class CacheUnit
	{
		public File file;

		public String digest;

		/**
		 * Chunk size (in bytes)
		 */
		private int chunkSize = 16 * 1024;

		public CacheUnit(File file, long dataSize) throws NoSuchAlgorithmException, IOException {
			this.file = file;
			if (dataSize == 0) {
				dataSize = file.length();
			}

			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(file);
			long totalRead = 0;
			byte[] buffer, remainderBuffer = null;
			int bufferSize, loops;
			if (dataSize < this.chunkSize) {
				bufferSize = (int) dataSize;
				loops = 1;
			}
			else {
				bufferSize = this.chunkSize;
				loops = (int) (dataSize / this.chunkSize);
				remainderBuffer = new byte[(int) dataSize % this.chunkSize];
			}
			buffer = new byte[bufferSize];
			try {
				is = new DigestInputStream(is, md);
				int numRead;
				for (; loops > 0; loops--) {
					numRead = is.read(buffer);
					totalRead += numRead;
					if (numRead < bufferSize) {
						break;
					}
				}
				if (remainderBuffer != null) {
					totalRead += is.read(remainderBuffer);

				}
			}
			finally {
				is.close();
			}
			this.digest = Digest.getHexString(md.digest());

			logger.debug(file.getPath() + ": " + this.getDigest());
			logger.debug("Total bytes read: " + totalRead);
		}

		public String getDigest() {
			return this.digest;
		}
	}
}

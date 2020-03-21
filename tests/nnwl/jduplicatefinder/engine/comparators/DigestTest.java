package nnwl.jduplicatefinder.engine.comparators;

import junit.framework.TestCase;
import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.ResultsSet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class DigestTest extends TestCase {

	public static final String TEST_FOLDER_PATH = "jduplicatefinder-tests";

	private Digest fixture;

	private Path tempDir;

	public void setUp() throws IOException  {
		this.fixture = new Digest();
		this.tempDir = Files.createTempDirectory(TEST_FOLDER_PATH);
	}

	public void tearDown() throws IOException  {
		/** @src http://stackoverflow.com/a/27917071 */
		Files.walkFileTree(this.tempDir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private File createFixedSizeFile(String name, int size, char charContent) throws IOException {
		File file = this.tempDir.resolve(name).toFile();

		char[] buffer = new char[size];
		for (int i = 0; i < size; i++) {
			buffer[i] = charContent;
		}
		FileWriter fw = new FileWriter(file);
		fw.write(buffer);
		fw.close();

		return file;
	}

	public void testRun() throws IOException, NoSuchAlgorithmException {
		Map<String, Object> config = new HashMap<>();
		config.put("digest.algorithm", "MD5");
		config.put("digest.chunkSize", String.valueOf(32));
		this.fixture.configure(config);

		// First 2 files should be found equal according to given configuration
		// File #3 has different size
		// File #4 has different content
		File f1 = this.createFixedSizeFile("32b-1", 32, '#');
		File f2 = this.createFixedSizeFile("32b-2", 32, '#');
		File f3 = this.createFixedSizeFile("64b-1", 64, '#');
		File f4 = this.createFixedSizeFile("64b-2", 64, '@');

		this.fixture.analyze(f1.toPath());
		this.fixture.analyze(f2.toPath());
		this.fixture.analyze(f3.toPath());
		this.fixture.analyze(f4.toPath());

		this.fixture.run();
		ResultsSet results = this.fixture.getResults();

		this.assertEquals(1, results.get(f1.toPath()).getSimilarityResults().size());
		this.assertEquals(1, results.get(f2.toPath()).getSimilarityResults().size());
		this.assertNull(results.get(f3.toPath()));
		this.assertNull(results.get(f4.toPath()));

		for (Map.Entry<Path, FileResult> res : results.entrySet()) {
			// f1 is the reference file
			if (res.getKey() == f1.toPath()) {
				this.assertEquals(f1.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f2.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());
				this.assertEquals(100, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
			// f2 is the reference file
			else if (res.getKey() == f2.toPath()) {
				this.assertEquals(f2.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f1.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());
				this.assertEquals(100, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
			// f3 is the reference file
			else if (res.getKey() == f3.toPath()) {
				this.assertEquals(f3.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(FileResult.UNIQUE, res.getValue().getStatus());
			}
			// f4 is the reference file
			else if (res.getKey() == f4.toPath()) {
				this.assertEquals(f4.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(FileResult.UNIQUE, res.getValue().getStatus());
			}
		}
	}
}

package nnwl.jduplicatefinder.engine.comparators;

import junit.framework.TestCase;
import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.ResultsSet;
import nnwl.jduplicatefinder.engine.comparators.Filesize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class FilesizeTest extends TestCase {

	public static final String TEST_FOLDER_PATH = "jduplicatefinder-tests";

	private Filesize fixture;

	private Path tempDir;

	public void setUp() throws IOException  {
		this.fixture = new Filesize();
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

	private File createFixedSizeFile(String name, int size) throws IOException {
		File file = this.tempDir.resolve(name).toFile();

		char[] buffer = new char[size];
		FileWriter fw = new FileWriter(file);
		fw.write(buffer);
		fw.close();

		return file;
	}

	public void testRunBytesNullMarginEqualFiles() throws IOException {
		Map<String, Object> config = new HashMap<>();
		config.put("filesize.margin", String.valueOf(0));
		config.put("filesize.margin_type", String.valueOf(Filesize.MARGIN_TYPE_BYTES));
		this.fixture.configure(config);

		File f1 = this.createFixedSizeFile("32b-1", 32);
		File f2 = this.createFixedSizeFile("32b-2", 32);

		this.fixture.analyze(f1.toPath());
		this.fixture.analyze(f2.toPath());

		this.fixture.run();
		ResultsSet results = this.fixture.getResults();

		for (Map.Entry<Path, FileResult> res : results.entrySet()) {
			// f1 is the reference file
			if (res.getKey() == f1.toPath()) {
				this.assertEquals(f1.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f2.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());
				this.assertEquals(100, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
			// f2 is the reference file
			else {
				this.assertEquals(f2.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f1.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());
				this.assertEquals(100, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
		}
	}

	public void testRunBytesNonNullMarginEqualFiles() throws IOException {
		int margin = 10;

		Map<String, Object> config = new HashMap<>();
		config.put("filesize.margin", String.valueOf(margin));
		config.put("filesize.margin_type", String.valueOf(Filesize.MARGIN_TYPE_BYTES));
		this.fixture.configure(config);

		File f1 = this.createFixedSizeFile("32b-1", 32);
		File f2 = this.createFixedSizeFile("32b-2", 32);

		this.fixture.analyze(f1.toPath());
		this.fixture.analyze(f2.toPath());

		this.fixture.run();
		ResultsSet results = this.fixture.getResults();

		for (Map.Entry<Path, FileResult> res : results.entrySet()) {
			// f1 is the reference file
			if (res.getKey() == f1.toPath()) {
				this.assertEquals(f1.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f2.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());
				this.assertEquals(100, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
			// f2 is the reference file
			else {
				this.assertEquals(f2.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f1.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());
				this.assertEquals(100, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
		}
	}

	public void testRunBytesNonNullMarginDifferentFiles() throws IOException {
		int margin = 10;

		Map<String, Object> config = new HashMap<>();
		config.put("filesize.margin", String.valueOf(margin));
		config.put("filesize.margin_type", String.valueOf(Filesize.MARGIN_TYPE_BYTES));
		this.fixture.configure(config);

		File f1 = this.createFixedSizeFile("28b-1", 28);
		File f2 = this.createFixedSizeFile("32b-2", 32);

		this.fixture.analyze(f1.toPath());
		this.fixture.analyze(f2.toPath());

		this.fixture.run();
		ResultsSet results = this.fixture.getResults();

		for (Map.Entry<Path, FileResult> res : results.entrySet()) {
			// f1 is the reference file
			if (res.getKey() == f1.toPath()) {
				this.assertEquals(f1.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f2.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());

				// 4 bytes delta on a 10 bytes margin => 60% similarity
				this.assertEquals(60, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
			// f2 is the reference file
			else {
				this.assertEquals(f2.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f1.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());

				// 4 bytes delta on a 10 bytes margin => 60% similarity
				this.assertEquals(60, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
		}
	}

	public void testRunPercentNonNullMarginDifferentFiles() throws IOException {
		int margin = 50;

		Map<String, Object> config = new HashMap<>();
		config.put("filesize.margin", String.valueOf(margin));
		config.put("filesize.margin_type", String.valueOf(Filesize.MARGIN_TYPE_PERCENTAGE));
		this.fixture.configure(config);

		File f1 = this.createFixedSizeFile("32b-1", 32);
		File f2 = this.createFixedSizeFile("48b-2", 48);
		File f3 = this.createFixedSizeFile("128b-1", 128);

		this.fixture.analyze(f1.toPath());
		this.fixture.analyze(f2.toPath());
		this.fixture.analyze(f3.toPath());

		this.fixture.run();
		ResultsSet results = this.fixture.getResults();

		for (Map.Entry<Path, FileResult> res : results.entrySet()) {
			// f1 is the reference file
			if (res.getKey() == f1.toPath()) {
				this.assertEquals(f1.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f2.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());

				// 50% bytes delta on 32 = 16 bytes, so 16 bytes delta on a 16 bytes margin => 0% similarity (lower limit)
				this.assertEquals(0, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
			// f2 is the reference file
			else if (res.getKey() == f2.toPath()) {
				this.assertEquals(f2.toPath(), res.getValue().getReferenceFile());
				this.assertEquals(f1.toPath(), res.getValue().getSimilarityResults().get(0).getSimilarFile());

				// 50% bytes delta on 48 = 24 bytes, so 16 bytes delta on a 24 bytes margin => 33% similarity
				this.assertEquals(33, res.getValue().getSimilarityResults().get(0).getSimilarity());
				this.assertEquals(FileResult.NOT_UNIQUE, res.getValue().getStatus());
			}
			// f3 is the reference file
			else if (res.getKey() == f3.toPath()) {
				this.assertEquals(f3.toPath(), res.getValue().getReferenceFile());
				this.assertNull(res.getValue().getSimilarityResults());
				this.assertEquals(FileResult.UNIQUE, res.getValue().getStatus());
			}
		}
	}
}

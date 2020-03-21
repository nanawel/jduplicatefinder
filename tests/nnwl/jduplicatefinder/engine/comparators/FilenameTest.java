package nnwl.jduplicatefinder.engine.comparators;

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

import org.apache.commons.text.similarity.FuzzyScore;

import junit.framework.TestCase;
import nnwl.jduplicatefinder.engine.FileResult;
import nnwl.jduplicatefinder.engine.ResultsSet;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class FilenameTest extends TestCase {

	public static final String TEST_FOLDER_PATH = "jduplicatefinder-tests";

	private Filename fixture;

	private Path tempDir;

	public void setUp() throws IOException  {
		this.fixture = new Filename();
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

	private File createFile(String name) throws IOException {
		File file = this.tempDir.resolve(name).toFile();

		char[] buffer = new char[0];
		FileWriter fw = new FileWriter(file);
		fw.write(buffer);
		fw.close();

		return file;
	}
	
//	public void testFuzzyScore() throws IOException {
//		Map<String, Object> config = new HashMap<>();
//		config.put("filename.ignore_pattern", String.valueOf(""));
//		config.put("filename.min_similarity", String.valueOf(50));
//		config.put("filename.locale", String.valueOf("fr"));
//		this.fixture.configure(config);
//
//		FuzzyScore fuzzyScore = this.fixture.getFuzzyScore();
//
//		assertEquals(7, fuzzyScore.fuzzyScore("foo", "foo").intValue());
//		assertEquals(4, fuzzyScore.fuzzyScore("bar", "baz").intValue());
//		assertEquals(1, fuzzyScore.fuzzyScore("foo", "fz").intValue());
//		assertEquals(0, fuzzyScore.fuzzyScore("foo", "baz").intValue());
//		assertEquals(7, fuzzyScore.fuzzyScore("foo", "foo bar baz").intValue());
//		assertEquals(7, fuzzyScore.fuzzyScore("bar foo baz", "foo").intValue());
//		//assertEquals(7, fuzzyScore.fuzzyScore("foo", "bar foo baz").intValue());
//		assertEquals(16, fuzzyScore.fuzzyScore("azerty", "azerty").intValue());
//		assertEquals(28, fuzzyScore.fuzzyScore("samesize01", "samesize01").intValue());
//		assertEquals(21, fuzzyScore.fuzzyScore("samesize01", "samesize02").intValue());
//	}

	public void testFilenameScores() throws IOException {
		Map<String, Object> config = new HashMap<>();
		config.put("filename.ignore_pattern", String.valueOf(""));
		config.put("filename.min_similarity", String.valueOf(50));
		config.put("filename.locale", String.valueOf("fr"));
		this.fixture.configure(config);

		File f1 = this.createFile("foo");
		File f2 = this.createFile("bar");
		File f3 = this.createFile("baz");
		File f4 = this.createFile("lorem ipsum dolor sit amet");
		File f5 = this.createFile("lorem ipsum dolor sit");

		this.fixture.analyze(f1.toPath());
		this.fixture.analyze(f2.toPath());
		this.fixture.analyze(f3.toPath());
		this.fixture.analyze(f4.toPath());
		this.fixture.analyze(f5.toPath());

		File[] files = new File[] {
			this.createFile("foo"),
			this.createFile("bar"),
			this.createFile("baz"),
			this.createFile("lorem ipsum dolor sit amet"),
			this.createFile("lorem ipsum dolor sit")
		};

		for (File f : files) {
			this.fixture.analyze(f.toPath());
		}

		this.fixture.run();
		ResultsSet results = this.fixture.getResults();

		for (Map.Entry<Path, FileResult> res : results.entrySet()) {
			System.out.println("testFilenameScores : " + res.getValue().toString());
		}
	}

	public void testFilenameScores2() throws IOException {
		Map<String, Object> config = new HashMap<>();
		config.put("filename.ignore_pattern", String.valueOf(" *(Arte|France.\\d|\\d{4}[-_]\\d{2}[-_]\\d{2}(.\\d{2}[-_]\\d{2})?) *"));
		config.put("filename.min_similarity", String.valueOf(20));
		config.put("filename.locale", String.valueOf("fr"));
		this.fixture.configure(config);

		File[] files = new File[] {
			this.createFile("Caramel, une note salée_France 5_2018_03_18_20_55.mpg"),
			this.createFile("Le caramel, une note salée_France 5_2019_09_29_21_47.mpg"),
			this.createFile("Cash investigation_Industrie_du_tabac_France 2_2014_10_07_20_50.mpg"),
			this.createFile("Cash investigation - Industrie agro-alimentaire business contre santé_France 2_2016_09_13_20_55.mpg"),
			this.createFile("Grande distribution_France 5_2014_03_16_20_42.mp4"),
			this.createFile("garbas_ NixOS - Reproducible Linux Distribution built around systemd.mp4"),
			this.createFile("La guerre des graines_France 5_2014_05_27_21_32.mp4"),
			this.createFile("La grande guerre, les tunnels de la mort (2-2) - L'effondrement des sommets_Arte_2017_11_11_21_44.mp4")
		};

		for (File f : files) {
			this.fixture.analyze(f.toPath());
		}

		this.fixture.run();
		ResultsSet results = this.fixture.getResults();

		for (Map.Entry<Path, FileResult> res : results.entrySet()) {
			System.out.println("testFilenameScores2 : " + res.getValue().toString());
		}
	}

	public void testApplyIgnorePattern() throws IOException {
		Map<String, Object> config = new HashMap<>();
		config.put("filename.ignore_pattern", String.valueOf(" *(abc|d.f|\\d{4}-\\d{2}-\\d{2}) *"));
		this.fixture.configure(config);

		assertEquals("g", this.fixture.applyIgnorePattern("abcdefg"));
		assertEquals("azerty", this.fixture.applyIgnorePattern("azerty"));
		assertEquals("myfile", this.fixture.applyIgnorePattern("myfile 2016-08-29"));
	}
}

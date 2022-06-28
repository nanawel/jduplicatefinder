package nnwl.jduplicatefinder.engine;

import junit.framework.TestCase;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by anael on 15/07/16.
 */
public class FileFilterTest extends TestCase {

	private FileFilter fixture;

	public void setUp() {
		this.fixture = new FileFilter();
	}

	private Path getFakePath(String path) {
		return FileSystems.getDefault().getPath(path);
	}

	public void testMatchesFilenameSimple() {
		this.fixture.setType(FileFilter.TYPE_SIMPLE);
		this.fixture.setPattern("*.avi");
		this.fixture.setMatches(FileFilter.MATCH_FILENAME);
		this.fixture.setPolicy(FileFilter.POLICY_INCLUDE);

		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder.avi/myfile.mkv")));
		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder.avi/myfile.avi.srt")));
		this.assertTrue(this.fixture.shouldInclude(this.getFakePath("folder/subfolder/myfile.avi")));
	}

	public void testMatchesPathSimple() {
		this.fixture.setType(FileFilter.TYPE_SIMPLE);
		this.fixture.setPattern("*.avi");
		this.fixture.setMatches(FileFilter.MATCH_PATH);
		this.fixture.setPolicy(FileFilter.POLICY_INCLUDE);

		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder/myfile.mpg")));
		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder.avi/myfile.mkv")));
		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder.avi/myfile.avi.srt")));
		this.assertTrue(this.fixture.shouldInclude(this.getFakePath("folder/subfolder/myfile.avi")));

		this.fixture.setPattern("*.avi*");
		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder/myfile.mpg")));
		this.assertTrue(this.fixture.shouldInclude(this.getFakePath("folder/subfolder.avi/myfile.mkv")));
		this.assertTrue(this.fixture.shouldInclude(this.getFakePath("folder/subfolder/myfile.avi.srt")));
		this.assertTrue(this.fixture.shouldInclude(this.getFakePath("folder/subfolder/myfile.avi")));
	}

	public void testMatchesFilenameRegexp() {
		this.fixture.setType(FileFilter.TYPE_REGEXP);
		this.fixture.setPattern("\\.(avi|mpg|divx|mp4|mkv|wmv|flv|vob|ogv|mov|rm)$");
		this.fixture.setMatches(FileFilter.MATCH_FILENAME);
		this.fixture.setPolicy(FileFilter.POLICY_INCLUDE);

		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder.avi/myfile.txt")));
		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder.avi/myfile.avi.srt")));
		this.assertTrue(this.fixture.shouldInclude(this.getFakePath("folder/subfolder/myfile.avi")));
		this.assertTrue(this.fixture.shouldInclude(this.getFakePath("folder/subfolder/myfile.mp4")));
	}

	public void testMatchesPathRegexp() {
		this.fixture.setType(FileFilter.TYPE_REGEXP);
		this.fixture.setPattern(".*/\\..*/.*$");
		this.fixture.setMatches(FileFilter.MATCH_PATH);
		this.fixture.setPolicy(FileFilter.POLICY_INCLUDE);

		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder.avi/myfile.txt")));
		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder.avi")));
		this.assertFalse(this.fixture.shouldInclude(this.getFakePath("folder/subfolder/.hiddenFile.mp4")));
		this.assertTrue(this.fixture.shouldInclude(this.getFakePath("folder/.hiddenSubfolder/myfile.avi")));
		this.assertTrue(this.fixture.shouldInclude(this.getFakePath("folder/.hiddenSubfolder/dir/myfile.avi")));
	}
}

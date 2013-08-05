package nnwl.jduplicatefinder.engine;

import java.io.File;
import java.util.regex.Pattern;

/**
 * JDuplicateFinder
 *  
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class FileFilter
{
	public static final String MATCH_FILENAME = "Filename";
	public static final String MATCH_PATH = "Path";
	public static final String[] MATCH_CHOICES = { MATCH_FILENAME, MATCH_PATH };

	public static final String TYPE_SIMPLE = "Simple";
	public static final String TYPE_REGEXP = "Regexp";
	public static final String[] TYPE_CHOICES = { TYPE_SIMPLE, TYPE_REGEXP };

	protected String userPattern;

	protected String matches;

	protected String type;

	protected Pattern pattern;

	public FileFilter() {
		this("", MATCH_FILENAME, TYPE_SIMPLE);
	}

	public FileFilter(String pattern, String matches, String type) {
		this.userPattern = pattern;
		this.matches = matches;
		this.type = type;

		this.prepareFilter();
	}

	protected void prepareFilter() {
		String finalPattern;
		switch (this.type) {
			case TYPE_SIMPLE:
				finalPattern = "^" + escapeSimplePattern(this.userPattern) + "$";
				break;
			case TYPE_REGEXP:
				finalPattern = userPattern;
				break;
			default:
				throw new IllegalArgumentException(this.type + " is not a valid type.");
		}

		this.pattern = Pattern.compile(finalPattern);
	}
	
	protected static String escapeSimplePattern(String p) {
		String[] parts = p.split("\\*", -1);
		
		StringBuffer sb = new StringBuffer();
		for (String part : parts) {
			sb.append(Pattern.quote(part)).append(".*");
		}
		return sb.toString();
	}

	public boolean matches(File f) {
		String subject;
		switch (this.matches) {
			case MATCH_FILENAME:
				subject = f.getName();
				break;
			case MATCH_PATH:
				subject = f.getAbsolutePath();
				break;
			default:
				throw new IllegalArgumentException(this.matches + " is not a valid value for matches.");
		}
		return this.pattern.matcher(subject).matches();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(FileFilter.class.toString());
		sb.append(" [Pattern=\"").append(this.userPattern).append("\", Matches=").append(this.matches)
				.append(", Type=").append(this.type).append("]");
		return sb.toString();
	}
}

package nnwl.jduplicatefinder.engine;

import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * JDuplicateFinder
 *
 * @author Anael Ollier <nanawel NOSPAM [at] gmail [dot] com>
 * @license GPLv3 - See LICENSE
 */
public class FileFilter {

	private static final Logger logger = Logger.getLogger(FileFilter.class);

	public static final String MATCH_FILENAME = "Filename";
	public static final String MATCH_PATH = "Path";
	public static final String[] MATCH_CHOICES = {MATCH_FILENAME, MATCH_PATH};

	public static final String TYPE_SIMPLE = "Simple";
	public static final String TYPE_REGEXP = "Regexp";
	public static final String[] TYPE_CHOICES = {TYPE_SIMPLE, TYPE_REGEXP};
	public static final String[] TYPE_TOOLTIPS = {
			"<html>Literal filter, but you can use \"*\" as a wildcard.<br/>Ex: \"*.jpg\" will find all filenames ending in \".jpg\"</html>",
			"Classic regexp"};

	protected String title;

	protected String userPattern;

	protected String matches;

	protected String type;

	protected Pattern pattern;

	public FileFilter() {
		this("", MATCH_FILENAME, TYPE_SIMPLE);
	}

	public FileFilter(String pattern, String matches, String type) {
		this.setType(type);
		this.setPattern(pattern);
		this.setMatches(matches);
	}

	public Pattern compilePattern() {
		String finalPattern;
		switch (this.type) {
			case TYPE_SIMPLE:
				finalPattern = "^" + escapeSimplePattern(this.userPattern) + "$";
				break;
			case TYPE_REGEXP:
				finalPattern = userPattern;
				break;
			default:
				throw new IllegalArgumentException(this.type + " is not a valid type of filter.");
		}
		return Pattern.compile(finalPattern);
	}

	protected static String escapeSimplePattern(String p) {
		String[] parts = p.split("\\*", -1);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			sb.append(Pattern.quote(part));
			if (i < parts.length - 1) {
				sb.append(".*");
			}
		}
		return sb.toString();
	}

	public boolean matches(Path p) {
		String subject;
		switch (this.matches) {
			case MATCH_FILENAME:
				subject = p.getFileName().toString();
				break;
			case MATCH_PATH:
				subject = p.toString();
				break;
			default:
				throw new IllegalArgumentException(this.matches + " is not a valid value for matches.");
		}
		boolean matches = this.getRegexpPattern().matcher(subject).find();
		if (logger.isDebugEnabled()) {
			if (matches) {
				logger.debug(this.toString() + " MATCHES " + subject);
			} else {
				logger.debug(this.toString() + " does NOT MATCH " + subject);
			}
		}
		return matches;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPattern() {
		return userPattern;
	}

	public void setPattern(String pattern) {
		this.userPattern = pattern;
		this.pattern = null;
	}

	protected Pattern getRegexpPattern() {
		if (this.pattern == null) {
			this.pattern = this.compilePattern();
		}
		return this.pattern;
	}

	public String getMatches() {
		return matches;
	}

	public void setMatches(String matches) {
		this.matches = matches;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(FileFilter.class.toString());
		sb.append(" [Pattern=\"").append(this.userPattern).append("\", Matches=").append(this.matches)
				.append(", Type=").append(this.type).append("]");
		return sb.toString();
	}
}

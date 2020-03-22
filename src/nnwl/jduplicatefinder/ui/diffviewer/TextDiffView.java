package nnwl.jduplicatefinder.ui.diffviewer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;

class TextDiffView implements FileDiffView {
	public static final String[] SUPPORTED_MIMETYPES = {
			"text/css",
			"text/csv",
			"text/html",
			"text/javascript",
			"application/javascript",
			"text/plain",
			"text/xml"};

	@Override
	public boolean canHandle(File file) throws IOException {
		String mime = java.nio.file.Files.probeContentType(file.toPath());
		for (String m : SUPPORTED_MIMETYPES) {
			if (m.equals(mime)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public JComponent getViewForFile(File file) throws IOException {
		JTextArea ta = new JTextArea();
		ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		//TODO Detect charset and line separator
		ta.setText(this.getFileContent(file));

		return new JScrollPane(ta);
	}

	protected Charset detectCharset(File file) {
		return Charset.defaultCharset();
	}

	protected String getFileContent(File file) throws IOException {
		BufferedReader bufferIn = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		try {
			StringBuffer out = new StringBuffer();
			String line;
			while ((line = bufferIn.readLine()) != null) {
				out.append(line).append(System.getProperty("line.separator"));
			}
			return out.toString();
		} finally {
			bufferIn.close();
		}
	}
}
package nnwl.jduplicatefinder.ui.diffviewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class ImageDiffView implements FileDiffView {
	public static final String[] SUPPORTED_MIMETYPES = {"image/gif", "image/jpeg", "image/png", "image/bmp",
			// "image/tiff",
			// "image/vnd.microsoft.icon"
	};

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
		return new BackgroundPanel(ImageIO.read(file));
	}

	@SuppressWarnings("serial")
	private class BackgroundPanel extends JPanel {
		private Image image;

		public BackgroundPanel(Image image) {
			super();
			this.image = image;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Dimension scaledDim = this.getScaledImageDimension();
			int padLeft = (int) Math.floor((this.getWidth() - scaledDim.width) / 2);
			int padTop = (int) Math.floor((this.getHeight() - scaledDim.height) / 2);
			g.drawImage(image, padLeft, padTop, scaledDim.width, scaledDim.height, null);
		}

		private Dimension getScaledImageDimension() {
			Dimension d = new Dimension();
			int imageWidth = image.getWidth(null);
			int imageHeight = image.getHeight(null);

			// System.out.println("CONTAINER: W=" + this.getWidth() + " | H=" + this.getHeight() + " | R=" + ((float)
			// this.getWidth() / (float) this.getHeight()));
			// System.out.println("IMAGE: W=" + imageWidth + " | H=" + imageHeight + " | R=" + ((float) imageWidth /
			// (float) imageHeight));

			if ((float) this.getWidth() / (float) this.getHeight() < (float) imageWidth / (float) imageHeight) {
				d.height = this.getWidth() * imageHeight / imageWidth;
				d.width = this.getWidth();
			} else {
				d.height = this.getHeight();
				d.width = this.getHeight() * imageWidth / imageHeight;
			}
			return d;
		}
	}
}
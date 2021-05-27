package au.com.highlowgame.service;

import static org.imgscalr.Scalr.OP_ANTIALIAS;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.imgscalr.Scalr;

import au.com.highlowgame.util.SSUtil;

public class ImageServiceImpl implements ImageService {

	@Override
	public boolean resize(Image image, int targetWidth, int targetHeight) {
		return resize(image, null, null, targetWidth, targetHeight);
	}

	@Override
	public Image createThumbnail(int targetWidth, int targetHeight, byte[] thumbnailContent, String thumbnailContentType) {
		Image thumbnailImage = new Image();
		thumbnailImage.setContent(thumbnailContent);
		thumbnailImage.setContentType(thumbnailContentType);

		resize(thumbnailImage, Method.SPEED, Mode.AUTOMATIC, targetWidth, targetHeight);

		return thumbnailImage;
	}

	@Override
	public boolean resize(Image image, Method method, Mode mode, int targetWidth, int targetHeight) {

		if (method == null)
			method = Method.QUALITY;

		if (mode == null)
			mode = Mode.AUTOMATIC;

		Scalr.Method scalrMethod = Scalr.Method.valueOf(method.name());
		Scalr.Mode scalrMode = Scalr.Mode.valueOf(mode.name());

		BufferedImage buffImage = null;
		BufferedImage resizedImage = null;
		ByteArrayOutputStream baos = null;
		InputStream in = null;
		try {

			String format = getFormatName(image);

			if (SSUtil.empty(format))
				return false;

			in = new ByteArrayInputStream(image.getContent());
			try {
				buffImage = ImageIO.read(in);
			} catch (Exception e) {
				ImageInputStream imageInput = new MemoryCacheImageInputStream(in);
				Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInput);
				while (imageReaders.hasNext()) {
					ImageReader reader = imageReaders.next();
					try {
						reader.setInput(imageInput);
						buffImage = reader.read(0);
						break;
					} catch (IOException e1) {
					}
				}
				imageInput.close();

				if (buffImage == null)
					return false;
			}

			// Create, then smooth it.
			resizedImage = Scalr.resize(buffImage, scalrMethod, scalrMode, targetWidth, targetHeight, OP_ANTIALIAS);

			// Add add a little border
			// resizedImage = pad(resizedImage, 1);

			buffImage.flush();
			resizedImage.flush();

			// convert BufferedImage to byte array
			baos = new ByteArrayOutputStream();
			boolean written = ImageIO.write(resizedImage, format, baos);
			if (written == false)
				return false;

			baos.flush();
			image.setContent(baos.toByteArray());
			image.setSize(new Long(image.getContent().length));

			return true;

		} catch (Exception e) {
		} finally {
			if (buffImage != null)
				buffImage.flush();
			if (resizedImage != null)
				resizedImage.flush();
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return false;

	}

	protected String getFormatName(Image image) {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(image.getContent());
			return SSUtil.getFileFormatName(in);
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return null;

	}

}

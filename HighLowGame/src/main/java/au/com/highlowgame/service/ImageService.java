package au.com.highlowgame.service;

import org.springframework.stereotype.Service;

@Service("imageService")
public interface ImageService {

	public static enum Method {
		AUTOMATIC,
		SPEED,
		BALANCED,
		QUALITY,
		ULTRA_QUALITY;
	}

	public static enum Mode {
		AUTOMATIC,
		FIT_EXACT,
		FIT_TO_WIDTH,
		FIT_TO_HEIGHT;
	}

	public boolean resize(Image image, int targetWidth, int targetHeight);

	public boolean resize(Image image, Method method, Mode mode, int targetWidth, int targetHeight);

	public Image createThumbnail(int targetWidth, int targetHeight, byte[] thumbnailContent, String thumbnailContentType);

}

package au.com.highlowgame.service;

import au.com.highlowgame.util.ApplicationContextProvider;

public class Image {

	protected byte[] content;
	protected String contentType;
	protected Long size;

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public byte[] getThumbnailContent() {
		return getImageService().createThumbnail(72, 72, getContent(), getContentType()).getContent();
	}

	public byte[] getMediumThumbnailContent() {
		return getImageService().createThumbnail(200, 200, getContent(), getContentType()).getContent();
	}

	public byte[] getMediumSizeContent() {
		return getImageService().createThumbnail(400, 400, getContent(), getContentType()).getContent();
	}

	protected ImageService getImageService() {
		return ApplicationContextProvider.getApplicationContext().getBean(ImageService.class);
	}

}

package au.com.highlowgame.model;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import au.com.highlowgame.service.Image;
import au.com.highlowgame.service.ImageService;
import au.com.highlowgame.service.SecurityService;

@Configurable
@Entity
public class Player extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@Autowired
	transient ImageService imageService;

	@Autowired
	transient SecurityService securityService;

	@NotNull
	@Column(unique = true)
	@Size(max = 300)
	private String name;

	@Size(max = 60)
	private String email;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] avatarContent;

	@Size(max = 255)
	private String avatarContentType;

	private Long avatarContentSize;

	private boolean admin;

	@NotNull
	@Size(max = 100)
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getAvatarContent() {
		return avatarContent;
	}

	public void setAvatarContent(byte[] avatarContent) {
		this.avatarContent = avatarContent;
	}

	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}

	public Long getAvatarContentSize() {
		return avatarContentSize;
	}

	public void setAvatarContentSize(Long avatarContentSize) {
		this.avatarContentSize = avatarContentSize;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean getHasAvatar() {
		return getAvatarContent() != null && getAvatarContent().length > 0;

	}

	public Image getAvatarImage() {
		if (getAvatarContentSize() != null && getAvatarContentSize() > 0) {
			Image image = new Image();
			image.setContent(getAvatarContent());
			image.setContentType(getAvatarContentType());
			image.setSize(getAvatarContentSize());
			return image;
		}

		return null;
	}

	public String getAvatarThumbnailUrl() {
		if (getHasAvatar())
			return "/players/showAvatar/" + getId() + "?thumbnail=true&preventCaching=" + System.currentTimeMillis();
		else
			return "";
	}
	
	public String getAvatarUrl() {
		if (getHasAvatar())
			return "/players/showAvatar/" + getId() + "?mediumthumbnail=true&preventCaching=" + System.currentTimeMillis();
		else
			return "";
	}
	
	public String getLabel() {
			return getName() + " - " + getEmail();
	}

	@PrePersist
	protected void prePersist() {
		setPassword(securityService.encodePassword(getPassword()));
	}

	@Transactional
	public Player merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		Player merged = this.entityManager.merge(this);
		return merged;
	}

	public static Player find(String id) {
		if (id == null || id.length() == 0)
			return null;
		return entityManager().find(Player.class, id);
	}

	public static Player findPlayerForEmail(String email) {
		EntityManager em = DomainEntity.entityManager();
		StringBuilder qryBuilder = new StringBuilder();
		qryBuilder.append("SELECT o FROM Player AS o WHERE o.email = :email");
		TypedQuery<Player> q = em.createQuery(qryBuilder.toString(), Player.class);
		q.setParameter("email", email);
		try {
			return q.getSingleResult();
		} catch (Exception e) {
		}

		return null;
	}

	public static List<Player> findAllPlayers() {
		return entityManager().createQuery("SELECT o FROM Player o order by o.name", Player.class).getResultList();
	}

	public static List<Player> findPlayerEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM Player o order by o.name", Player.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public static long countPlayers() {
		return entityManager().createQuery("SELECT COUNT(o) FROM Player o", Long.class).getSingleResult();
	}

}

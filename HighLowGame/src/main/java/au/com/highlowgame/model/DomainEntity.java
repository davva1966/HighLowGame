package au.com.highlowgame.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@MappedSuperclass
@Configurable
public abstract class DomainEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	protected transient EntityManager entityManager;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "au.com.highlowgame.util.SSUUIDGenerator")
	@Column(name = "id", columnDefinition = "CHAR(36)")
	private String id;

	public static final EntityManager entityManager() {
		EntityManager em = new DomainEntity() {
			private static final long serialVersionUID = 1L;
		}.entityManager;
		if (em == null)
			throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	public String getEntityName() {
		return getClass().getSimpleName().toUpperCase();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static DomainEntity findDomainEntity(Class clazz, String id) {
		if (id == null || id.length() == 0)
			return null;
		return (DomainEntity) entityManager().find(clazz, id);
	}

	@Transactional
	public void persist() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		this.entityManager.persist(this);
	}

	@Transactional
	public void clear() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		this.entityManager.clear();
	}

	@Transactional
	public DomainEntity merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		DomainEntity merged = this.entityManager.merge(this);
		return merged;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DomainEntity)
			return ((DomainEntity) o).getId().equals(getId());

		return super.equals(o);
	}

	@Transactional
	public void remove() {

		// Check that item is not used
		validateRemove();

		if (this.entityManager == null)
			this.entityManager = entityManager();
		if (this.entityManager.contains(this)) {
			this.entityManager.remove(this);
		} else {
			DomainEntity attached = DomainEntity.findDomainEntity(getClass(), this.id);
			this.entityManager.remove(attached);
		}

	}

	protected void validateRemove() {

	}

	public String getViewPermissionName() {
		return "USER";
	}

	public String getMaintainPermissionName() {
		return "ADMIN";
	}

}

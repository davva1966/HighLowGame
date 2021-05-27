package au.com.highlowgame.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import au.com.highlowgame.model.DomainEntity;

public class DatabaseServiceImpl implements DatabaseService {

	DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean verifyUnique(DomainEntity entity, String property, Object value) {
		Map<String, Object> propertyValueMap = new HashMap<String, Object>();
		propertyValueMap.put(property, value);
		return verifyUnique(entity, propertyValueMap);
	}

	@Override
	public boolean verifyUnique(DomainEntity entity, Map<String, Object> propertyValueMap) {
		String entityName = entity.getClass().getSimpleName();
		Query query = createQuery(null, entityName, propertyValueMap);

		if (query.getResultList().size() == 0) {
			return true;
		} else if (query.getResultList().size() > 1) {
			return false;
		} else {
			DomainEntity existingEntity = (DomainEntity) query.getSingleResult();
			if (entity.getId() == null || existingEntity.getId().equals(entity.getId()) == false)
				return false;
		}

		return true;

	}

	@Override
	public DomainEntity getEntity(String entityName, String property, Object value) {
		return getEntity((EntityManager) null, entityName, property, value);
	}

	@Override
	public DomainEntity getEntity(EntityManager em, String entityName, String property, Object value) {
		Map<String, Object> propertyValueMap = new HashMap<String, Object>();
		propertyValueMap.put(property, value);
		return getEntity(em, entityName, propertyValueMap);
	}

	@Override
	public DomainEntity getEntity(String entityName, Map<String, Object> propertyValueMap) {
		return getEntity((EntityManager) null, entityName, propertyValueMap);

	}

	@Override
	public DomainEntity getEntity(EntityManager em, String entityName, Map<String, Object> propertyValueMap) {
		Query query = createQuery(em, entityName, propertyValueMap);
		try {
			return (DomainEntity) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	protected Query createQuery(EntityManager em, String entityName, Map<String, Object> propertyValueMap) {
		if (em == null)
			em = DomainEntity.entityManager();
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT o FROM ");
		queryBuilder.append(entityName);
		queryBuilder.append(" AS o WHERE o.");
		boolean addAnd = false;
		for (String property : propertyValueMap.keySet()) {
			Object value = propertyValueMap.get(property);
			if (addAnd)
				queryBuilder.append(" AND o.");
			queryBuilder.append(property);
			if (value != null)
				queryBuilder.append(" = :" + property.replace(".", "_"));
			else
				queryBuilder.append(" IS NULL");
			addAnd = true;
		}

		Query query = em.createQuery(queryBuilder.toString());
		for (String property : propertyValueMap.keySet()) {
			Object value = propertyValueMap.get(property);
			if (value != null)
				query.setParameter(property.replace(".", "_"), propertyValueMap.get(property));
		}

		return query;

	}

	@Override
	@Transactional
	public void removeEntities(List<DomainEntity> entities) {
		for (DomainEntity entity : entities) {
			if (entity != null)
				entity.remove();
		}
	}
}

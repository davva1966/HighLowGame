package au.com.highlowgame.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import au.com.highlowgame.model.DomainEntity;

@Service("databaseService")
public interface DatabaseService {

	boolean verifyUnique(DomainEntity entity, String property, Object value);

	boolean verifyUnique(DomainEntity entity, Map<String, Object> propertyValueMap);

	public DomainEntity getEntity(String entityName, String property, Object value);

	public DomainEntity getEntity(String entityName, Map<String, Object> propertyValueMap);

	public DomainEntity getEntity(EntityManager em, String entityName, String property, Object value);

	public DomainEntity getEntity(EntityManager em, String entityName, Map<String, Object> propertyValueMap);

	public void removeEntities(List<DomainEntity> entities);
	
	public DataSource getDataSource();

}

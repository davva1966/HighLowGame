package au.com.highlowgame.util;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;

import au.com.highlowgame.model.DomainEntity;
import au.com.highlowgame.service.DatabaseService;

import au.com.highlowgame.util.SSUtil;

public class SSDomainEntityPropertyEditor extends PropertyEditorSupport {

	@Autowired
	private DatabaseService databaseService;

	public SSDomainEntityPropertyEditor() {
		super();
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		DomainEntity entity = null;
		if (text != null && text.trim().length() > 0) {
			String[] entArr = SSUtil.formatSeparatedToArray(text, ":", true);
			entity = databaseService.getEntity(entArr[0], "id", entArr[1]);
		}
		setValue(entity);

	}

	@Override
	public String getAsText() {
		if (getValue() == null)
			return null;
		DomainEntity entity = (DomainEntity) getValue();
		if (entity.getId() == null)
			return null;
		return entity.getClass().getSimpleName() + ":" + entity.getId();
	}

}

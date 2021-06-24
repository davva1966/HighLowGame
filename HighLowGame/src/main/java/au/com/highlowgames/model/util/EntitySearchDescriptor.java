package au.com.highlowgames.model.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import au.com.highlowgame.util.SSUtil;

public class EntitySearchDescriptor {

	public static final String ENTITY_MARKER = "entityMarker";
	public static final String SEARCH_TERM_MARKER = "searchTermMarker";

	protected String queryString;
	protected List<Object> searchTerms = new ArrayList<Object>();

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void addSearchTerm(Object term) {
		searchTerms.add(term);
	}

	public void addSearch(StringBuilder sb, String attribute, String comparison, int searchTermIndex) {
		addSearch(sb, attribute, comparison, searchTermIndex, false);
	}

	public void addSearch(StringBuilder sb, String attribute, String comparison, int searchTermIndex, boolean convertToString) {
		if (convertToString)
			sb.append("STR(" + EntitySearchDescriptor.ENTITY_MARKER + "." + attribute + ") " + comparison + ":" + EntitySearchDescriptor.SEARCH_TERM_MARKER + searchTermIndex);
		else
			sb.append(EntitySearchDescriptor.ENTITY_MARKER + "." + attribute + " " + comparison + ":" + EntitySearchDescriptor.SEARCH_TERM_MARKER + searchTermIndex);
	}

	public boolean hasSearch() {
		return SSUtil.notEmpty(getQueryString()) && !searchTerms.isEmpty();
	}

	public String getQueryString(String entity) {
		if (SSUtil.empty(getQueryString()))
			return "";
		return getQueryString().replace(ENTITY_MARKER, entity);
	}

	public void setParameters(Query query) {
		if (SSUtil.empty(getQueryString()) || searchTerms.isEmpty())
			return;

		int searchTermIndex = 1;
		for (Object searchTerm : searchTerms) {
			String marker = SEARCH_TERM_MARKER + searchTermIndex;
			query.setParameter(marker, searchTerm);
			searchTermIndex++;
		}

	}

}

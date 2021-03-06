package org.wikidata.wdtk.datamodel.json.jackson;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Abstract Jackson implementation of {@link TermedDocument}. Like all Jackson
 * objects, it is not technically immutable, but it is strongly recommended to
 * treat it as such in all contexts: the setters are for Jackson; never call
 * them in your code.
 *
 * @author Fredo Erxleben
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = JacksonItemDocument.class, name = JacksonTermedDocument.JSON_TYPE_ITEM),
		@Type(value = JacksonPropertyDocument.class, name = JacksonTermedDocument.JSON_TYPE_PROPERTY) })
public abstract class JacksonTermedDocument implements TermedDocument {

	/**
	 * String used to refer to items in JSON.
	 */
	public static final String JSON_TYPE_ITEM = "item";
	/**
	 * String used to refer to properties in JSON.
	 */
	public static final String JSON_TYPE_PROPERTY = "property";

	@JsonDeserialize(using = AliasesDeserializer.class)
	protected Map<String, List<JacksonMonolingualTextValue>> aliases = new HashMap<>();
	protected Map<String, JacksonMonolingualTextValue> labels = new HashMap<>();
	protected Map<String, JacksonMonolingualTextValue> descriptions = new HashMap<>();

	/**
	 * The id of the entity that the document refers to. This is not mapped to
	 * JSON directly by Jackson but split into two fields, "type" and "id". The
	 * type field is ignored during deserialization since the type is clear for
	 * a concrete document. For serialization, the type is hard-coded.
	 * <p>
	 * The site IRI, which would also be required to create a complete
	 * {@link EntityIdValue}, is not encoded in JSON. It needs to be injected
	 * from the outside (if not, we default to Wikidata).
	 */
	protected String entityId = "";

	/**
	 * The site IRI that this document refers to, or null if not specified. In
	 * the latter case, we assume Wikidata as the default.
	 *
	 * @see EntityIdValue#getSiteIri()
	 */
	@JsonIgnore
	protected String siteIri = null;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonTermedDocument() {
	}

	/**
	 * Sets the aliases to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param aliases
	 *            new value
	 */
	public void setAliases(
			Map<String, List<JacksonMonolingualTextValue>> aliases) {
		this.aliases = aliases;
	}

	@Override
	public Map<String, List<MonolingualTextValue>> getAliases() {
		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, List<MonolingualTextValue>> returnMap = new HashMap<>();

		for (Entry<String, List<JacksonMonolingualTextValue>> entry : this.aliases
				.entrySet()) {
			returnMap.put(entry.getKey(), Collections
					.<MonolingualTextValue> unmodifiableList(entry.getValue()));
		}

		return Collections.unmodifiableMap(returnMap);
	}

	/**
	 * Sets the descriptions to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param descriptions
	 *            new value
	 */
	public void setDescriptions(
			Map<String, JacksonMonolingualTextValue> descriptions) {
		this.descriptions = descriptions;
	}

	@Override
	public Map<String, MonolingualTextValue> getDescriptions() {
		return Collections
				.<String, MonolingualTextValue> unmodifiableMap(this.descriptions);
	}

	/**
	 * Sets the labels to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param labels
	 *            new value
	 */
	public void setLabels(Map<String, JacksonMonolingualTextValue> labels) {
		this.labels = labels;
	}

	@Override
	public Map<String, MonolingualTextValue> getLabels() {
		return Collections
				.<String, MonolingualTextValue> unmodifiableMap(this.labels);
	}

	/**
	 * Sets the string id of the entity that this document refers to. Only for
	 * use by Jackson during deserialization.
	 *
	 * @param id
	 *            new value
	 */
	@JsonProperty("id")
	public void setJsonId(String id) {
		this.entityId = id;
	}

	/**
	 * Returns the string id of the entity that this document refers to. Only
	 * for use by Jackson during serialization.
	 *
	 * @return string id
	 */
	@JsonProperty("id")
	public String getJsonId() {
		return this.entityId;
	}

	/**
	 * Sets the site iri to the given value. This can be used to inject
	 * information about the site the object belongs to after the object is
	 * constructed. This is needed since this information is not part of the
	 * JSON serialization.
	 *
	 * @see EntityIdValue#getSiteIri()
	 * @param siteIri
	 *            the site IRI
	 */
	@JsonIgnore
	public void setSiteIri(String siteIri) {
		this.siteIri = siteIri;
	}

	@JsonIgnore
	public String getSiteIri() {
		return this.siteIri;
	}

	/**
	 * Returns the JSON type string of the entity that this document refers to.
	 * Only used by Jackson.
	 *
	 * @return either {@link JacksonTermedDocument#JSON_TYPE_ITEM} or
	 *         {@link JacksonTermedDocument#JSON_TYPE_PROPERTY}
	 */
	@JsonProperty("type")
	public abstract String getJsonType();

}

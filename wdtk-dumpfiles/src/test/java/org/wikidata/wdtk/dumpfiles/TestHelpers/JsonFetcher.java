package org.wikidata.wdtk.dumpfiles.TestHelpers;

import java.io.IOException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.dumpfiles.JsonConverter;
import org.wikidata.wdtk.testing.MockStringContentFactory;

/**
 * A simple helper class to get JSON objects from a ressource file.
 * 
 * @author Fredo Erxleben
 * 
 */
public class JsonFetcher {
	// NOTE: in the case of switching to fasterxml.jackson this class might be
	// outfitted with more methods to accomodate different ways of in- / output

	private static final String SAMPLE_FILES_BASE_PATH = "/testSamples/";

	/**
	 * Returns a JSON object for the JSON stored in the given resource.
	 * 
	 * @param resourceName
	 *            a file name without any path information
	 * @return the JSONObject
	 * @throws IOException
	 * @throws JSONException
	 */
	public JSONObject getJsonObjectForResource(String resourceName)
			throws IOException, JSONException {

		URL resourceUrl = this.getClass().getResource(
				JsonFetcher.SAMPLE_FILES_BASE_PATH + resourceName);
		String jsonString = MockStringContentFactory
				.getStringFromUrl(resourceUrl);
		return new JSONObject(jsonString);
	}
	
	/**
	 * Returns a JSON array for the JSON stored in the given resource.
	 * 
	 * @param resourceName
	 *            a file name without any path information
	 * @return the JSONArray
	 * @throws IOException
	 * @throws JSONException
	 */
	public JSONArray getJsonArrayForResource(String resourceName)
			throws IOException, JSONException {

		URL resourceUrl = this.getClass().getResource(
				JsonFetcher.SAMPLE_FILES_BASE_PATH + resourceName);
		String jsonString = MockStringContentFactory
				.getStringFromUrl(resourceUrl);
		return new JSONArray(jsonString);
	}

	/**
	 * Applies the JSON converter to the JSON stored in the given resource to
	 * return an ItemDocument.
	 * 
	 * @param fileName
	 *            the file name only, no path information
	 * @param itemId
	 *            the string id of the item
	 * @throws IOException
	 * @throws JSONException
	 * @return the ItemDocument
	 */
	public ItemDocument getItemDocumentFromResource(String fileName,
			String itemId, JsonConverter unitUnderTest) throws IOException,
			JSONException {
		
		JSONObject jsonObject = this.getJsonObjectForResource(fileName);
		return unitUnderTest.convertToItemDocument(jsonObject, itemId);
	}

	/**
	 * Applies the JSON converter to the JSON stored in the given resource to
	 * return a PropertyDocument.
	 * 
	 * @param fileName
	 *            the file name only, no path information. The file is supposed
	 *            to be in the "resources/testSamples/"-directory.
	 * @param propertyId
	 *            the string id of the property
	 * @return the PropertyDocument
	 * @throws JSONException
	 * @throws IOException
	 */
	public PropertyDocument getPropertyDocumentFromResource(String fileName,
			String propertyId, JsonConverter unitUnderTest) throws IOException,
			JSONException {
		
		JSONObject jsonObject = this.getJsonObjectForResource(fileName);
		return unitUnderTest.convertToPropertyDocument(jsonObject, propertyId);
	}

}
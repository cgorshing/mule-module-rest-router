package org.mule.modules.rest;

public class RestUriUtils {

	
	
	public static final String REST_RESOURCE_DELIMITER = "/";

	private static int charCount(String subject, String needle) {
    	return subject.split(needle).length-1;
    }

	public static boolean equalDepths(String uri, String templateUri) {
		return (charCount(uri, REST_RESOURCE_DELIMITER)==charCount(templateUri, REST_RESOURCE_DELIMITER));
	}

}

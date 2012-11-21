/**
 * Mule Rest Module
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

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

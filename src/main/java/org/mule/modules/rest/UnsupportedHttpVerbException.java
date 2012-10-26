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

/**
 * Exception thrown when the HTTP method is not supported by
 * the REST router.
 */
public class UnsupportedHttpVerbException extends Exception {
    public UnsupportedHttpVerbException(String method) {
        super("Unsupported HTTP verb: " + method);
    }
}

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

import org.apache.log4j.Logger;
import org.mule.api.NestedProcessor;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.InboundHeaders;
import org.mule.api.annotations.param.Optional;
import org.mule.api.callback.SourceCallback;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Mule module for implementing REST APIs whitin flows.
 * <p/>
 * The module includes a custom router that will dispatch the message
 * to the inner message processors as long as the URI matches and
 * the processors are attached to the right HTTP method.
 *
 * @author MuleSoft, Inc.
 */
@Module(name = "rest-router")
public class RestRouterModule
{
    private static final Logger LOGGER = Logger.getLogger(RestRouterModule.class);

    /**
     * Captures URI template variable names.
     */
    private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

    /**
     * Replaces template variables in the URI template.
     */
    private static final String VALUE_REGEX = "(.*)";

    /**
     * A REST router is an intercepting message processor that will only execute if the incoming message
     * contains a <i>http.request.path</i> property that matches the templareUri parameter.
     * <p/>
     * An URI template is a URI-like String that contained variables marked of in
     * braces (<code>{</code>, <code>}</code>), which can be expanded to produce a URI.
     * <p/>
     * The following is an example URI template:
     * <p/>
     * <code>
     * /hotels/{hotel}/bookings/{booking}
     * </code>
     * <p/>
     * If the incoming URI matches the template it will extract the variables in it and it will make them available as
     * properties.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-rest-router.xml.sample rest-router:router}
     * {@sample.java ../../../doc/mule-module-rest-router.java.sample rest-router:router}
     *
     * @param templateUri    Template URI string. Variables need to be contained within braces <code>{</code>, <code>}</code>.
     * @param method         The HTTP method of the request
     * @param uri            The HTTP uri of the request
     * @param get            Processor to be executed when the HTTP method is "GET"
     * @param put            Processor to be executed when the HTTP method is "PUT"
     * @param post           Processor to be executed when the HTTP method is "POST"
     * @param delete         Processor to be executed when the HTTP method is "DELETE"
     * @param patch          Processor to be executed when the HTTP method is "PATCH"  {@see http://tools.ietf.org/html/rfc5789}
     * @param sourceCallback the source callback
     * @return {@link Object}
     * @throws UnsupportedHttpVerbException Thrown when the HTTP method is not available or when the processor to be
     *                                      executed is null.
     * @throws Exception                    Thrown when something goes wrong processing the request.
     */
    @Processor(intercepting = true)
    public Object router(String templateUri,
                         @Optional NestedProcessor get,
                         @Optional NestedProcessor put,
                         @Optional NestedProcessor post,
                         @Optional NestedProcessor delete,
                         @Optional NestedProcessor patch,
                         @InboundHeaders("http.method") String method,
                         @InboundHeaders("http.request.path") String uri,
                         SourceCallback sourceCallback) throws Exception {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Request Path = {" + uri + "} Method = {" + method + "}");
        }

        Parser parser = new Parser(templateUri);

        List<String> variableNames = parser.getVariableNames();
        Pattern matchPattern = parser.getMatchPattern();
        Matcher matcher = matchPattern.matcher(uri);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Match Pattern = {" + matchPattern.pattern() + "} Matches = {" + matcher.matches() + "}");
        }

        if (matcher.matches()) {

            // extract URI variables                               `
            Map<String, Object> properties = new LinkedHashMap<String, Object>(variableNames.size());
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String name = variableNames.get(i - 1);
                String value = matcher.group(i);
                properties.put(name, value);
            }

            // execute correct chain using same payload but with
            // the extracted URI variables added
            if (get != null && method.equalsIgnoreCase("get")) {
                return get.processWithExtraProperties(properties);
            } else if (put != null && method.equalsIgnoreCase("put")) {
                return put.processWithExtraProperties(properties);
            } else if (post != null && method.equalsIgnoreCase("post")) {
                return post.processWithExtraProperties(properties);
            } else if (delete != null && method.equalsIgnoreCase("delete")) {
                return delete.processWithExtraProperties(properties);
            } else if (patch != null && method.equalsIgnoreCase("patch")) {
                return patch.processWithExtraProperties(properties);
            } else {
                throw new UnsupportedHttpVerbException(method);
            }
        }

        // this does not really matter since we are intercepting the
        // actual return will be the one from the next MP of the chain
        return sourceCallback.process();
    }

    /**
     * Static inner class to parse uri template strings into a matching regular expression.
     */
    private static class Parser {

        private final List<String> variableNames = new LinkedList<String>();

        private final StringBuilder patternBuilder = new StringBuilder();

        private Parser(String uriTemplate) {
            Matcher m = NAMES_PATTERN.matcher(uriTemplate);
            int end = 0;
            while (m.find()) {
                this.patternBuilder.append(quote(uriTemplate, end, m.start()));
                this.patternBuilder.append(VALUE_REGEX);
                this.variableNames.add(m.group(1));
                end = m.end();
            }
            this.patternBuilder.append(quote(uriTemplate, end, uriTemplate.length()));
            int lastIdx = this.patternBuilder.length() - 1;
            if (lastIdx >= 0 && this.patternBuilder.charAt(lastIdx) == '/') {
                this.patternBuilder.deleteCharAt(lastIdx);
            }
        }

        private String quote(String fullPath, int start, int end) {
            if (start == end) {
                return "";
            }
            return Pattern.quote(fullPath.substring(start, end));
        }

        private List<String> getVariableNames() {
            return Collections.unmodifiableList(this.variableNames);
        }

        private Pattern getMatchPattern() {
            return Pattern.compile(this.patternBuilder.toString());
        }
    }
}

package com.github.pavelt.appsistedparking.security;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class Sanitizer {
    /**
     * Sanitizes user input by stripping it of:
     * SQL, JavaScript (XSS), HMTL
     *
     * @param userInput - input to sanitize
     * @return sanitized input
     */
    public static String sanitizeAll(String userInput) {
        return Jsoup.clean(StringEscapeUtils.escapeHtml(
                           StringEscapeUtils.escapeJavaScript(
                           StringEscapeUtils.escapeSql(userInput))),
                           Whitelist.basic());
    }
}

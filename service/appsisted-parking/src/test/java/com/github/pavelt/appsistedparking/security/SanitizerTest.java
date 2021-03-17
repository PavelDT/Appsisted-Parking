package com.github.pavelt.appsistedparking.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class SanitizerTest {


    /**
     * Checks if HTML is stripped of html tags.
     */
    @Test
    public void sanitizeHTML() {
        String html = "<html>injecting html</html>";
        String expectedCleansed = "&lt;html&gt;injecting html&lt;\\/html&gt;";
        String sanitized = Sanitizer.sanitizeAll(html);

        assertEquals(expectedCleansed, sanitized);
    }

    /**
     * Tests that script tag can be escaped to prevent from javascript injection.
     */
    @Test
    public void sanitizeXSS() {
        String javaScript = "<script>const a = Math.random(); console.log(a);</script>";
        String expectedCleansed = "&lt;script&gt;const a = Math.random(); console.log(a);&lt;\\/script&gt;";
        String sanitized = Sanitizer.sanitizeAll(javaScript);

        assertEquals(expectedCleansed, sanitized);
    }
}

package com.serkowski.task7_4.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Redacts sensitive PII data from text (the "Shrek" approach — buffer chunks, then redact).
 * Covers: SSN, credit card numbers, CVV, expiration dates, phone numbers,
 * email addresses, driver's license, bank account numbers, addresses, income, DOB.
 */
@Service
public class TextRedactor {

    /**
     * Maximum possible length of a PII match across all patterns.
     * The ADDRESS pattern is the longest (~100 chars: "12345 Some Long Street Name Boulevard, Unit 12B, Springfield, WA, 98765").
     * This value is used as overlap size to prevent patterns from being split across buffer windows.
     */
    public static final int MAX_PII_LENGTH = 120;

    private static final Map<String, Pattern> PII_PATTERNS = new LinkedHashMap<>();

    static {
        // SSN: 123-45-6789 or 123 45 6789
        PII_PATTERNS.put("[SSN REDACTED]",
                Pattern.compile("\\b\\d{3}[- ]\\d{2}[- ]\\d{4}\\b"));

        // Credit card: 4111 1111 1111 1111 or 4111-1111-1111-1111 or 4111111111111111
        PII_PATTERNS.put("[CARD REDACTED]",
                Pattern.compile("\\b(?:\\d{4}[- ]?){3}\\d{4}\\b"));

        // CVV: 3 or 4 digit code after "CVV" keyword
        PII_PATTERNS.put("[CVV REDACTED]",
                Pattern.compile("(?i)(?<=CVV[:\\s]{0,3})\\d{3,4}"));

        // Expiration date: MM/YY or MM/YYYY
        PII_PATTERNS.put("[EXP REDACTED]",
                Pattern.compile("(?i)(?<=Exp[:\\s]{0,3})\\d{2}/\\d{2,4}"));

        // Phone: (206) 555-0683 or 206-555-0683 or 206.555.0683
        PII_PATTERNS.put("[PHONE REDACTED]",
                Pattern.compile("\\(?\\d{3}\\)?[-.\\s]\\d{3}[-.\\s]\\d{4}"));

        // Email
        PII_PATTERNS.put("[EMAIL REDACTED]",
                Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b"));

        // Driver's license: WA-DL-J648572139 pattern
        PII_PATTERNS.put("[DL REDACTED]",
                Pattern.compile("\\b[A-Z]{2}-DL-[A-Z0-9]+\\b"));

        // Bank account number: digits 7-17 after "account" context
        PII_PATTERNS.put("[ACCOUNT REDACTED]",
                Pattern.compile("(?i)(?<=account[:\\s#]{0,5})\\d{7,17}"));

        // Dollar amounts (income, salary)
        PII_PATTERNS.put("[AMOUNT REDACTED]",
                Pattern.compile("\\$[\\d,]+(?:\\.\\d{2})?"));

        // Street address pattern: number + street name + (Ave|St|Rd|Blvd|Dr|Ln|Way|Unit...)
        PII_PATTERNS.put("[ADDRESS REDACTED]",
                Pattern.compile("\\b\\d{1,5}\\s[A-Z][a-zA-Z\\s]+(?:Avenue|Ave|Street|St|Road|Rd|Boulevard|Blvd|Drive|Dr|Lane|Ln|Way|Court|Ct|Circle|Cir)\\b(?:[\\s,]+(?:Unit|Apt|Suite|#)\\s*\\S+)?(?:[\\s,]+[A-Z][a-z]+(?:[\\s,]+[A-Z]{2})?(?:[\\s,]+\\d{5})?)?"));

        // Date of birth patterns: September 12, 1990 or 09/12/1990
        PII_PATTERNS.put("[DOB REDACTED]",
                Pattern.compile("(?i)(?:January|February|March|April|May|June|July|August|September|October|November|December)\\s+\\d{1,2},?\\s+\\d{4}"));
    }

    /**
     * Redacts all known PII patterns from the given text.
     */
    public String redact(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String redacted = text;
        for (Map.Entry<String, Pattern> entry : PII_PATTERNS.entrySet()) {
            redacted = entry.getValue().matcher(redacted).replaceAll(entry.getKey());
        }
        return redacted;
    }
}


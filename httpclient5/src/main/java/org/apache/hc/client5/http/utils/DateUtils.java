package org.apache.hc.client5.http.utils;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.util.Args;

import java.lang.ref.SoftReference;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A utility class for parsing and formatting HTTP dates as used in cookies and
 * other headers.  This class handles dates as defined by RFC 2616 section
 * 3.3.1 as well as some other common non-standard formats.
 *
 * @since 4.3
 */
public final class DateUtils {

    /**
     * Date format pattern used to parse HTTP date headers in RFC 1123 format.
     */
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in RFC 1036 format.
     */
    public static final String PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in ANSI C
     * {@code asctime()} format.
     */
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

    private static final String[] DEFAULT_PATTERNS = new String[]{
            PATTERN_RFC1123,
            PATTERN_RFC1036,
            PATTERN_ASCTIME
    };

    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    static {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(GMT);
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    /**
     * Parses a date value.  The formats used for parsing the date value are retrieved from
     * the default http params.
     *
     * @param dateValue the date value to parse
     * @return the parsed date or null if input could not be parsed
     */
    public static Date parseDate(final String dateValue) {
        return parseDate(dateValue, null, null);
    }

    /**
     * Parses a date value from a header with the given name.
     *
     * @param headers    message headers
     * @param headerName header name
     * @return the parsed date or null if input could not be parsed
     * @since 5.0
     */
    public static Date parseDate(final MessageHeaders headers, final String headerName) {
        if (headers == null) {
            return null;
        }
        final Header header = headers.getFirstHeader(headerName);
        if (header == null) {
            return null;
        }
        return parseDate(header.getValue(), null, null);
    }

    /**
     * Tests if the first message is after (newer) than seconds message
     * using the given message header for comparison.
     *
     * @param message1   the first message
     * @param message2   the second message
     * @param headerName header name
     * @return {@code true} if both messages contain a header with the given name
     * and the value of the header from the first message is newer that of
     * the second message.
     * @since 5.0
     */
    public static boolean isAfter(
            final MessageHeaders message1,
            final MessageHeaders message2,
            final String headerName) {
        if (message1 != null && message2 != null) {
            final Header dateHeader1 = message1.getFirstHeader(headerName);
            if (dateHeader1 != null) {
                final Header dateHeader2 = message2.getFirstHeader(headerName);
                if (dateHeader2 != null) {
                    final Date date1 = parseDate(dateHeader1.getValue());
                    if (date1 != null) {
                        final Date date2 = parseDate(dateHeader2.getValue());
                        if (date2 != null) {
                            return date1.after(date2);
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Tests if the first message is before (older) than seconds message
     * using the given message header for comparison.
     *
     * @param message1   the first message
     * @param message2   the second message
     * @param headerName header name
     * @return {@code true} if both messages contain a header with the given name
     * and the value of the header from the first message is older that of
     * the second message.
     * @since 5.0
     */
    public static boolean isBefore(
            final MessageHeaders message1,
            final MessageHeaders message2,
            final String headerName) {
        if (message1 != null && message2 != null) {
            final Header dateHeader1 = message1.getFirstHeader(headerName);
            if (dateHeader1 != null) {
                final Header dateHeader2 = message2.getFirstHeader(headerName);
                if (dateHeader2 != null) {
                    final Date date1 = parseDate(dateHeader1.getValue());
                    if (date1 != null) {
                        final Date date2 = parseDate(dateHeader2.getValue());
                        if (date2 != null) {
                            return date1.before(date2);
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Parses the date value using the given date formats.
     *
     * @param dateValue   the date value to parse
     * @param dateFormats the date formats to use
     * @return the parsed date or null if input could not be parsed
     */
    public static Date parseDate(final String dateValue, final String[] dateFormats) {
        return parseDate(dateValue, dateFormats, null);
    }

    /**
     * Parses the date value using the given date formats.
     *
     * @param dateValue   the date value to parse
     * @param dateFormats the date formats to use
     * @param startDate   During parsing, two digit years will be placed in the range
     *                    {@code startDate} to {@code startDate + 100 years}. This value may
     *                    be {@code null}. When {@code null} is given as a parameter, year
     *                    {@code 2000} will be used.
     * @return the parsed date or null if input could not be parsed
     */
    public static Date parseDate(
            final String dateValue,
            final String[] dateFormats,
            final Date startDate) {
        Args.notNull(dateValue, "Date value");
        final String[] localDateFormats = dateFormats != null ? dateFormats : DEFAULT_PATTERNS;
        final Date localStartDate = startDate != null ? startDate : DEFAULT_TWO_DIGIT_YEAR_START;
        String v = dateValue;
        // trim single quotes around date if present
        // see issue #5279
        if (v.length() > 1 && v.startsWith("'") && v.endsWith("'")) {
            v = v.substring(1, v.length() - 1);
        }

        for (final String dateFormat : localDateFormats) {
            final SimpleDateFormat dateParser = DateFormatHolder.formatFor(dateFormat);
            dateParser.set2DigitYearStart(localStartDate);
            final ParsePosition pos = new ParsePosition(0);
            final Date result = dateParser.parse(v, pos);
            if (pos.getIndex() != 0) {
                return result;
            }
        }
        return null;
    }

    /**
     * Formats the given date according to the RFC 1123 pattern.
     *
     * @param date The date to format.
     * @return An RFC 1123 formatted date string.
     * @see #PATTERN_RFC1123
     */
    public static String formatDate(final Date date) {
        return formatDate(date, PATTERN_RFC1123);
    }

    /**
     * Formats the given date according to the specified pattern.  The pattern
     * must conform to that used by the {@link SimpleDateFormat simple date
     * format} class.
     *
     * @param date    The date to format.
     * @param pattern The pattern to use for formatting the date.
     * @return A formatted date string.
     * @throws IllegalArgumentException If the given date pattern is invalid.
     * @see SimpleDateFormat
     */
    public static String formatDate(final Date date, final String pattern) {
        Args.notNull(date, "Date");
        Args.notNull(pattern, "Pattern");
        final SimpleDateFormat formatter = DateFormatHolder.formatFor(pattern);
        return formatter.format(date);
    }

    /**
     * Clears thread-local variable containing {@link java.text.DateFormat} cache.
     *
     * @since 4.3
     */
    public static void clearThreadLocal() {
        DateFormatHolder.clearThreadLocal();
    }

    /**
     * This class should not be instantiated.
     */
    private DateUtils() {
    }

    /**
     * A factory for {@link SimpleDateFormat}s. The instances are stored in a
     * threadlocal way because SimpleDateFormat is not threadsafe as noted in
     * {@link SimpleDateFormat its javadoc}.
     */
    final static class DateFormatHolder {

        private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS = new ThreadLocal<>();

        /**
         * creates a {@link SimpleDateFormat} for the requested format string.
         *
         * @param pattern a non-{@code null} format String according to
         *                {@link SimpleDateFormat}. The format is not checked against
         *                {@code null} since all paths go through
         *                {@link DateUtils}.
         * @return the requested format. This simple dateformat should not be used
         * to {@link SimpleDateFormat#applyPattern(String) apply} to a
         * different pattern.
         */
        public static SimpleDateFormat formatFor(final String pattern) {
            final SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
            Map<String, SimpleDateFormat> formats = ref == null ? null : ref.get();
            if (formats == null) {
                formats = new HashMap<>();
                THREADLOCAL_FORMATS.set(new SoftReference<>(formats));
            }

            SimpleDateFormat format = formats.get(pattern);
            if (format == null) {
                format = new SimpleDateFormat(pattern, Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                formats.put(pattern, format);
            }

            return format;
        }

        public static void clearThreadLocal() {
            THREADLOCAL_FORMATS.remove();
        }
    }
}

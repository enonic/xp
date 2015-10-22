package com.enonic.xp.web.jetty.impl;

public @interface JettyConfig
{
    /**
     * Host name.
     */
    String host();

    /**
     * Connector timeout in min.
     */
    int timeout() default 60000;

    /**
     * Send server header.
     */
    boolean sendServerHeader() default false;

    /**
     * Http enabled.
     */
    boolean http_enabled() default true;

    /**
     * Http port.
     */
    int http_port() default 8080;

    /**
     * Session timeout in minutes.
     */
    int session_timeout() default 60;

    /**
     * Session cookie name.
     */
    String session_cookieName() default "JSESSIONID";

    /**
     * Multipart store location. Defaults to java temporary directory.
     */
    String multipart_store();

    /**
     * Max file size for multipart (in MB).
     */
    int multipart_maxFileSize() default 1;

    /**
     * Max request size for multipart (in MB).
     */
    int multipart_maxRequestSize() default 10;

    /**
     * File size treshold for when to store to disk. 0 means always. Specified in bytes.
     */
    int multipart_fileSizeThreshold() default 0;

    /**
     * True if GZip should be enabled.
     */
    boolean gzip_enabled() default true;

    /**
     * Content will only be compressed if content length is either unknown or greater than value.
     */
    int gzip_minSize() default 16;

    /**
     * Logging to file enabled.
     */
    boolean log_enabled() default false;

    /**
     * Logging file name to use.
     */
    String log_file();

    /**
     * Append to log.
     */
    boolean log_append() default true;

    /**
     * Log extended information.
     */
    boolean log_extended() default true;

    /**
     * Timezone to log in.
     */
    String log_timeZone() default "GMT";

    /**
     * Retain log for number of days.
     */
    int log_retainDays() default 31;
}

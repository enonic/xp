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
     * Http xp port.
     */
    int http_xp_port() default 8080;

    /**
     * Http management port.
     *
     * @see <a href="https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers">iana.org port numbers</a>
     */
    int http_management_port() default 4848;

    /**
     * Http monitor port.
     *
     * @see <a href="https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers">iana.org port numbers</a>
     */
    int http_monitor_port() default 2609;

    /**
     * Session timeout in minutes.
     */
    int session_timeout() default 60;

    /**
     * Max request header size (32K is default).
     */
    int http_requestHeaderSize() default 32 * 1024;

    /**
     * Max response header size (32K is default).
     */
    int http_responseHeaderSize() default 32 * 1024;

    /**
     * Session cookie name.
     */
    String session_cookieName() default "JSESSIONID";

    /**
     * Multipart store location. Defaults to java temporary directory.
     */
    String multipart_store();

    /**
     * Max file size for multipart (-1 if unlimited).
     */
    long multipart_maxFileSize() default -1;

    /**
     * Max request size for multipart (-1 if unlimited).
     */
    long multipart_maxRequestSize() default -1;

    /**
     * File size treshold for when to store to disk. 0 means always. Specified in bytes.
     */
    int multipart_fileSizeThreshold() default 1000;

    /**
     * True if GZip should be enabled.
     */
    boolean gzip_enabled() default true;

    /**
     * Content will only be compressed if content length is either unknown or greater than value.
     */
    int gzip_minSize() default 23;

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

    /**
     * Maximum number of threads.
     */
    int threadPool_maxThreads() default 200;

    /**
     * Minimum number of threads.
     */
    int threadPool_minThreads() default 8;

    /**
     * Thread Idle Timeout (in milliseconds).
     */
    int threadPool_idleTimeout() default 60000;
}

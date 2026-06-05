package com.enonic.xp.web.jetty.impl;

public @interface JettyConfig
{
    /**
     * Bind address used for all connectors unless overridden per connector.
     * Supports network interface expressions, e.g. "_en0_", "_local_".
     * "_auto_" resolves to loopback in dev mode and all interfaces in prod mode.
     */
    String host() default "_auto_";

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
     * Http web port. When not set, falls back to the deprecated {@code http.xp.port}, then to 8080.
     */
    int http_web_port() default -1;

    /**
     * Http web bind address. Overrides {@link #host()} for the web connector.
     */
    String http_web_host();

    /**
     * Http xp port.
     *
     * @deprecated use {@link #http_web_port()} instead.
     */
    @Deprecated
    int http_xp_port() default -1;

    /**
     * Http management port.
     *
     * @see <a href="https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers">iana.org port numbers</a>
     */
    int http_management_port() default 4848;

    /**
     * Http management bind address. Overrides {@link #host()} for the management connector.
     */
    String http_management_host();

    /**
     * Http statistics port. When not set, falls back to the deprecated {@code http.monitor.port}, then to 2609.
     *
     * @see <a href="https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers">iana.org port numbers</a>
     */
    int http_statistics_port() default -1;

    /**
     * Http statistics bind address. Overrides {@link #host()} for the statistics connector.
     */
    String http_statistics_host();

    /**
     * Http monitor port.
     *
     * @deprecated use {@link #http_statistics_port()} instead.
     */
    @Deprecated
    int http_monitor_port() default -1;

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
     * Session cookie SameSite policy. Lax, Strict, None. Empty means "use browser default".
     */
    String session_cookieSameSite() default "Lax";

    /**
     * If set to true, session cookie marked as Secure on both HTTP and HTTPS.
     */
    boolean session_cookieAlwaysSecure() default false;

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
    boolean gzip_enabled() default false;

    /**
     * Content will only be compressed if content length is either unknown or greater than value.
     */
    int gzip_minSize() default 32;

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

    /**
     * The time in milliseconds that a websocket may be idle before closing.
     */
    long websocket_idleTimeout() default 300000;

    /**
     * Enforce a same-origin check on WebSocket handshakes by default. When false, the platform
     * accepts any {@code Origin} unless a controller supplies its own {@code checkOrigin} function.
     * Disable only as a fallback for deployments where the reverse proxy does not propagate the
     * public scheme/host/port to Jetty and the proxy configuration cannot be fixed.
     */
    boolean websocket_defaultOriginCheck() default true;
}

package com.enonic.xp.impl.server.rest;

public @interface AppManagementConfig
{
    /**
     * Wildcard patterns of URLs the {@code pull} verb may install from.
     * Empty disables {@code pull}. When absent {@code null}, falls back to {@link #installUrl_allowedUrls()}.
     */
    String pull_allowedUrls();

    /**
     * Whether {@code pull} requires a {@code sha512} checksum ({@code true}/{@code false}). When absent (empty), falls back to
     * {@link #installUrl_checksumRequired()}.
     */
    String pull_checksumRequired() default "";

    /**
     * Maximum size of the pulled application, using size units (for example {@code 512mb}, {@code 1gb}).
     */
    String pull_maxSize() default "1gb";

    /**
     * Maximum number of redirect hops allowed while pulling an application.
     */
    int pull_maxRedirects() default 5;

    /**
     * Connection timeout while pulling an application, as ISO-8601 duration.
     */
    String pull_connectTimeout() default "PT10S";

    /**
     * Read timeout while pulling an application, as ISO-8601 duration.
     */
    String pull_readTimeout() default "PT60S";

    /**
     * Legacy alias of {@link #pull_allowedUrls()}.
     */
    String installUrl_allowedUrls() default "https://*";

    /**
     * Legacy alias of {@link #pull_checksumRequired()}.
     */
    boolean installUrl_checksumRequired() default true;
}

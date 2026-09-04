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
     * Legacy alias of {@link #pull_allowedUrls()}.
     */
    String installUrl_allowedUrls() default "https://*";

    /**
     * Legacy alias of {@link #pull_checksumRequired()}.
     */
    boolean installUrl_checksumRequired() default true;
}

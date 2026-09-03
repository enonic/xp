package com.enonic.xp.impl.server.rest;

public @interface AppManagementConfig
{
    /**
     * Marker default meaning "key not present in the cfg file", so an explicitly empty value (disable pull) stays distinguishable.
     */
    String UNSET = "\u0000";

    /**
     * Wildcard patterns of URLs the {@code pull} verb may install from. Empty disables {@code pull}. When absent, falls back to
     * {@link #installUrl_allowedUrls()}.
     */
    String pull_allowedUrls() default UNSET;

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

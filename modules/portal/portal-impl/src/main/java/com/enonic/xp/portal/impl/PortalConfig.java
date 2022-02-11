package com.enonic.xp.portal.impl;

public @interface PortalConfig
{
    String asset_cacheControl() default "public, max-age=31536000, immutable";

    String media_public_cacheControl() default "public, max-age=31536000, immutable";

    String media_private_cacheControl() default "private, max-age=31536000, immutable";

    String page_previewContentSecurityPolicy() default "default-src 'self'; object-src 'none'; img-src: 'self' data:; style-src 'self' 'unsafe-inline'";
}

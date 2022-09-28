package com.enonic.xp.portal.impl;

public @interface PortalConfig
{
    String asset_cacheControl() default "public, max-age=31536000, immutable";

    String media_public_cacheControl() default "public, max-age=31536000, immutable";

    String media_private_cacheControl() default "private, max-age=31536000, immutable";

    String media_contentSecurityPolicy() default "default-src 'none'; base-uri 'none'; form-action 'none'";

    String media_contentSecurityPolicy_svg() default "default-src 'none'; base-uri 'none'; form-action 'none'; style-src 'self' 'unsafe-inline'";
}

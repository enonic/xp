package com.enonic.xp.admin.impl.portal;

public @interface AdminConfig
{
    String site_previewContentSecurityPolicy() default "default-src 'self'; object-src 'none'; img-src 'self' data:; style-src * 'unsafe-inline'; font-src * data:";
}

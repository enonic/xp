package com.enonic.xp.admin.impl.portal;

public @interface AdminConfig
{
    String site_preview_contentSecurityPolicy() default "default-src 'self'; script-src 'self'; object-src 'none'; img-src * data:; style-src * 'unsafe-inline'; font-src * data:'";
}

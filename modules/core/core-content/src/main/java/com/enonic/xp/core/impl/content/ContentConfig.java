package com.enonic.xp.core.impl.content;

public @interface ContentConfig
{
    boolean auditlog_enabled() default true;

    boolean htmlarea_sanitizing_enabled() default false;

    boolean attachments_allowUnsafeNames() default false;

    String auditlog_filter() default "!system.content.update,*";
}

package com.enonic.xp.core.impl.app;

public @interface AppConfig
{
    String filter() default "*";

    boolean auditlog_enabled() default true;

    boolean virtual_enabled() default true;

    boolean virtual_schema_override() default true;

}

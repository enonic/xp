package com.enonic.xp.core.impl.security;

public @interface SecurityConfig
{
    boolean auditlog_enabled() default true;
}

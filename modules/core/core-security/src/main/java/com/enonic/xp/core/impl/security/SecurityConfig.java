package com.enonic.xp.core.impl.security;

public @interface SecurityConfig
{
    boolean auditlog_enabled() default true;

    String password_policy() default "$pbkdf2-sha512$i=210000,l=64,slen=16";
}

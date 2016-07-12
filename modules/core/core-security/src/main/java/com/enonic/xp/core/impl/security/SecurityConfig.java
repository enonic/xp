package com.enonic.xp.core.impl.security;

public @interface SecurityConfig
{
    String suPassword();

    String suPasswordFormat() default "\\{(\\w+)\\}(\\S+)";

    String suUsername() default "su";
}

package com.enonic.xp.core.impl.security;

@FunctionalInterface
public interface PasswordEncoder
{
    String encode( char[] plainPassword );
}

package com.enonic.xp.core.impl.security;

@FunctionalInterface
public interface PasswordValidator
{
    boolean validate( char[] plainPassword );
}

package com.enonic.xp.core.impl.security;

import org.jspecify.annotations.NullMarked;

@FunctionalInterface
@NullMarked
interface PasswordVerifier
{
    boolean verify( char[] plainPassword, String encodedPassword );
}

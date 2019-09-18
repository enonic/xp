package com.enonic.xp.core.impl.security;

interface PasswordEncoder
{
    String encodePassword( final String plainPassword );

    boolean validate( final String plainPassword, final String encodedPassword );

    String getType();

}

package com.enonic.xp.core.impl.security;

interface PasswordEncoder
{
    String encodePassword( String plainPassword );

    boolean validate( String plainPassword, String encodedPassword );

    String getType();

}

package com.enonic.xp.security.impl;

interface PasswordEncoder
{
    public String encodePassword( final String plainPassword );

    public boolean validate( final String plainPassword, final String encodedPassword );

    public String getType();

}

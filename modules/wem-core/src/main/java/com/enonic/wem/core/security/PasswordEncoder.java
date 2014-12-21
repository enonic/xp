package com.enonic.wem.core.security;

public interface PasswordEncoder
{
    public String encodePassword( final String plainPassword );

    public boolean validate( final String plainPassword, final String encodedPassword );

    public String getType();

}

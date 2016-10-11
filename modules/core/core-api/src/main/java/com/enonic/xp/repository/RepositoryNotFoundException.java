package com.enonic.xp.repository;

public class RepositoryNotFoundException
    extends RuntimeException
{
    public RepositoryNotFoundException( final String message )
    {
        super( message );
    }
}

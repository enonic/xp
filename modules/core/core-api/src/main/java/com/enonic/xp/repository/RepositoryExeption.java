package com.enonic.xp.repository;

public class RepositoryExeption
    extends RuntimeException
{
    public RepositoryExeption( final String message )
    {
        super( message );
    }

    public RepositoryExeption( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}

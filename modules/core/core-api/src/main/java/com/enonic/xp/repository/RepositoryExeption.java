package com.enonic.xp.repository;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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

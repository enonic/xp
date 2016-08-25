package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
class RepositoryExeption
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

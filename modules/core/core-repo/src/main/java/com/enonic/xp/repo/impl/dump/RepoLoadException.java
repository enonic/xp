package com.enonic.xp.repo.impl.dump;

public class RepoLoadException
    extends RuntimeException
{
    public RepoLoadException( final String message )
    {
        super( message );
    }

    public RepoLoadException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}

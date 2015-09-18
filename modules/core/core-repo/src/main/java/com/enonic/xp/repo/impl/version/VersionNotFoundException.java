package com.enonic.xp.repo.impl.version;

public class VersionNotFoundException extends RuntimeException
{

    public VersionNotFoundException( final String message )
    {
        super( message );
    }
}

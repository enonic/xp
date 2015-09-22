package com.enonic.wem.repo.internal.version;

public class VersionNotFoundException extends RuntimeException
{

    public VersionNotFoundException( final String message )
    {
        super( message );
    }
}

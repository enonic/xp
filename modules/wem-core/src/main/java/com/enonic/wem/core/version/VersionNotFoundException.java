package com.enonic.wem.core.version;

public class VersionNotFoundException extends RuntimeException
{

    public VersionNotFoundException( final String message )
    {
        super( message );
    }
}

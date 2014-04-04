package com.enonic.wem.core.module.source;

public final class SourceNotFoundException
    extends RuntimeException
{
    public SourceNotFoundException( final String message )
    {
        super( message );
    }
}

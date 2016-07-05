package com.enonic.xp.repo.impl.elasticsearch.snapshot;

public class SnapshotException
    extends RuntimeException
{

    public SnapshotException( final String message )
    {
        super( message );
    }

    public SnapshotException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}

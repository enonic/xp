package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import static java.util.Objects.requireNonNull;

public sealed interface ApplicationIconUpdate
    permits ApplicationIconUpdate.Keep, ApplicationIconUpdate.Remove, ApplicationIconUpdate.Replace
{
    ApplicationIconUpdate KEEP = new Keep();

    ApplicationIconUpdate REMOVE = new Remove();

    static ApplicationIconUpdate replace( final ByteSource data, final String mimeType )
    {
        return new Replace( data, mimeType );
    }

    record Keep()
        implements ApplicationIconUpdate
    {
    }

    record Remove()
        implements ApplicationIconUpdate
    {
    }

    record Replace(ByteSource data, String mimeType)
        implements ApplicationIconUpdate
    {
        public Replace
        {
            requireNonNull( data, "data is required" );
            requireNonNull( mimeType, "mimeType is required" );
        }
    }
}

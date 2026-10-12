package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.util.BinaryReference;

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

    /**
     * The icon to attach to the descriptor node; the binary is named by its mime type ({@code enonic.svg} or {@code enonic.png}),
     * whatever the bundle shipped it as.
     */
    record Replace(ByteSource data, String mimeType)
        implements ApplicationIconUpdate
    {
        public Replace
        {
            requireNonNull( data, "data is required" );
            requireNonNull( mimeType, "mimeType is required" );
            if ( SchemaResourcePaths.appIconBinaryReference( mimeType ) == null )
            {
                throw new IllegalArgumentException( "unsupported icon mime type: " + mimeType );
            }
        }

        public BinaryReference reference()
        {
            return SchemaResourcePaths.appIconBinaryReference( mimeType );
        }
    }
}

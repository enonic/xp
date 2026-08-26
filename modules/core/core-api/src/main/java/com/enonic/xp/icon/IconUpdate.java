package com.enonic.xp.icon;

import com.google.common.io.ByteSource;

import static java.util.Objects.requireNonNull;

/**
 * What becomes of the icon of a descriptor when the descriptor is created or updated: it is kept as it is, removed,
 * or replaced by the given image.
 */
public sealed interface IconUpdate
    permits IconUpdate.Keep, IconUpdate.Remove, IconUpdate.Replace
{
    IconUpdate KEEP = new Keep();

    IconUpdate REMOVE = new Remove();

    /**
     * @param data     the image
     * @param mimeType {@code image/svg+xml} or {@code image/png}
     */
    static IconUpdate replace( final ByteSource data, final String mimeType )
    {
        return new Replace( data, mimeType );
    }

    record Keep()
        implements IconUpdate
    {
    }

    record Remove()
        implements IconUpdate
    {
    }

    record Replace(ByteSource data, String mimeType)
        implements IconUpdate
    {
        public Replace
        {
            requireNonNull( data, "data is required" );
            requireNonNull( mimeType, "mimeType is required" );
        }
    }
}

package com.enonic.xp.lib.schema;

import com.google.common.io.ByteSource;

import com.enonic.xp.icon.IconUpdate;

/**
 * The icon as the JS API passes it to a create-or-update handler: left out it is kept, {@code null} removes it, and
 * {@code {data, mimeType}} replaces it.
 */
final class IconUpdates
{
    private IconUpdates()
    {
    }

    static IconUpdate from( final ByteSource data, final String mimeType, final boolean remove )
    {
        if ( remove )
        {
            return IconUpdate.REMOVE;
        }
        if ( data != null || mimeType != null )
        {
            return IconUpdate.replace( data, mimeType );
        }
        return IconUpdate.KEEP;
    }
}

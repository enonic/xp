package com.enonic.xp.core.node;

import com.enonic.xp.core.blob.BlobKey;
import com.enonic.xp.core.support.AbstractId;

public class NodeVersionId
    extends AbstractId
{
    private NodeVersionId( final String value )
    {
        super( value );
    }

    public static NodeVersionId from( final BlobKey blobKey )
    {
        return new NodeVersionId( blobKey.toString() );
    }

    public static NodeVersionId from( final String value )
    {
        return new NodeVersionId( value );
    }

}

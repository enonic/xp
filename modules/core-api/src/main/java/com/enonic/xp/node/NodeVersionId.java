package com.enonic.xp.node;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.support.AbstractId;

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

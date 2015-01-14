package com.enonic.wem.api.node;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.support.AbstractId;

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

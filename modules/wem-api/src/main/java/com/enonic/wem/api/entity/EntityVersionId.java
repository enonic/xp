package com.enonic.wem.api.entity;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.support.AbstractId;

public class EntityVersionId
    extends AbstractId
{

    public EntityVersionId( final String value )
    {
        super( value );
    }

    public static EntityVersionId from( final BlobKey blobKey )
    {
        return new EntityVersionId( blobKey.toString() );
    }
}

package com.enonic.wem.core.content.data;


import com.enonic.wem.core.content.datatype.DataTypes;

import com.enonic.cms.framework.blob.BlobKey;

public class BlobToKeyReplacer
{
    private BlobKeyResolver blobKeyResolver;

    public BlobToKeyReplacer( final BlobKeyResolver blobKeyResolver )
    {
        this.blobKeyResolver = blobKeyResolver;
    }

    public void replace( ContentData contentData )
    {
        doReplace( contentData );
    }

    private void doReplace( final Iterable<Data> dataIt )
    {
        for ( Data data : dataIt )
        {
            if ( data.getDataType().equals( DataTypes.BLOB ) )
            {
                if ( data.getValue() instanceof byte[] )
                {
                    BlobKey blobKey = blobKeyResolver.resolve( (byte[]) data.getValue() );
                    data.setValue( blobKey );
                }

            }
            else if ( data.isDataSet() )
            {
                doReplace( data.getDataSet() );
            }
        }
    }
}

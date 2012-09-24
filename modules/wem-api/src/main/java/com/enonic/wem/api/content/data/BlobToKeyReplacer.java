package com.enonic.wem.api.content.data;


import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.datatype.DataTypes;


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
            else if ( data.hasDataSetAsValue() )
            {
                doReplace( data.getDataSet() );
            }
        }
    }
}

package com.enonic.wem.api.content.data;


import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.data.type.DataTypes;

import static com.enonic.wem.api.content.data.Value.newValue;


public class BlobToKeyReplacer
{
    private BlobKeyResolver blobKeyResolver;

    public BlobToKeyReplacer( final BlobKeyResolver blobKeyResolver )
    {
        this.blobKeyResolver = blobKeyResolver;
    }

    public void replace( DataSet dataSet )
    {
        doReplace( dataSet );
    }

    private void doReplace( final Iterable<Entry> dataIt )
    {
        for ( Entry entry : dataIt )
        {

            if ( entry.isData() )
            {
                final Data data = entry.toData();
                if ( data.getType().equals( DataTypes.BLOB ) )
                {
                    if ( data.getObject() instanceof byte[] )
                    {
                        final BlobKey blobKey = blobKeyResolver.resolve( (byte[]) data.getObject() );
                        data.setValue( newValue().type( DataTypes.BLOB ).value( blobKey ).build() );
                    }

                }
            }
            else if ( entry.isDataSet() )
            {
                doReplace( entry.toDataSet() );
            }
        }
    }
}

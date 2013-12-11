package com.enonic.wem.core.icon;


import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.icon.Icon;

public class IconDataSerializer
{
    private static final String BLOB_KEY = "blobKey";

    private static final String MIME_TYPE = "mimeType";

    public static DataSet toData( final Icon icon )
    {
        return toData( icon, new DataSet( "icon" ) );
    }

    public static void nullableToData( final Icon icon, final String name, final DataSet parent )
    {
        if ( icon == null )
        {
            return;
        }
        parent.add( toData( icon, new DataSet( name ) ) );
    }

    public static DataSet toData( final Icon icon, final DataSet data )
    {
        data.setProperty( BLOB_KEY, new Value.String( icon.getBlobKey().toString() ) );
        data.setProperty( MIME_TYPE, new Value.String( icon.getMimeType() ) );
        return data;
    }

    public static Icon toIconNullable( final DataSet dataSet )
    {
        if ( dataSet == null )
        {
            return null;
        }
        return toIcon( dataSet );
    }

    public static Icon toIcon( final DataSet dataSet )
    {
        final BlobKey blobKey = new BlobKey( dataSet.getProperty( BLOB_KEY ).getString() );
        final String mimeType = dataSet.getProperty( MIME_TYPE ).getString();
        return Icon.from( blobKey, mimeType );
    }
}

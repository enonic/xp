package com.enonic.wem.api.content;

import org.joda.time.DateMidnight;

import com.enonic.wem.api.content.data.BlobToKeyReplacer;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.MockBlobKeyResolver;
import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.RequiredContractVerifier;
import com.enonic.wem.api.content.type.component.BreaksRequiredContractException;

public class Content
{
    private ContentPath path = new ContentPath();

    private ContentType type;

    private ContentData data = new ContentData();

    public void setPath( final ContentPath path )
    {
        this.path = path;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public ContentType getType()
    {
        return type;
    }

    public void setType( final ContentType type )
    {
        this.type = type;
    }

    public String getName()
    {
        if ( path.hasName() )
        {
            return path.getName();
        }
        else
        {
            return null;
        }
    }

    public void setName( final String name )
    {
        this.path = this.path.withName( name );
    }

    public void setData( final ContentData value )
    {
        this.data = value;
    }

    public ContentData getData()
    {
        return data;
    }

    public void setData( final String path, final String value )
    {
        this.data.setData( new EntryPath( path ), value, DataTypes.TEXT );
    }

    public void setData( final String path, final DateMidnight value )
    {
        this.data.setData( new EntryPath( path ), value, DataTypes.DATE );
    }

    public void setData( final String path, final Long value )
    {
        this.data.setData( new EntryPath( path ), value, DataTypes.WHOLE_NUMBER );
    }

    public void setData( final String path, final Double value )
    {
        this.data.setData( new EntryPath( path ), value, DataTypes.DECIMAL_NUMBER );
    }

    public void setData( final String path, final Object value, DataType dataType )
    {
        this.data.setData( new EntryPath( path ), value, dataType );
    }

    public Data getData( final String path )
    {
        return this.data.getData( new EntryPath( path ) );
    }

    public String getValueAsString( final String path )
    {
        return this.data.getValueAsString( new EntryPath( path ) );
    }

    public DataSet getDataSet( String path )
    {
        return this.data.getDataSet( new EntryPath( path ) );
    }

    public void checkBreaksRequiredContract()
        throws BreaksRequiredContractException
    {
        new RequiredContractVerifier( type ).verify( data );
    }

    public Object getIndexableValues()
    {
        // TODO
        return null;
    }

    public void replaceBlobsWithKeys( final MockBlobKeyResolver blobToKeyResolver )
    {
        new BlobToKeyReplacer( blobToKeyResolver ).replace( data );
    }


    public static Content create( final ContentPath contentPath )
    {
        Content content = new Content();
        content.setPath( contentPath );
        return content;
    }
}

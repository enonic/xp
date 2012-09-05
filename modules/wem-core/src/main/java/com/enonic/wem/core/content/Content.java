package com.enonic.wem.core.content;

import org.joda.time.DateMidnight;

import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.data.DataSet;
import com.enonic.wem.core.content.data.EntryPath;
import com.enonic.wem.core.content.datatype.DataType;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.RequiredContractVerifier;
import com.enonic.wem.core.content.type.configitem.BreaksRequiredContractException;

public class Content
{
    private ContentType type;

    private String name;

    private ContentData data = new ContentData();

    public ContentType getType()
    {
        return type;
    }

    public void setType( final ContentType type )
    {
        this.type = type;
        this.data.setContentType( type );
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    void setData( final ContentData value )
    {
        this.data = value;
    }

    public ContentData getData()
    {
        return data;
    }

    public void setData( final String path, final String value )
    {
        this.data.setData( new EntryPath( path ), value, DataTypes.STRING );
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
}

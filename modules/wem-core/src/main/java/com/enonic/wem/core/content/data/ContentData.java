package com.enonic.wem.core.content.data;


import java.util.Iterator;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.datatype.DataType;
import com.enonic.wem.core.content.type.ContentType;

public class ContentData
    implements EntrySelector, Iterable<Entry>
{
    private DataSet dataSet;

    /**
     * Structured data.
     *
     * @param contentType
     */
    public ContentData( final ContentType contentType )
    {
        this.dataSet = new DataSet( new EntryPath(), contentType.getConfigItems() );
    }

    /**
     * Unstructured data.
     */
    public ContentData()
    {
        this.dataSet = new DataSet( new EntryPath() );
    }

    public void setContentType( final ContentType contentType )
    {
        this.dataSet.setConfigItems( contentType.getConfigItems() );
    }

    void setDataSet( final DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public void setData( final EntryPath path, final Object value, final DataType dataType )
    {
        dataSet.setData( path, value, dataType );
    }

    public void setData( final EntryPath path, final String value )
    {
        dataSet.setData( path, value, null );
    }

    public String getValueAsString( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        Data data = getData( path );
        Preconditions.checkArgument( data != null, "No data at path: " + path );

        //TODO: Preconditions.checkArgument( data.getDataType() == BasalValueType.STRING, "Value is not of type %", BasalValueType.STRING );
        return (String) data.getValue();
    }

    public Data getData( final EntryPath path )
    {
        return dataSet.getData( path );
    }

    public DataSet getDataSet( final EntryPath path )
    {
        return dataSet.getDataSet( path );
    }

    DataSet getDataSet()
    {
        return dataSet;
    }


    @Override
    public Iterator<Entry> iterator()
    {
        return dataSet.iterator();
    }
}

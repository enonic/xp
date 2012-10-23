package com.enonic.wem.api.content.data;


import java.util.Iterator;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;

public class ContentData
    implements EntrySelector, Iterable<Data>
{
    private DataSet dataSet;

    public ContentData()
    {
        this.dataSet = new DataSet( new EntryPath() );
    }

    public void setDataSet( final DataSet dataSet )
    {
        Preconditions.checkArgument( dataSet.getPath().elementCount() == 0, "Expected dataSet without path: " + dataSet.getPath() );
        this.dataSet = dataSet;
    }

    public void setData( final EntryPath path, final Object value, final DataType dataType )
    {
        dataSet.setData( path, value, (BaseDataType) dataType );
    }

    public void setData( final EntryPath path, final String value )
    {
        dataSet.setData( path, value, DataTypes.TEXT );
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

    @Override
    public Iterator<Data> iterator()
    {
        return dataSet.iterator();
    }
}

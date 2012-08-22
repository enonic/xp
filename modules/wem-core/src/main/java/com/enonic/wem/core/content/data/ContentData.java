package com.enonic.wem.core.content.data;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.datatype.BasalValueType;

public class ContentData
{
    private DataSet dataSet;

    /**
     * Structured data.
     *
     * @param configItems
     */
    public ContentData( final ConfigItems configItems )
    {
        this.dataSet = new DataSet( new EntryPath(), configItems );
    }

    /**
     * Unstructured data.
     */
    public ContentData()
    {
        this.dataSet = new DataSet( new EntryPath() );
    }

    public void setConfigItems( final ConfigItems configItems )
    {
        this.dataSet.setConfigItems( configItems );
    }

    void setDataSet( final DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public void setData( final EntryPath path, final Object value )
    {
        dataSet.setData( path, value );
    }

    public void setData( final EntryPath path, final String value )
    {
        dataSet.setData( path, value );
    }

    public void setData( final String path, final Object value )
    {
        dataSet.setData( new EntryPath( path ), value );
    }

    public void setData( final String path, final FieldSet value )
    {
        dataSet.setData( new EntryPath( path ), value );
    }

    public Data setData( final String path )
    {
        return dataSet.getData( new EntryPath( path ) );
    }

    public String getValueAsString( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        Data data = getData( path );
        Preconditions.checkArgument( data != null, "No data at path: " + path );

        Preconditions.checkArgument( data.getBasalValueType() == BasalValueType.STRING, "Value is not of type %", BasalValueType.STRING );
        return (String) data.getValue();
    }

    public Data getData( final EntryPath path )
    {
        return dataSet.getData( path );
    }

    public DataSet getDataSet( final String path )
    {
        return getDataSet( new EntryPath( path ) );
    }

    public DataSet getDataSet( final EntryPath path )
    {
        return dataSet.getDataSet( path );
    }

    DataSet getDataSet()
    {
        return dataSet;
    }

    public boolean breaksRequiredContract()
    {
        return dataSet.breaksRequiredContract();
    }

    public void checkBreaksRequiredContract()
    {
        dataSet.breaksRequiredContract();
    }
}

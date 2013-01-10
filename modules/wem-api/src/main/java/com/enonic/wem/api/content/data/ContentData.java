package com.enonic.wem.api.content.data;


import java.util.Iterator;

import com.enonic.wem.api.content.datatype.DataTypes;

import static com.enonic.wem.api.content.data.Value.newValue;

public final class ContentData
    implements Iterable<Entry>
{
    private DataSet dataSet;

    public ContentData()
    {
        this.dataSet = DataSet.newRootDataSet();
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void add( final Entry entry )
    {
        dataSet.add( entry );
    }

    public void setData( final EntryPath path, final Value value )
    {
        dataSet.setData( path, value );
    }

    public void setData( final EntryPath path, final String value )
    {
        dataSet.setData( path, newValue().type( DataTypes.TEXT ).value( value ).build() );
    }

    public Entry getEntry( final EntryPath path )
    {
        return dataSet.getEntry( path );
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
    public Iterator<Entry> iterator()
    {
        return dataSet.iterator();
    }
}

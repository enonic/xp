package com.enonic.wem.core.content.data;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.datatype.BaseDataType;
import com.enonic.wem.core.content.datatype.DataTypes;

public class DataSet
    implements Iterable<Data>, EntrySelector
{
    private EntryPath path;

    private LinkedHashMap<EntryPath.Element, Data> entries = new LinkedHashMap<EntryPath.Element, Data>();

    public DataSet( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.path = path;
    }

    public EntryPath getPath()
    {
        return path;
    }

    void add( Data data )
    {
        entries.put( data.getPath().getLastElement(), data );
    }

    void setData( final EntryPath path, final Object value, final BaseDataType dataType )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            forwardSetDataToDataSet( path, value, dataType );
        }
        else
        {
            final EntryPath newEntryPath = new EntryPath( this.path, path.getFirstElement() );
            final Data newData = Data.newData().path( newEntryPath ).type( dataType ).value( value ).build();
            doSetData( path.getFirstElement(), newData );
        }
    }

    private void forwardSetDataToDataSet( final EntryPath path, final Object value, final BaseDataType dataType )
    {
        Data existingDataWithDataSetValue = this.entries.get( path.getFirstElement() );
        if ( existingDataWithDataSetValue == null )
        {
            final EntryPath newEntryPath = new EntryPath( this.path, path.getFirstElement() );
            existingDataWithDataSetValue =
                Data.newData().path( newEntryPath ).type( DataTypes.DATA_SET ).value( new DataSet( newEntryPath ) ).build();
            doSetData( path.getFirstElement(), existingDataWithDataSetValue );
        }
        existingDataWithDataSetValue.setData( path.asNewWithoutFirstPathElement(), value, dataType );
    }

    private void doSetData( EntryPath.Element element, Data data )
    {
        entries.put( element, data );
    }

    public Data getData( final String path )
    {
        return getData( new EntryPath( path ) );
    }

    public Data getData( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            return forwardGetDataToDataSet( path );
        }
        else
        {
            return doGetData( path );
        }
    }

    public DataSet getDataSet( final EntryPath path )
    {
        Preconditions.checkArgument( path.elementCount() > 0, "path must be something: " + path );

        if ( path.elementCount() == 1 )
        {
            final Data data = entries.get( path.getLastElement() );
            if ( data == null )
            {
                return null;
            }
            if ( !( data.getDataType().equals( DataTypes.DATA_SET ) ) )
            {
                throw new IllegalArgumentException( "Data at path [%s] is not of type DataSet: " + data.getDataType() );
            }
            return (DataSet) data.getValue();
        }
        else
        {
            final Data data = entries.get( path.getFirstElement() );
            if ( !( data.getDataType().equals( DataTypes.DATA_SET ) ) )
            {
                throw new IllegalArgumentException( "Data at path [%s] is not of type DataSet: " + data.getDataType() );
            }
            return data.getDataSet( path.asNewWithoutFirstPathElement() );
        }
    }


    private Data forwardGetDataToDataSet( final EntryPath path )
    {
        final Data foundData = entries.get( path.getFirstElement() );
        if ( foundData == null )
        {
            return null;
        }

        if ( !( foundData.getValue() instanceof DataSet ) )
        {
            throw new IllegalArgumentException(
                "Data at path [" + this.getPath() + "] expected to have a value of type DataSet: " + foundData.getDataType().getName() );
        }

        final DataSet dataSet = (DataSet) foundData.getValue();
        return dataSet.getData( path.asNewWithoutFirstPathElement() );
    }

    private Data doGetData( final EntryPath path )
    {
        Preconditions.checkArgument( path.elementCount() == 1, "path expected to contain only one element: " + path );

        final Data data = entries.get( path.getLastElement() );
        if ( data == null )
        {
            return null;
        }

        return data;
    }

    public Iterator<Data> iterator()
    {
        return entries.values().iterator();
    }

    public int size()
    {
        return entries.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( path.toString() );
        s.append( ": " );
        int index = 0;
        final int size = entries.size();
        for ( Data data : entries.values() )
        {
            s.append( data.getPath().getLastElement() );
            if ( index < size - 1 )
            {
                s.append( ", " );
            }
            index++;
        }
        return s.toString();
    }
}

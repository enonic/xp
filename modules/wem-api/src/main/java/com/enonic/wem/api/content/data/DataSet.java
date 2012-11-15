package com.enonic.wem.api.content.data;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.component.InvalidDataException;

import static com.enonic.wem.api.content.data.Data.newData;

public final class DataSet
    implements Iterable<Data>, EntrySelector
{
    private EntryPath path;

    private DataEntries entries;

    public DataSet( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        this.path = path;
        entries = new DataEntries();
    }

    void setEntryPathIndex( final EntryPath path, final int index )
    {
        this.path = this.path.asNewWithIndexAtPath( index, path );
        for ( Data data : entries )
        {
            data.setEntryPathIndex( path, index );
        }
    }

    public EntryPath getPath()
    {
        return path;
    }

    public void add( final Data data )
    {
        entries.add( data );
    }

    void setData( final EntryPath path, final Object value, final BaseDataType dataType )
        throws InvalidDataException
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
            entries.setData( newEntryPath, value, dataType );
        }
    }

    private void forwardSetDataToDataSet( final EntryPath path, final Object value, final BaseDataType dataType )
    {
        final DataSet dataSet = findOrCreateDataSet( path.getFirstElement() );
        dataSet.setData( path.asNewWithoutFirstPathElement(), value, dataType );
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
            return doGetData( path.getLastElement() );
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
                throw new IllegalArgumentException( "Data at path [" + data.getPath() + "] is not of type DataSet: " + data.getDataType() );
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

    private DataSet findOrCreateDataSet( final EntryPath.Element firstElement )
    {
        final DataSet dataSet;
        Data exData = this.entries.get( firstElement );
        if ( exData == null )
        {
            // create new set
            final EntryPath pathToDataSet = new EntryPath( this.path, firstElement );
            dataSet = new DataSet( pathToDataSet );
            entries.setData( pathToDataSet, dataSet, DataTypes.DATA_SET );
        }
        else
        {
            if ( exData.getDataType() == DataTypes.DATA_ARRAY )
            {
                final DataArray dataArray = exData.getDataArray();
                final Data data = dataArray.getData( firstElement.getIndex() );
                if ( data == null )
                {
                    final EntryPath newPath = new EntryPath( exData.getPath(), firstElement.getIndex() );
                    dataSet = new DataSet( newPath );
                    dataArray.set( firstElement.getIndex(), dataSet, DataTypes.DATA_SET );
                }
                else
                {
                    dataSet = data.getDataSet();
                }
            }
            else
            {
                dataSet = exData.getDataSet();
            }
        }
        return dataSet;
    }

    private Data forwardGetDataToDataSet( final EntryPath path )
    {
        Data data = entries.get( path.getFirstElement() );
        if ( data == null )
        {
            return null;
        }

        if ( data.getDataType().equals( DataTypes.DATA_ARRAY ) )
        {
            if ( path.getFirstElement().hasIndex() )
            {
                data = data.getDataArray().getData( path.getFirstElement().getIndex() );
            }
            else
            {
                data = data.getDataArray().getData( 0 );
            }
        }

        if ( !data.hasDataSetAsValue() )
        {
            throw new IllegalArgumentException(
                "Data at path [" + this.getPath() + "] expected to have a value of type DataSet: " + data.getDataType().getName() );
        }

        return data.getDataSet().getData( path.asNewWithoutFirstPathElement() );
    }

    private Data doGetData( final EntryPath.Element element )
    {
        final Data data = entries.get( element );
        if ( data == null )
        {
            return null;
        }

        // TODO: Try move this logic into entries.get....
        if ( element.hasIndex() )
        {
            final DataType dataType = data.getDataType();
            if ( dataType == DataTypes.DATA_ARRAY )
            {
                DataArray array = data.getDataArray();
                return array.getData( element.getIndex() );
            }
            else if ( dataType == DataTypes.DATA_ARRAY )
            {
                // allow returning of single data, if first element of an array was requested
                if ( element.getIndex() == 0 )
                {
                    return data;
                }
                else
                {
                    throw new IllegalArgumentException(
                        "Data at path [" + new EntryPath( path, element.getName() ) + "] is not an array: " +
                            new EntryPath( path, element ) );
                }
            }
        }

        return data;
    }

    public Iterator<Data> iterator()
    {
        return entries.iterator();
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
        s.append( " { " );
        int index = 0;
        final int size = entries.size();
        for ( Data data : entries )
        {
            s.append( data.getPath().getLastElement() );
            if ( index < size - 1 )
            {
                s.append( ", " );
            }
            index++;
        }
        s.append( " }" );
        return s.toString();
    }

    public static Builder newDataSet()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<Data> dataList = new ArrayList<Data>();

        public Builder set( String path, Object value, DataType dataType )
        {
            dataList.add( newData().path( new EntryPath( path ) ).value( value ).type( dataType ).build() );
            return this;
        }

        public DataSet build()
        {
            DataSet dataSet = new DataSet( new EntryPath() );

            for ( Data data : dataList )
            {
                dataSet.add( data );
            }

            return dataSet;
        }
    }
}

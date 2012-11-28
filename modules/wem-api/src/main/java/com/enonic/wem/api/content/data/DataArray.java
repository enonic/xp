package com.enonic.wem.api.content.data;


import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.datatype.DataType;

public final class DataArray
    implements Iterable<Data>
{
    private EntryPath path;

    private final ArrayList<Data> list = new ArrayList<Data>();

    private final DataType type;

    public DataArray( final EntryPath path, final DataType dataType )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( dataType, "dataType cannot be null" );
        Preconditions.checkArgument( !path.getLastElement().hasIndex(),
                                     "Last path element of a DataArray must not contain index: " + path );
        this.path = path;
        this.type = dataType;
    }

    void setEntryPathIndex( final EntryPath path, final int index )
    {
        this.path = this.path.asNewWithIndexAtPath( index, path );
        for ( Data data : list )
        {
            data.setEntryPathIndex( path, index );
        }
    }

    public EntryPath getPath()
    {
        return path;
    }

    public DataType getType()
    {
        return type;
    }

    public int size()
    {
        return list.size();
    }

    @Override
    public Iterator<Data> iterator()
    {
        return list.iterator();
    }

    public Data getData( final int i )
    {
        if ( i > list.size() - 1 )
        {
            return null;
        }
        return list.get( i );
    }

    public Data add( final Object value )
    {
        final int index = list.size();
        final Data data = Data.newData().path( new EntryPath( path, index ) ).value( value ).type( type ).build();
        checkType( data );
        if ( data.hasDataSetAsValue() )
        {
            data.setEntryPathIndex( data.getDataSet().getPath(), index );
        }

        list.add( data );
        return data;
    }

    public void add( final Data data )
    {
        checkType( data );
        final EntryPath dataPath = data.getPath();
        Preconditions.checkArgument( new EntryPath( dataPath.getParent(), dataPath.getLastElement().getName() ).equals( path ),
                                     "Data added to array [%s] does not have same path: %s", this.path, data.getPath() );

        if ( dataPath.getLastElement().hasIndex() )
        {
            final int index = dataPath.getLastElement().getIndex();
            checkIndexIsSuccessive( index, data.getValue() );
        }

        data.setEntryPathIndex( dataPath, list.size() );
        list.add( data );
    }

    public Data set( final int index, final Object value )
    {
        Data newData = Data.newData().path( new EntryPath( path, index ) ).value( value ).type( type ).build();

        if ( overwritesExisting( index ) )
        {
            list.set( index, newData );
        }
        else
        {
            checkIndexIsSuccessive( index, value );
            list.add( newData );
        }

        return newData;
    }

    public void set( final EntryPath path, final Object value )
    {
        if ( overwritesExisting( path.getFirstElement().getIndex() ) )
        {
            final Data data = list.get( path.getFirstElement().getIndex() );
            if ( path.elementCount() > 1 )
            {
                data.getDataSet().setData( path.asNewWithoutFirstPathElement(), value, type );
            }
            else
            {
                data.setValue( value );
            }
        }
        else
        {
            checkIndexIsSuccessive( path.getFirstElement().getIndex(), value );

            final Data data = Data.newData().path( path ).value( value ).type( type ).build();
            checkType( data );
            list.add( data );
        }
    }

    private void checkType( Data data )
    {
        if ( !this.type.equals( data.getDataType() ) )
        {
            throw new IllegalArgumentException(
                "DataArray [" + this.path + "] expects data of type [" + this.type + "]. Data was of type: " + data.getDataType() );
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( path );
        s.append( " [ " );
        for ( int i = 0; i < list.size(); i++ )
        {
            final Data data = list.get( i );
            s.append( data );
            if ( i < list.size() - 1 )
            {
                s.append( ", " );
            }
        }
        s.append( " ]" );
        return s.toString();
    }

    private void checkIndexIsSuccessive( final int index, final Object value )
    {
        Preconditions.checkArgument( index == list.size(),
                                     "Data [value=%s] not added successively to array [%s] with size %s. Data had unexpected index: %s",
                                     value, path, list.size(), index );
    }

    private boolean overwritesExisting( int index )
    {
        return index < list.size();
    }

}

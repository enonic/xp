package com.enonic.wem.api.content.data;


import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;

public final class DataArray
    implements Iterable<Data>
{
    private EntryPath path;

    private ArrayList<Data> list = new ArrayList<Data>();

    /**
     * TODO: Type should to be initialized in constructor.
     */
    private DataType type;

    public DataArray( final EntryPath path )
    {
        Preconditions.checkArgument( !path.getLastElement().hasIndex(),
                                     "Last path element of a DataArray must not contain index: " + path );
        this.path = path;
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

    public void add( final Data data )
    {
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
        registerType();
        checkType( data );
    }

    public void setData( final Data data )
    {
        Preconditions.checkArgument( data.getPath().getLastElement().hasIndex(),
                                     "Cannot set data in array[" + path + "] without index: " + data.getPath() );
        list.add( data );
        registerType();
        checkType( data );
    }

    public Data set( final int index, final Object value, final BaseDataType dataType )
    {
        Data newData = Data.newData().path( new EntryPath( path, index ) ).value( value ).type( dataType ).build();

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

    public void set( final EntryPath path, final Object value, final BaseDataType dataType )
    {
        if ( overwritesExisting( path.getFirstElement().getIndex() ) )
        {
            final Data data = list.get( path.getFirstElement().getIndex() );
            if ( path.elementCount() > 1 )
            {
                data.getDataSet().setData( path.asNewWithoutFirstPathElement(), value, dataType );
            }
            else
            {
                data.setValue( value );
            }
        }
        else
        {
            checkIndexIsSuccessive( path.getFirstElement().getIndex(), value );

            final Data data = Data.newData().path( path ).value( value ).type( dataType ).build();
            checkType( data );
            list.add( data );
        }

        registerType();
    }

    private void registerType()
    {
        if ( list.size() == 1 )
        {
            this.type = list.get( 0 ).getDataType();
            Preconditions.checkArgument( !this.type.equals( DataTypes.ARRAY ), "Multidimensional arrays are not supported" );
        }
    }

    private void checkType( Data data )
    {
        if ( !this.type.equals( data.getDataType() ) )
        {
            throw new IllegalArgumentException(
                "Data [" + data.getPath() + "] with type [" + data.getDataType() + "] in array [" + this.path +
                    "] is not of this array's type: " + this.type );
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

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

    public void add( final Data data )
    {
        data.setEntryPathIndex( data.getPath(), list.size() );
        list.add( data );
        registerType();
        checkType( data );
    }

    public void setData( final Data data )
    {
        list.add( data );
        registerType();
        checkType( data );
    }

    public Data getData( final int i )
    {
        return list.get( i );
    }

    public void set( final EntryPath path, final Object value, final BaseDataType dataType )
    {
        boolean overwriteExisting = path.getFirstElement().getIndex() < list.size();
        if ( overwriteExisting )
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
            Preconditions.checkPositionIndex( path.getFirstElement().getIndex(), list.size() );
            if ( path.elementCount() > 1 )
            {
                EntryPath newDataSetPath = new EntryPath( this.path, path.getFirstElement().getIndex() );
                DataSet newDataSet = new DataSet( newDataSetPath );
                Data newDataWithSet = Data.newData().type( DataTypes.DATA_SET ).path( newDataSetPath ).value( newDataSet ).build();
                checkType( newDataWithSet );
                list.add( newDataWithSet );
                newDataWithSet.getDataSet().setData( path.asNewWithoutFirstPathElement(), value, dataType );
            }
            else
            {
                final Data data = Data.newData().path( path ).value( value ).type( dataType ).build();
                checkType( data );
                list.add( data );
            }
        }

        registerType();
    }

    private void registerType()
    {
        if ( list.size() == 1 )
        {
            this.type = list.get( 0 ).getDataType();
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
}

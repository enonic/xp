package com.enonic.wem.api.content.data;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;

import static com.enonic.wem.api.content.data.Data.newData;

final class DataEntries
    implements Iterable<Data>
{
    private LinkedHashMap<String, Data> dataByName = new LinkedHashMap<String, Data>();

    DataEntries()
    {

    }

    void add( final Data data )
    {
        final EntryPath.Element lastElement = data.getPath().getLastElement();
        final String key = resolveKey( lastElement );

        Data exData = dataByName.get( key );
        if ( exData == null )
        {
            dataByName.put( key, data );
        }
        else if ( exData.getDataType() == DataTypes.ARRAY )
        {
            DataArray array = exData.getDataArray();
            array.add( data );
        }
        else
        {
            final DataArray array = new DataArray( exData.getPath(), exData.getDataType() );
            array.add( exData.getValue() );
            array.add( data );
            final Data newDataWithArray = newData().path( array.getPath() ).type( DataTypes.ARRAY ).value( array ).build();

            dataByName.put( key, newDataWithArray );
        }
    }

    void setData( final EntryPath path, final Object value, final DataType type )
    {
        final String key = resolveKey( path.getLastElement() );
        final Data exData = dataByName.get( key );
        final EntryPath pathWithoutIndexAtLastElement = path.asNewWithoutIndexAtLastPathElement();
        if ( exData == null )
        {
            if ( path.getLastElement().hasIndex() )
            {
                final DataArray dataArray = new DataArray( pathWithoutIndexAtLastElement, type );
                dataArray.set( 0, value );
                final Data newDataWithArray =
                    newData().path( pathWithoutIndexAtLastElement ).type( DataTypes.ARRAY ).value( dataArray ).build();
                dataByName.put( key, newDataWithArray );
            }
            else
            {
                dataByName.put( key, newData().path( path ).type( type ).value( value ).build() );
            }
        }
        else if ( exData.getDataType().equals( DataTypes.ARRAY ) )
        {
            exData.getDataArray().set( path.getLastElement().getIndex(), value );
        }
        else if ( path.getLastElement().hasIndex() )
        {
            final DataArray array = new DataArray( pathWithoutIndexAtLastElement, exData.getDataType() );
            array.add( exData.getValue() );
            array.set( path.getLastElement().getIndex(), value );
            final Data newDataWithArray = newData().path( pathWithoutIndexAtLastElement ).type( DataTypes.ARRAY ).value( array ).build();

            dataByName.put( key, newDataWithArray );
        }
        else
        {
            dataByName.put( key, newData().path( path ).type( type ).value( value ).build() );
        }
    }

    Data get( final EntryPath.Element element )
    {
        return dataByName.get( resolveKey( element ) );
    }

    int size()
    {
        return dataByName.size();
    }

    public Iterator<Data> iterator()
    {
        return dataByName.values().iterator();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        for ( Data data : this )
        {
            if ( data.getDataType() == DataTypes.ARRAY )
            {

            }
            else
            {
                s.append( data.getPath() ).append( " = " ).append( data.getString() ).append( "\n" );
            }
        }
        return s.toString();
    }

    private String resolveKey( EntryPath.Element element )
    {
        return element.getName();
    }
}

package com.enonic.wem.api.content.data;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.enonic.wem.api.content.datatype.BaseDataType;
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
        else if ( data.getDataType() == DataTypes.DATA_ARRAY )
        {
            DataArray array = data.getDataArray();
            array.setData( data );
        }
        else
        {
            final DataArray array = new DataArray( data.getPath() );
            array.add( newData().path( exData.getPath() ).type( exData.getDataType() ).value( exData.getValue() ).build() );
            array.add( newData().path( data.getPath() ).type( data.getDataType() ).value( data.getValue() ).build() );
            final Data newDataWithArray = newData().path( data.getPath() ).type( DataTypes.DATA_ARRAY ).value( array ).build();

            dataByName.put( key, newDataWithArray );
        }
    }

    void setData( final EntryPath path, final Object value, final BaseDataType type )
    {
        final String key = resolveKey( path.getLastElement() );
        Data exData = dataByName.get( key );
        final EntryPath pathWithoutIndexAtLastElement = path.asNewWithoutIndexAtPath( path );
        final Data createdData;
        if ( exData == null )
        {
            if ( path.getLastElement().hasIndex() )
            {
                final DataArray dataArray = new DataArray( pathWithoutIndexAtLastElement );
                createdData = newData().path( path ).type( type ).value( value ).build();
                dataArray.add( createdData );
                final Data newDataWithArray =
                    newData().path( pathWithoutIndexAtLastElement ).type( DataTypes.DATA_ARRAY ).value( dataArray ).build();
                dataByName.put( key, newDataWithArray );
            }
            else
            {
                createdData = newData().path( path ).type( type ).value( value ).build();
                dataByName.put( key, createdData );
            }
        }
        else if ( exData.getDataType() == DataTypes.DATA_ARRAY )
        {
            DataArray dataArray = exData.getDataArray();
            createdData = newData().path( path ).type( type ).value( value ).build();
            dataArray.add( createdData );
        }
        else
        {
            final DataArray array = new DataArray( pathWithoutIndexAtLastElement );
            EntryPath exDataPathWithIndex = exData.getPath().asNewWithIndexAtPath( 0, exData.getPath() );
            array.add( newData().path( exDataPathWithIndex ).type( exData.getDataType() ).value( exData.getValue() ).build() );
            array.set( path, value, type );
            final Data newDataWithArray =
                newData().path( pathWithoutIndexAtLastElement ).type( DataTypes.DATA_ARRAY ).value( array ).build();

            dataByName.put( key, newDataWithArray );
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
            if ( data.getDataType() == DataTypes.DATA_ARRAY )
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

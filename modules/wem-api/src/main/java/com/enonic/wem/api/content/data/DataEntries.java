package com.enonic.wem.api.content.data;


import java.util.Iterator;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;

import com.enonic.wem.api.content.datatype.DataType;

class DataEntries
    implements Iterable<Data>
{
    private LinkedListMultimap<String, Data> entries = LinkedListMultimap.create();

    void add( final Data data )
    {
        final String key = resolveKey( data.getPath().getLastElement() );
        final List<Data> list = entries.get( key );

        checkNewEntryAreOfSameTypeAsRest( data, list );

        if ( list.size() == 1 )
        {
            list.get( 0 ).setEntryPathIndex( data.getPath(), 0 );
        }
        if ( list.size() > 0 )
        {
            data.setEntryPathIndex( data.getPath(), list.size() );
        }

        list.add( data );
        //entries.put( key, data );
    }

    void setData( final EntryPath.Element element, final Data data )
    {
        final List<Data> list = entries.get( resolveKey( data.getPath().getLastElement() ) );
        //Preconditions.checkPositionIndex( data.getL )
        checkNewEntryAreOfSameTypeAsRest( data, list );
        entries.put( resolveKey( element ), data );
    }

    Data get( EntryPath.Element element )
    {
        List<Data> list = entries.get( resolveKey( element ) );
        if ( list.isEmpty() )
        {
            return null;
        }
        int index = element.hasIndex() ? element.getIndex() : 0;
        if ( list.size() - 1 < index )
        {
            return null;
        }
        return list.get( index );
    }

    int size()
    {
        return entries.size();
    }

    public Iterator<Data> iterator()
    {
        return entries.values().iterator();
    }

    private String resolveKey( EntryPath.Element element )
    {
        return element.getName();
    }

    private void checkNewEntryAreOfSameTypeAsRest( final Data newEntry, final List<Data> list )
    {
        if ( list.size() > 0 )
        {
            Data previousData = list.get( list.size() - 1 );
            DataType dataTypeOfNewEntry = newEntry.getDataType();
            DataType dataTypeOfPreviousEntry = previousData.getDataType();
            if ( !dataTypeOfNewEntry.equals( dataTypeOfPreviousEntry ) )
            {
                throw new IllegalArgumentException(
                    "Array [" + previousData.getPath() + "] is of type [" + dataTypeOfPreviousEntry.getName() +
                        "] got: " + dataTypeOfNewEntry.getName() );
            }
        }
    }
}

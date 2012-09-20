package com.enonic.wem.core.content.data;


import java.util.Iterator;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;

class DataEntries
    implements Iterable<Data>
{
    private LinkedListMultimap<String, Data> entries = LinkedListMultimap.create();

    void add( Data data )
    {
        List<Data> list = entries.get( resolveKey( data.getPath().getLastElement() ) );
        if ( list.size() == 1 )
        {
            list.get( 0 ).setEntryPathIndex( data.getPath(), 0 );
        }
        if ( list.size() > 0 )
        {
            data.setEntryPathIndex( data.getPath(), list.size() );
        }
        list.add( data );
    }

    void setData( EntryPath.Element element, Data data )
    {
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

    private String resolveKey( EntryPath.Element element )
    {
        return element.getName();
    }

    public Iterator<Data> iterator()
    {
        return entries.values().iterator();
    }
}

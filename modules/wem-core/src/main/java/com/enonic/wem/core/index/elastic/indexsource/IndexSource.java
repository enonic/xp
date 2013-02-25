package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

public class IndexSource
{
    private final Set<IndexSourceEntry> indexSourceEntries = Sets.newHashSet();

    public void addIndexSourceEntry( final IndexSourceEntry indexSourceEntry )
    {
        indexSourceEntries.add( indexSourceEntry );
    }

    public void addIndexSourceEntries( final Collection<IndexSourceEntry> indexSourceEntries )
    {
        this.indexSourceEntries.addAll( indexSourceEntries );
    }

    public Set<IndexSourceEntry> getIndexSourceEntries()
    {
        return indexSourceEntries;
    }

    public IndexSourceEntry getIndexSourceEntryWithName( final String name )
    {
        for ( IndexSourceEntry indexSourceEntry : indexSourceEntries )
        {
            if ( indexSourceEntry.getKey().equals( name ) )
            {
                return indexSourceEntry;
            }
        }

        return null;
    }

}

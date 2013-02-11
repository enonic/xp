package com.enonic.wem.core.search.facet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Facet
    implements Iterable<FacetEntry>
{
    private final String name;

    private final List<FacetEntry> entries;

    public Facet( String name )
    {
        this.name = name;
        this.entries = new ArrayList<FacetEntry>();
    }

    public String getName()
    {
        return name;
    }

    public int getCount()
    {
        return this.entries.size();
    }

    public void addEntry( FacetEntry entry )
    {
        this.entries.add( entry );
    }

    public Iterator<FacetEntry> iterator()
    {
        return this.entries.iterator();
    }

    /**
     * Aggregates the terms that are equal but with different case (e.g. "enonic" and "Enonic") into one with the total count.
     */
    public void consolidate()
    {
        final Map<String, Set<FacetEntry>> entriesTable = new HashMap<String, Set<FacetEntry>>();
        // group by term, case insensitive
        boolean duplicateFound = false;
        for ( FacetEntry entry : entries )
        {
            final String key = entry.getTerm().toLowerCase().trim();
            Set<FacetEntry> termEntries = entriesTable.get( key );
            if ( termEntries == null )
            {
                termEntries = new HashSet<FacetEntry>();
                entriesTable.put( key, termEntries );
            }
            else
            {
                duplicateFound = true;
            }
            termEntries.add( entry );
        }
        if ( !duplicateFound )
        {
            return;
        }

        // aggregate terms with same text but different case
        final List<FacetEntry> newEntries = new ArrayList<FacetEntry>();
        for ( String key : entriesTable.keySet() )
        {
            final Set<FacetEntry> entries = entriesTable.get( key );
            if ( entries.size() == 1 )
            {
                newEntries.add( entries.iterator().next() );
            }
            else
            {
                int max = -1;
                int totalCount = 0;
                String termText = null;
                for ( FacetEntry facetEntry : entries )
                {
                    // take the term text with higher count, in case they are equal take the one with more capital letters
                    if ( facetEntry.getCount() > max )
                    {
                        termText = facetEntry.getTerm();
                        max = facetEntry.getCount();
                    }
                    else if ( ( facetEntry.getCount() == max ) && ( facetEntry.getTerm().compareTo( termText ) < 0 ) )
                    {
                        termText = facetEntry.getTerm();
                    }
                    totalCount += facetEntry.getCount();
                }
                newEntries.add( new FacetEntry( termText, totalCount ) );
            }
        }
        this.entries.clear();
        this.entries.addAll( newEntries );
    }
}

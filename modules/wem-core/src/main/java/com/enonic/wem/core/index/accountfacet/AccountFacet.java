package com.enonic.wem.core.index.accountfacet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Deprecated
public final class AccountFacet
    implements Iterable<AccountFacetEntry>
{
    private final String name;

    private final List<AccountFacetEntry> entries;

    public AccountFacet( String name )
    {
        this.name = name;
        this.entries = new ArrayList<AccountFacetEntry>();
    }

    public String getName()
    {
        return name;
    }

    public int getCount()
    {
        return this.entries.size();
    }

    public void addEntry( AccountFacetEntry entry )
    {
        this.entries.add( entry );
    }

    public Iterator<AccountFacetEntry> iterator()
    {
        return this.entries.iterator();
    }

    /**
     * Aggregates the terms that are equal but with different case (e.g. "enonic" and "Enonic") into one with the total count.
     */
    public void consolidate()
    {
        final Map<String, Set<AccountFacetEntry>> entriesTable = new HashMap<String, Set<AccountFacetEntry>>();
        // group by term, case insensitive
        boolean duplicateFound = false;
        for ( AccountFacetEntry entry : entries )
        {
            final String key = entry.getTerm().toLowerCase().trim();
            Set<AccountFacetEntry> termEntries = entriesTable.get( key );
            if ( termEntries == null )
            {
                termEntries = new HashSet<AccountFacetEntry>();
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
        final List<AccountFacetEntry> newEntries = new ArrayList<AccountFacetEntry>();
        for ( String key : entriesTable.keySet() )
        {
            final Set<AccountFacetEntry> entries = entriesTable.get( key );
            if ( entries.size() == 1 )
            {
                newEntries.add( entries.iterator().next() );
            }
            else
            {
                int max = -1;
                int totalCount = 0;
                String termText = null;
                for ( AccountFacetEntry accountFacetEntry : entries )
                {
                    // take the term text with higher count, in case they are equal take the one with more capital letters
                    if ( accountFacetEntry.getCount() > max )
                    {
                        termText = accountFacetEntry.getTerm();
                        max = accountFacetEntry.getCount();
                    }
                    else if ( ( accountFacetEntry.getCount() == max ) && ( accountFacetEntry.getTerm().compareTo( termText ) < 0 ) )
                    {
                        termText = accountFacetEntry.getTerm();
                    }
                    totalCount += accountFacetEntry.getCount();
                }
                newEntries.add( new AccountFacetEntry( termText, totalCount ) );
            }
        }
        this.entries.clear();
        this.entries.addAll( newEntries );
    }
}

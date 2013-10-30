package com.enonic.wem.core.index.document;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.core.index.IndexType;

@Deprecated
public class IndexDocument
{
    /**
     * An index document contains all properties to be indexed.
     * All properties have a IndexDocumentBaseType, which again is used to determine the fieldname, the value-formatting etc
     * when doing the actual indexing.
     */


    private final String id;

    private final IndexType indexType;

    private final String index;

    private final Set<IndexDocumentEntry> indexDocumentEntries = Sets.newHashSet();

    private boolean refreshOnStore = false;

    public IndexDocument( final String id, final IndexType indexType, final String index )
    {
        this.id = id;
        this.indexType = indexType;
        this.index = index;
    }

    public void addDocumentEntry( final String key, final Object value, boolean includeInAllField, boolean includeOrderBy )
    {
        indexDocumentEntries.add( new IndexDocumentEntry( key, value, includeInAllField, includeOrderBy ) );
    }

    public String getId()
    {
        return id;
    }

    public IndexType getIndexType()
    {
        return indexType;
    }

    public String getIndex()
    {
        return index;
    }

    public Set<IndexDocumentEntry> getIndexDocumentEntries()
    {
        return indexDocumentEntries;
    }

    public boolean doRefreshOnStore()
    {
        return refreshOnStore;
    }

    public void setRefreshOnStore( final boolean refreshOnStore )
    {
        this.refreshOnStore = refreshOnStore;
    }
}

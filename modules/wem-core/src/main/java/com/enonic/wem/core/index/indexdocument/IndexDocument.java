package com.enonic.wem.core.index.indexdocument;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.core.index.IndexType;

public class IndexDocument
{

    private final String id;

    private final IndexType indexType;

    private final String index;

    private final Set<IndexDocumentEntry> indexDocumentEntries = Sets.newHashSet();

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
}

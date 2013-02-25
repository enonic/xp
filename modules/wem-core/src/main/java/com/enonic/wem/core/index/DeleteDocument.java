package com.enonic.wem.core.index;

public class DeleteDocument
{
    private final String indexName;

    private final IndexType indexType;

    private final String id;

    public DeleteDocument( final String indexName, final IndexType indexType, final String id )
    {
        this.indexName = indexName;
        this.indexType = indexType;
        this.id = id;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public IndexType getIndexType()
    {
        return indexType;
    }

    public String getId()
    {
        return id;
    }
}

package com.enonic.wem.core.elasticsearch;

class DeleteDocument
{
    private final String indexName;

    private final String indexType;

    private final String id;

    public DeleteDocument( final String indexName, final String indexType, final String id )
    {
        this.indexName = indexName;
        this.indexType = indexType;
        this.id = id;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public String getIndexType()
    {
        return indexType;
    }

    public String getId()
    {
        return id;
    }
}

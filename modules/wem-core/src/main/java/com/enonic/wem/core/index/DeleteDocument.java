package com.enonic.wem.core.index;

public class DeleteDocument
{
    private final Index index;

    private final IndexType indexType;

    private final String id;

    public DeleteDocument( final Index index, final IndexType indexType, final String id )
    {
        this.index = index;
        this.indexType = indexType;
        this.id = id;
    }

    public Index getIndex()
    {
        return index;
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

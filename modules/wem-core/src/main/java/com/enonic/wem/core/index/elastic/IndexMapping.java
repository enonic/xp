package com.enonic.wem.core.index.elastic;

import com.enonic.wem.core.index.Index;

public class IndexMapping
{
    private final Index index;

    private final String indexType;

    private final String source;

    public IndexMapping( final Index index, final String indexType, final String source )
    {
        this.index = index;
        this.indexType = indexType;
        this.source = source;
    }

    public Index getIndex()
    {
        return index;
    }

    public String getIndexType()
    {
        return indexType;
    }

    public String getSource()
    {
        return source;
    }
}

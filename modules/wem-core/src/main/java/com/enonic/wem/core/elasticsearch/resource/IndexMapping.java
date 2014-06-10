package com.enonic.wem.core.elasticsearch.resource;

import com.enonic.wem.core.index.Index;

public class IndexMapping
{
    private final Index index;

    private final String indexType;

    private final String source;

    IndexMapping( final Index index, final String indexType, final String source )
    {
        this.index = index;
        this.indexType = indexType;
        this.source = source;
    }

    Index getIndex()
    {
        return index;
    }

    String getIndexType()
    {
        return indexType;
    }

    String getSource()
    {
        return source;
    }
}

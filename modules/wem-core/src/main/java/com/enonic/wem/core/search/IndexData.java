package com.enonic.wem.core.search;

import org.elasticsearch.common.xcontent.XContentBuilder;

public interface IndexData
{
    public IndexType getIndexType();

    public XContentBuilder getData();

    public String getId();

    public String getIndexName();
}

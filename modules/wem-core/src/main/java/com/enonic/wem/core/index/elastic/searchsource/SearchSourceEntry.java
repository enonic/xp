package com.enonic.wem.core.index.elastic.searchsource;

public class SearchSourceEntry
{

    private final String key;

    private final Object value;

    public SearchSourceEntry( final String key, final Object value )
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public Object getValue()
    {
        return value;
    }
}

package com.enonic.wem.core.index.elastic.indexsource;

final class IndexSourceEntry
{
    private final String key;

    private final Object value;

    public IndexSourceEntry( final String key, final Object value )
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

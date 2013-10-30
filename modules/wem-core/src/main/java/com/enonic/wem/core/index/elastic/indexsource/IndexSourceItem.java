package com.enonic.wem.core.index.elastic.indexsource;

final class IndexSourceItem
{
    private final String key;

    private final Object value;

    public IndexSourceItem( final String key, final Object value )
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

package com.enonic.wem.core.search.elastic.indexsource;

final class IndexSourceEntry
{
    /**
     * This represents one entry to be indexed
     * It should contain the key and value, plus other needed stuff
     */

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

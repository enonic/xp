package com.enonic.wem.core.index.document;

public class IndexDocumentEntry
{
    private final String key;

    private final Object value;

    private boolean includeInAllField = false;

    private boolean includeOrderBy = true;

    public IndexDocumentEntry( final String key, final Object value, final boolean includeInAllField, final boolean includeOrderBy )
    {
        this.key = key;
        this.value = value;
        this.includeInAllField = includeInAllField;
        this.includeOrderBy = includeOrderBy;
    }

    public String getKey()
    {
        return key;
    }

    public Object getValue()
    {
        return value;
    }

    public boolean doIncludeInAllField()
    {
        return includeInAllField;
    }

    public boolean doIncludeOrderBy()
    {
        return includeOrderBy;
    }
}



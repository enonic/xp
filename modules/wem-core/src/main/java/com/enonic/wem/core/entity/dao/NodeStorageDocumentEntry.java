package com.enonic.wem.core.entity.dao;

public class NodeStorageDocumentEntry
{
    private final String fieldName;

    private final Object value;

    public NodeStorageDocumentEntry( final String fieldName, final Object value )
    {
        this.fieldName = fieldName;
        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }

    public String getFieldName()
    {
        return fieldName;
    }
}

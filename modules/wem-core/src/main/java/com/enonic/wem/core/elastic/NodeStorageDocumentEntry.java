package com.enonic.wem.core.elastic;

class NodeStorageDocumentEntry
{
    private final String fieldName;

    private final Object value;

    NodeStorageDocumentEntry( final String fieldName, final Object value )
    {
        this.fieldName = fieldName;
        this.value = value;
    }

    Object getValue()
    {
        return value;
    }

    String getFieldName()
    {
        return fieldName;
    }
}

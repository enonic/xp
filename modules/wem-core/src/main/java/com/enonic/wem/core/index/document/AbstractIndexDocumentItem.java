package com.enonic.wem.core.index.document;

public abstract class AbstractIndexDocumentItem<T>
{
    protected String fieldBaseName;

    protected AbstractIndexDocumentItem( final String fieldBaseName )
    {
        this.fieldBaseName = fieldBaseName;
    }

    public String getFieldBaseName()
    {
        return fieldBaseName;
    }

    public abstract IndexValueType getIndexBaseType();

    public abstract T getValue();

}

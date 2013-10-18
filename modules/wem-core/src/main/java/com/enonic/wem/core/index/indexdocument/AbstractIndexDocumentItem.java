package com.enonic.wem.core.index.indexdocument;

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

    public abstract IndexBaseType getIndexBaseType();

    public abstract T getValue();

}

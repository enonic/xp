package com.enonic.wem.core.index.indexdocument;

public class IndexDocumentStringItem
    extends AbstractIndexDocumentItem<String>
{
    private final String value;

    public IndexDocumentStringItem( final String fieldBaseName, final String value )
    {
        super( fieldBaseName );
        this.value = value;
    }

    @Override
    public IndexDocumentBaseType getIndexBaseType()
    {
        return IndexDocumentBaseType.STRING;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

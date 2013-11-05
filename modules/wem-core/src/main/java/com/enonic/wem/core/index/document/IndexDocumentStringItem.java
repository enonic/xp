package com.enonic.wem.core.index.document;

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
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.STRING;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

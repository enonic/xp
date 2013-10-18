package com.enonic.wem.core.index.indexdocument;

class IndexDocumentTokenizedItem
    extends AbstractIndexDocumentItem<String>
{
    private final String value;

    public IndexDocumentTokenizedItem( final String fieldBaseName, final String value )
    {
        super( fieldBaseName );
        this.value = value;
    }

    @Override
    public IndexBaseType getIndexBaseType()
    {
        return IndexBaseType.TOKENIZED;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

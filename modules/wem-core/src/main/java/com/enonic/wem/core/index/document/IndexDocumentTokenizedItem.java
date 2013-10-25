package com.enonic.wem.core.index.document;

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
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.TOKENIZED;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

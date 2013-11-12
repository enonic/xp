package com.enonic.wem.core.index.document;

public class IndexDocumentTokenizedItem
    extends AbstractIndexDocumentItem<String>
{
    private final String value;

    public IndexDocumentTokenizedItem( final IndexDocumentItemPath path, final String value )
    {
        super( path );
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

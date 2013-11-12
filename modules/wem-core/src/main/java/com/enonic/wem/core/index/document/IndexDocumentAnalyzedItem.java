package com.enonic.wem.core.index.document;

public class IndexDocumentAnalyzedItem
    extends AbstractIndexDocumentItem<String>
{

    private final String value;

    public IndexDocumentAnalyzedItem( final IndexDocumentItemPath path, final String value )
    {
        super( path );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.ANALYZED;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

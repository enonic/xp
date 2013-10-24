package com.enonic.wem.core.index.indexdocument;

public class IndexDocumentAnalyzedItem
    extends AbstractIndexDocumentItem<String>
{

    private final String value;

    public IndexDocumentAnalyzedItem( final String fieldBaseName, final String value )
    {
        super( fieldBaseName );
        this.value = value;
    }

    @Override
    public IndexDocumentBaseType getIndexBaseType()
    {
        return IndexDocumentBaseType.ANALYZED;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

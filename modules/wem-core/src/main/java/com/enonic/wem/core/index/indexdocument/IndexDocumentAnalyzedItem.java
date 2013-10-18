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
    public IndexBaseType getIndexBaseType()
    {
        return IndexBaseType.ANALYZED;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

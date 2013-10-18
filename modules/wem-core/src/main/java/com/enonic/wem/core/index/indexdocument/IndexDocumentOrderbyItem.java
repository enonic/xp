package com.enonic.wem.core.index.indexdocument;

class IndexDocumentOrderbyItem
    extends AbstractIndexDocumentItem<String>
{
    final String value;

    public IndexDocumentOrderbyItem( final String fieldBaseName, final String value )
    {
        super( fieldBaseName );
        this.value = value;
    }

    @Override
    public IndexBaseType getIndexBaseType()
    {
        return IndexBaseType.SORTABLE;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

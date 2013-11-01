package com.enonic.wem.core.index.document;

public class IndexDocumentOrderbyItem
    extends AbstractIndexDocumentItem<String>
{
    final String value;

    public IndexDocumentOrderbyItem( final String fieldBaseName, final String value )
    {
        super( fieldBaseName );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.SORTABLE;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

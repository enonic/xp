package com.enonic.wem.core.index.document;

import com.enonic.wem.core.index.IndexValueType;

public class IndexDocumentOrderbyItem
    extends AbstractIndexDocumentItem<String>
{
    final String value;

    public IndexDocumentOrderbyItem( final IndexDocumentItemPath path, final String value )
    {
        super( path );
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

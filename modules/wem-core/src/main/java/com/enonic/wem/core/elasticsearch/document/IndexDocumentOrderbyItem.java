package com.enonic.wem.core.elasticsearch.document;

import com.enonic.wem.core.entity.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public class IndexDocumentOrderbyItem
    extends AbstractIndexDocumentItem<String>
{
    private final String value;

    public IndexDocumentOrderbyItem( final IndexDocumentItemPath path, final String value )
    {
        super( path );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.ORDERBY;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.wem.api.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public class StoreDocumentOrderbyItem
    extends AbstractStoreDocumentItem<String>
{
    private final String value;

    public StoreDocumentOrderbyItem( final IndexDocumentItemPath path, final String value )
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

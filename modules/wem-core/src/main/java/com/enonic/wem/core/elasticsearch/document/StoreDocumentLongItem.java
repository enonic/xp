package com.enonic.wem.core.elasticsearch.document;

import com.enonic.wem.api.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public class StoreDocumentLongItem
    extends AbstractStoreDocumentItem<Long>
{
    private final Long value;

    public StoreDocumentLongItem( final IndexDocumentItemPath path, final Long value )
    {
        super( path );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.NUMBER;
    }

    @Override
    public Long getValue()
    {
        return value;
    }


}

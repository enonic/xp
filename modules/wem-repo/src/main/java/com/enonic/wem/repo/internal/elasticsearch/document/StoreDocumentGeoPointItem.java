package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.wem.api.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public class StoreDocumentGeoPointItem
    extends AbstractStoreDocumentItem<String>
{
    private final String value;

    public StoreDocumentGeoPointItem( final IndexDocumentItemPath path, final String value )
    {
        super( path );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.GEO_POINT;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}

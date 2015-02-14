package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.xp.core.index.IndexPath;
import com.enonic.wem.repo.internal.index.IndexValueType;

public class StoreDocumentGeoPointItem
    extends AbstractStoreDocumentItem<String>
{
    private final String value;

    public StoreDocumentGeoPointItem( final IndexPath path, final String value )
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

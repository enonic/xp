package com.enonic.xp.repo.impl.elasticsearch.document;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

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

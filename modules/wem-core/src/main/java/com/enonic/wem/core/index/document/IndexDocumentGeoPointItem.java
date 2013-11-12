package com.enonic.wem.core.index.document;

import com.enonic.wem.api.data.Value;

public class IndexDocumentGeoPointItem
    extends AbstractIndexDocumentItem<String>
{
    private final Value.GeoPoint value;

    public IndexDocumentGeoPointItem( final IndexDocumentItemPath path, final Value.GeoPoint value )
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
        return value.asString();
    }
}

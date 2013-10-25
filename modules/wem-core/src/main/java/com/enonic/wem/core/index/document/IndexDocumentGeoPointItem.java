package com.enonic.wem.core.index.document;

import com.enonic.wem.api.data.Value;

public class IndexDocumentGeoPointItem
    extends AbstractIndexDocumentItem<Value.GeoPoint>
{
    // TODO: Evaluate how to handle values here - do the ES-convertion here or in the deep.
    private final Value.GeoPoint value;

    public IndexDocumentGeoPointItem( final String fieldBaseName, final Value.GeoPoint value )
    {
        super( fieldBaseName );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.GEO_POINT;
    }

    @Override
    public Value.GeoPoint getValue()
    {
        return value;
    }
}

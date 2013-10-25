package com.enonic.wem.core.index.document;

import com.enonic.wem.api.data.Value;

public class IndexDocumentGeoPointItem
    extends AbstractIndexDocumentItem<Value.GeographicCoordinate>
{
    // TODO: Evaluate how to handle values here - do the ES-convertion here or in the deep.
    private final Value.GeographicCoordinate value;

    public IndexDocumentGeoPointItem( final String fieldBaseName, final Value.GeographicCoordinate value )
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
    public Value.GeographicCoordinate getValue()
    {
        return value;
    }
}

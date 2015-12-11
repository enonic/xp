package com.enonic.xp.repo.impl.index.document;

import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.util.GeoPoint;

public class IndexItemGeoPoint
    extends IndexItem<IndexValueString>
{
    public IndexItemGeoPoint( final String keyBase, final GeoPoint value )
    {
        super( keyBase, IndexValue.create( value.toString() ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.GEO_POINT;
    }
}

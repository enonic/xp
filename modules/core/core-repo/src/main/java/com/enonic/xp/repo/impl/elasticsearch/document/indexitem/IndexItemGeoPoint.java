package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.util.GeoPoint;

public class IndexItemGeoPoint
    extends IndexItem<IndexValueString>
{
    public IndexItemGeoPoint( final IndexPath indexPath, final GeoPoint value )
    {
        super( indexPath, IndexValue.create( value.toString() ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.GEO_POINT;
    }
}

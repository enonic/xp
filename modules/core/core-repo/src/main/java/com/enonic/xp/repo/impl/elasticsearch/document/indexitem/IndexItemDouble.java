package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemDouble
    extends IndexItem<IndexValueDouble>
{

    public IndexItemDouble( final IndexPath indexPath, final Double value )
    {
        super( indexPath, IndexValue.create( value ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.NUMBER;
    }
}

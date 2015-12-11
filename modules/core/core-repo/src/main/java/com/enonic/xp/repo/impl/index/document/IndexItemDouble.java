package com.enonic.xp.repo.impl.index.document;

import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemDouble
    extends IndexItem<IndexValueDouble>
{

    public IndexItemDouble( final String keyBase, final Double value )
    {
        super( keyBase, IndexValue.create( value ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.NUMBER;
    }
}

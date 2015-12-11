package com.enonic.xp.repo.impl.index.document;

import java.time.Instant;

import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemInstant
    extends IndexItem<IndexValueInstant>
{

    public IndexItemInstant( final String keyBase, final Instant value )
    {
        super( keyBase, IndexValue.create( value ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.DATETIME;
    }
}

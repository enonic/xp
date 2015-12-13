package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.time.Instant;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemInstant
    extends IndexItem<IndexValueInstant>
{

    public IndexItemInstant( final IndexPath indexPath, final Instant value )
    {
        super( indexPath, IndexValue.create( value ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.DATETIME;
    }
}

package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.time.Instant;

public class IndexValueInstant
    extends IndexValue<Instant>
{

    public IndexValueInstant( final Instant value )
    {
        super( value );
    }
}

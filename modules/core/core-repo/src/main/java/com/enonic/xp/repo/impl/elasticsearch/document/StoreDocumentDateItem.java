package com.enonic.xp.repo.impl.elasticsearch.document;

import java.time.Instant;
import java.util.Date;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class StoreDocumentDateItem
    extends AbstractStoreDocumentItem<Date>
{
    private final Instant instant;

    public StoreDocumentDateItem( final IndexPath path, final Instant instant )
    {
        super( path );
        this.instant = instant;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.DATETIME;
    }

    @Override
    public Date getValue()
    {
        return Date.from( instant );
    }
}

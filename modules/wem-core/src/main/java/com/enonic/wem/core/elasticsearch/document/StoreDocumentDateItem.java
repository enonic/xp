package com.enonic.wem.core.elasticsearch.document;

import java.time.Instant;
import java.util.Date;

import com.enonic.wem.api.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public class StoreDocumentDateItem
    extends AbstractStoreDocumentItem<Date>
{
    private final Instant instant;

    public StoreDocumentDateItem( final IndexDocumentItemPath path, final Instant instant )
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

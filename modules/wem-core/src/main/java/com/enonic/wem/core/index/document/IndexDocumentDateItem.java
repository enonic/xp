package com.enonic.wem.core.index.document;

import java.time.Instant;
import java.util.Date;

import com.enonic.wem.core.index.IndexValueType;

public class IndexDocumentDateItem
    extends AbstractIndexDocumentItem<Date>
{
    private final Instant instant;

    public IndexDocumentDateItem( final IndexDocumentItemPath path, final Instant instant )
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

package com.enonic.wem.core.index.document;

import java.util.Date;

import org.joda.time.Instant;

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
        return IndexValueType.INSTANT;
    }

    @Override
    public Date getValue()
    {
        return instant.toDate();
    }
}

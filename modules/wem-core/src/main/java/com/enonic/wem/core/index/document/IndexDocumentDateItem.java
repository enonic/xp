package com.enonic.wem.core.index.document;

import java.util.Date;

import org.joda.time.DateTime;

import com.enonic.wem.core.index.IndexValueType;

public class IndexDocumentDateItem
    extends AbstractIndexDocumentItem<Date>
{
    private final DateTime dateTime;

    public IndexDocumentDateItem( final IndexDocumentItemPath path, final DateTime dateTime )
    {
        super( path );
        this.dateTime = dateTime;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.DATETIME;
    }

    @Override
    public Date getValue()
    {
        return dateTime.toDate();
    }
}

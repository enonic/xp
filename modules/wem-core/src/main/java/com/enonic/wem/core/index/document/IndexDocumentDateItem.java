package com.enonic.wem.core.index.document;

import org.joda.time.DateTime;

public class IndexDocumentDateItem
    extends AbstractIndexDocumentItem<DateTime>
{
    private final DateTime dateTime;

    public IndexDocumentDateItem( final String fieldName, final DateTime dateTime )
    {
        super( fieldName );
        this.dateTime = dateTime;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.DATETIME;
    }

    @Override
    public DateTime getValue()
    {
        return dateTime;
    }
}

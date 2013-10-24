package com.enonic.wem.core.index.indexdocument;

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
    public IndexDocumentBaseType getIndexBaseType()
    {
        return IndexDocumentBaseType.DATETIME;
    }

    @Override
    public DateTime getValue()
    {
        return dateTime;
    }
}

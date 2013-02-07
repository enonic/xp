package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.core.search.indexdocument.IndexDocumentEntry;

final class IndexSourceEntryFactory
{
    public static final String ORDERBY_FIELD_POSTFIX = "orderby";

    public static final String NUMERIC_FIELD_POSTFIX = "numeric";

    public static final String FIELD_TYPE_SEPARATOR = ".";


    /**
     * * This class should translate a indexDocumentEntry to the necessary indexSourceEntries
     * <p/>
     * The fields to be indexed should be decided elsewhere, this should be slavery work, e.g:
     * <p/>
     * If (include orderby), create orderable value and index
     * Based on type, creat one or more fields to be indexed
     * If (value is array), then build this as array
     */
    protected Set<IndexSourceEntry> createIndexSourceEntries( IndexDocumentEntry indexDocumentEntry )
    {
        Set<IndexSourceEntry> indexSourceEntries = Sets.newHashSet();

        if ( indexDocumentEntry.doIncludeOrderBy() )
        {
            appendOrderBy( indexDocumentEntry, indexSourceEntries );
        }

        final Object value = indexDocumentEntry.getValue();

        if ( value instanceof Number )
        {
            appendNumericField( indexDocumentEntry, indexSourceEntries );
            appendStringField( indexDocumentEntry, indexSourceEntries );
        }
        else
        {
            appendStringField( indexDocumentEntry, indexSourceEntries );
        }
        /*
        else if ( value instanceof Date )
        {
            contentIndexDataElement.addDateValue( (Date) value );
            contentIndexDataElement.addStringValue( ElasticSearchFormatter.formatDateAsStringIgnoreTimezone( (Date) value ) );
        }
        else
        {
            contentIndexDataElement.addStringValue( IndexValueNormalizer.normalizeStringValue( value.toString() ) );
            addNumberOrDateIfPossible( contentIndexDataElement, value );
        }
        */

        return indexSourceEntries;
    }

    private void appendNumericField( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceEntry> indexSourceEntries )
    {
        final String baseFieldName = indexDocumentEntry.getKey();
        final Double doubleValue = ( (Number) indexDocumentEntry.getValue() ).doubleValue();

        indexSourceEntries.add( new IndexSourceEntry( generateNumericFieldName( baseFieldName ), doubleValue ) );
    }

    private void appendStringField( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceEntry> indexSourceEntries )
    {
        String baseFieldName = indexDocumentEntry.getKey();

        final String stringValue = indexDocumentEntry.getValueAsString();

        indexSourceEntries.add( new IndexSourceEntry( generateStringTypeFieldName( baseFieldName ), stringValue ) );
    }

    private void appendOrderBy( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceEntry> indexSourceEntries )
    {
        final String orderByValue = IndexSourceOrderbyValueResolver.getOrderbyValue( indexDocumentEntry.getValue() );
        final String orderByFieldName = generateOrderbyFieldName( indexDocumentEntry.getKey() );

        indexSourceEntries.add( new IndexSourceEntry( orderByFieldName, orderByValue ) );
    }

    private String generateOrderbyFieldName( final String originalFieldName )
    {
        return originalFieldName + FIELD_TYPE_SEPARATOR + ORDERBY_FIELD_POSTFIX;

    }

    private String generateNumericFieldName( final String originalFieldName )
    {
        return originalFieldName + FIELD_TYPE_SEPARATOR + NUMERIC_FIELD_POSTFIX;
    }

    private String generateStringTypeFieldName( final String originalFieldName )
    {
        return originalFieldName;
    }
}

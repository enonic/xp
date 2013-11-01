package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Date;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.wem.core.index.document.IndexDocumentEntry;

@Deprecated
final class IndexSourceEntryFactory
{
    public static final String ORDERBY_FIELD_POSTFIX = "orderby";

    public static final String NUMERIC_FIELD_POSTFIX = "numeric";

    public static final String DATE_FIELD_POSTFIX = "date";

    public static final String FIELD_TYPE_SEPARATOR = ".";

    public static final String DEFAULT_EMPTY_STRING_VALUE = "";

    protected static Set<IndexSourceItem> create( IndexDocumentEntry indexDocumentEntry )
    {
        Set<IndexSourceItem> indexSourceEntries = Sets.newHashSet();

        if ( indexDocumentEntry.doIncludeOrderBy() )
        {
            appendOrderBy( indexDocumentEntry, indexSourceEntries );
        }

        final Object value = indexDocumentEntry.getValue();

        if ( value instanceof Number )
        {
            appendNumericField( indexDocumentEntry, indexSourceEntries );
        }
        else if ( value instanceof Date )
        {
            appendDateField( indexDocumentEntry, indexSourceEntries );
        }
        else if ( value instanceof DateTime )
        {
            appendDateTimeField( indexDocumentEntry, indexSourceEntries );
        }

        appendOtherField( indexDocumentEntry, indexSourceEntries );

        return indexSourceEntries;
    }

    private static void appendNumericField( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceItem> indexSourceEntries )
    {
        final String baseFieldName = indexDocumentEntry.getKey();
        final Double doubleValue = ( (Number) indexDocumentEntry.getValue() ).doubleValue();

        indexSourceEntries.add( new IndexSourceItem( generateNumericFieldName( baseFieldName ), doubleValue ) );
    }

    private static void appendDateField( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceItem> indexSourceEntries )
    {
        final String baseFieldName = indexDocumentEntry.getKey();
        final Date dateValue = ( (Date) indexDocumentEntry.getValue() );

        indexSourceEntries.add( new IndexSourceItem( generateDateFieldName( baseFieldName ), dateValue ) );
    }

    private static void appendDateTimeField( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceItem> indexSourceEntries )
    {
        final String baseFieldName = indexDocumentEntry.getKey();
        final Date dateValue = ( (DateTime) indexDocumentEntry.getValue() ).toDate();

        indexSourceEntries.add( new IndexSourceItem( generateDateFieldName( baseFieldName ), dateValue ) );
    }

    private static void appendOtherField( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceItem> indexSourceEntries )
    {
        String baseFieldName = indexDocumentEntry.getKey();

        indexSourceEntries.add( new IndexSourceItem( generateStringTypeFieldName( baseFieldName ), indexDocumentEntry.getValue() ) );
    }

    private static void appendOrderBy( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceItem> indexSourceEntries )
    {
        final String orderByValue = OrderByValueResolver.getOrderbyValue( indexDocumentEntry.getValue() );

        if ( Strings.isNullOrEmpty( orderByValue ) )
        {
            return;
        }

        final String orderByFieldName = generateOrderbyFieldName( indexDocumentEntry.getKey() );

        indexSourceEntries.add( new IndexSourceItem( orderByFieldName, orderByValue ) );
    }

    private static String generateOrderbyFieldName( final String originalFieldName )
    {
        return replaceDotWithHyphen( originalFieldName ) + FIELD_TYPE_SEPARATOR + ORDERBY_FIELD_POSTFIX;

    }

    private static String generateNumericFieldName( final String originalFieldName )
    {
        return replaceDotWithHyphen( originalFieldName ) + FIELD_TYPE_SEPARATOR + NUMERIC_FIELD_POSTFIX;
    }

    private static String generateDateFieldName( final String originalFieldName )
    {
        return replaceDotWithHyphen( originalFieldName ) + FIELD_TYPE_SEPARATOR + DATE_FIELD_POSTFIX;
    }

    private static String generateStringTypeFieldName( final String originalFieldName )
    {
        return replaceDotWithHyphen( originalFieldName );
    }

    private static String replaceDotWithHyphen( final String originalName )
    {
        return originalName.replace( '.', '_' );
    }

    private static String genereateStringValue( final String stringValue )
    {
        return stringValue != null ? stringValue.toLowerCase() : DEFAULT_EMPTY_STRING_VALUE;
    }
}

package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import com.enonic.wem.core.search.indexdocument.IndexDocument;
import com.enonic.wem.core.search.indexdocument.IndexDocumentEntry;


/**
 * * This class should handle the transformation from IndexDocument to IndexSource
 * <p/>
 * The fields to be indexed should be decided elsewhere, this should be slavery work, e.g:
 * <p/>
 * If (include in all), append this field to all-field value
 * Add IndexSourceEntries needed
 */

public class IndexSourceFactory
{
    private static final String ALL_FIELD_NAME = "_all";

    private static final Joiner allFieldJoiner = Joiner.on( " " );

    private IndexSourceFactory()
    {
    }

    public static IndexSource create( final IndexDocument indexDocument )
    {
        final IndexSource indexSource = new IndexSource();

        final Set<IndexDocumentEntry> indexDocumentEntries = indexDocument.getIndexDocumentEntries();

        final Set<String> allFieldValues = Sets.newHashSet();

        for ( final IndexDocumentEntry indexDocumentEntry : indexDocumentEntries )
        {
            if ( indexDocumentEntry.doIncludeInAllField() )
            {
                appendToAllField( allFieldValues, indexDocumentEntry );
            }

            indexSource.addIndexSourceEntries( IndexSourceEntryFactory.create( indexDocumentEntry ) );
        }

        indexSource.addIndexSourceEntry( new IndexSourceEntry( ALL_FIELD_NAME, joinSet( allFieldJoiner, allFieldValues ) ) );

        return indexSource;
    }

    private static void appendToAllField( final Set<String> allFieldValues, final IndexDocumentEntry indexDocumentEntry )
    {
        allFieldValues.add( indexDocumentEntry.getValueAsString() );
    }


    private static String joinSet( final Joiner joiner, final Set<String> set )
    {
        return joiner.join( set );
    }

}

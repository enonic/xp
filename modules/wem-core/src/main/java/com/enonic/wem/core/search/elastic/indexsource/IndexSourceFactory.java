package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import com.enonic.wem.core.search.IndexConstants;
import com.enonic.wem.core.search.indexdocument.IndexDocument;
import com.enonic.wem.core.search.indexdocument.IndexDocumentEntry;

public class IndexSourceFactory
{
    private static final Joiner allFieldJoiner = Joiner.on( " " );

    private IndexSourceFactory()
    {
    }

    public static IndexSource create( final IndexDocument indexDocument )
    {
        final IndexSource indexSource = new IndexSource();

        final Set<IndexDocumentEntry> indexDocumentEntries = indexDocument.getIndexDocumentEntries();

        AllUserData allUserData = new AllUserData();

        for ( final IndexDocumentEntry indexDocumentEntry : indexDocumentEntries )
        {
            if ( indexDocumentEntry.doIncludeInAllField() )
            {
                allUserData.addValue( indexDocumentEntry.getValue() );
            }

            indexSource.addIndexSourceEntries( IndexSourceEntryFactory.create( indexDocumentEntry ) );
        }

        indexSource.addIndexSourceEntries( buildAllFieldValue( allUserData ) );

        return indexSource;
    }

    private static Collection<IndexSourceEntry> buildAllFieldValue( final AllUserData allUserData )
    {
        Set<IndexSourceEntry> indexSourceEntries = Sets.newHashSet();

        addSetIfExists( indexSourceEntries, IndexConstants.ALL_USERDATA_STRING_FIELD, allUserData.getStringValues() );
        addSetIfExists( indexSourceEntries, IndexConstants.ALL_USERDATA_NUMBER_FIELD, allUserData.getNumberValues() );
        addSetIfExists( indexSourceEntries, IndexConstants.ALL_USERDATA_DATE_FIELD, allUserData.getDateValues() );

        return indexSourceEntries;
    }

    private static void addSetIfExists( final Collection<IndexSourceEntry> indexSourceEntries, final String fieldName, final Set<?> set )
    {
        if ( set != null && set.size() > 0 )
        {
            indexSourceEntries.add( new IndexSourceEntry( fieldName, set ) );
        }
    }


}

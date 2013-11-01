package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.document.IndexDocument2;
import com.enonic.wem.core.index.document.IndexDocumentEntry;

@Deprecated
public class IndexSourceFactory
{
    private static final Joiner allFieldJoiner = Joiner.on( " " );

    private IndexSourceFactory()
    {
    }

    public static IndexSource create( final IndexDocument2 indexDocument )
    {
        final IndexSource.Builder builder = IndexSource.newIndexSource();

        setDocumentAnalyzer( indexDocument, builder );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = indexDocument.getIndexDocumentItems();

        for ( final AbstractIndexDocumentItem indexDocumentItem : indexDocumentItems )
        {
            final String fieldName = IndexFieldNameResolver.resolve( indexDocumentItem );
            builder.addItem( new IndexSourceItem( fieldName, indexDocumentItem.getValue() ) );
        }

        return builder.build();
    }

    private static void setDocumentAnalyzer( final IndexDocument2 indexDocument, final IndexSource.Builder builder )
    {
        final String analyzer = indexDocument.getAnalyzer();

        if ( !Strings.isNullOrEmpty( analyzer ) )
        {
            builder.addItem( new IndexSourceItem( IndexConstants.ANALYZER_VALUE_FIELD, analyzer ) );
        }
    }

    public static IndexSource create( final IndexDocument indexDocument )
    {
        final IndexSource.Builder builder = IndexSource.newIndexSource();

        final Set<IndexDocumentEntry> indexDocumentEntries = indexDocument.getIndexDocumentEntries();

        AllUserData allUserData = new AllUserData();

        for ( final IndexDocumentEntry indexDocumentEntry : indexDocumentEntries )
        {
            if ( indexDocumentEntry.doIncludeInAllField() )
            {
                allUserData.addValue( indexDocumentEntry.getValue() );
            }

            builder.addItems( IndexSourceEntryFactory.create( indexDocumentEntry ) );
        }

        //indexSource.addIndexSourceEntries( buildAllFieldValue( allUserData ) );

        return builder.build();
    }

    private static Collection<IndexSourceItem> buildAllFieldValue( final AllUserData allUserData )
    {
        Set<IndexSourceItem> indexSourceEntries = Sets.newHashSet();

        addSetIfExists( indexSourceEntries, IndexConstants.ALL_USERDATA_STRING_FIELD, allUserData.getStringValues() );
        addSetIfExists( indexSourceEntries, IndexConstants.ALL_USERDATA_NUMBER_FIELD, allUserData.getNumberValues() );
        addSetIfExists( indexSourceEntries, IndexConstants.ALL_USERDATA_DATE_FIELD, allUserData.getDateValues() );

        return indexSourceEntries;
    }

    private static void addSetIfExists( final Collection<IndexSourceItem> indexSourceEntries, final String fieldName, final Set<?> set )
    {
        if ( set != null && set.size() > 0 )
        {
            indexSourceEntries.add( new IndexSourceItem( fieldName, set ) );
        }
    }


}

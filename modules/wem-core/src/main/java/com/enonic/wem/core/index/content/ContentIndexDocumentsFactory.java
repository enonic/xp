package com.enonic.wem.core.index.content;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.indexdocument.IndexDocument;

public class ContentIndexDocumentsFactory
{

    private ContentIndexDocumentsFactory()
    {
    }

    public static Collection<IndexDocument> create( Content content )
    {
        Set<IndexDocument> indexDocuments = Sets.newHashSet();

        indexDocuments.add( createContentIndexDocument( content ) );

        return indexDocuments;
    }

    private static IndexDocument createContentIndexDocument( final Content content )
    {
        IndexDocument indexDocument = new IndexDocument( content.getId().toString(), IndexType.CONTENT, IndexConstants.WEM_INDEX );

        addContentMetaData( content, indexDocument );
        addContentData( content, indexDocument );

        return indexDocument;
    }

    private static void addContentMetaData( final Content content, final IndexDocument indexDocument )
    {
        addIfNotNull( indexDocument, ContentIndexField.KEY, content.getId() != null ? content.getId().toString() : null, false, true );
        addIfNotNull( indexDocument, ContentIndexField.PATH, content.getPath() != null ? content.getPath().toString() : null, true, true );
        addIfNotNull( indexDocument, ContentIndexField.CREATED, content.getCreatedTime(), false, true );
        addIfNotNull( indexDocument, ContentIndexField.LAST_MODIFIED, content.getModifiedTime(), false, true );
        addIfNotNull( indexDocument, ContentIndexField.CONTENT_TYPE, content.getType().getContentTypeName(), false, true );
        addIfNotNull( indexDocument, ContentIndexField.DISPLAY_NAME, content.getDisplayName(), true, true );
        addIfNotNull( indexDocument, ContentIndexField.OWNER, content.getOwner().getQualifiedName(), false, true );
        addIfNotNull( indexDocument, ContentIndexField.MODIFIER, content.getModifier().getQualifiedName(), false, true );
        addIfNotNull( indexDocument, ContentIndexField.SPACE,
                      content.getPath().getSpace() != null ? content.getPath().getSpace().toString() : null, true, true );
    }

    private static void addIfNotNull( final IndexDocument indexDocument, final String field, final Object value,
                                      final boolean includeInAllField, final boolean includeOrderBy )
    {
        if ( value != null )
        {
            indexDocument.addDocumentEntry( field, value, includeInAllField, includeOrderBy );
        }
    }


    private static void addContentData( final Content content, final IndexDocument indexDocument )
    {
        final DataSet dataSet = content.getRootDataSet();

        traverseDataSet( dataSet, indexDocument );
    }

    private static void traverseDataSet( final DataSet dataSet, final IndexDocument indexDocument )
    {
        final Iterator<Entry> dataSetIterator = dataSet.iterator();

        while ( dataSetIterator.hasNext() )
        {
            final Entry entry = dataSetIterator.next();

            if ( entry.isData() )
            {
                final Data data = entry.toData();

                final Value dataValue = data.getValue();
                final Object value = dataValue.getObject();

                final String fieldName = ContentIndexField.CONTENT_DATA_PREFIX + ContentIndexField.FIELD_SEPARATOR + getEntryPath( entry );

                indexDocument.addDocumentEntry( fieldName, value, true, true );
            }
            else if ( entry.isDataSet() )
            {
                final DataSet entryDataSet = entry.toDataSet();
                traverseDataSet( entryDataSet, indexDocument );
            }
        }
    }

    protected static String getEntryPath( final Entry entry )
    {
        return entry.getPath().toString();
    }

}

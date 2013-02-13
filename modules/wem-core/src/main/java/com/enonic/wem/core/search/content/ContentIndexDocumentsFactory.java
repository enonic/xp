package com.enonic.wem.core.search.content;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.core.search.IndexConstants;
import com.enonic.wem.core.search.IndexType;
import com.enonic.wem.core.search.indexdocument.IndexDocument;

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
        IndexDocument indexDocument = new IndexDocument( content.getId().toString(), IndexType.CONTENT, IndexConstants.WEM_INDEX.value() );

        addContentMetaData( content, indexDocument );
        addContentData( content, indexDocument );

        return indexDocument;
    }

    private static void addContentMetaData( final Content content, final IndexDocument indexDocument )
    {
        indexDocument.addDocumentEntry( ContentIndexField.KEY.id(), content.getId().toString(), false, true );
        indexDocument.addDocumentEntry( ContentIndexField.PATH.id(), content.getPath().toString(), true, true );
        indexDocument.addDocumentEntry( ContentIndexField.CREATED.id(), content.getCreatedTime(), false, true );
        indexDocument.addDocumentEntry( ContentIndexField.LAST_MODIFIED.id(), content.getModifiedTime(), false, true );
        indexDocument.addDocumentEntry( ContentIndexField.CONTENT_TYPE.id(), content.getType().getContentTypeName(), false, true );
        indexDocument.addDocumentEntry( ContentIndexField.DISPLAY_NAME.id(), content.getDisplayName(), true, true );
        indexDocument.addDocumentEntry( ContentIndexField.OWNER.id(), content.getOwner().getQualifiedName(), false, true );
        indexDocument.addDocumentEntry( ContentIndexField.MODIFIER.id(), content.getModifier().getQualifiedName(), false, true );
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

                final Object value = data.getValue().getObject();

                final String fieldName =
                    ContentIndexField.CONTENT_DATA_PREFIX.id() + ContentIndexField.FIELD_SEPARATOR.id() + getEntryPath( entry );

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

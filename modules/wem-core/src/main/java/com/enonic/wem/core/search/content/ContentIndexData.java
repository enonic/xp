package com.enonic.wem.core.search.content;

import java.util.Iterator;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.core.search.AbstractIndexData;
import com.enonic.wem.core.search.IndexData;
import com.enonic.wem.core.search.IndexType;

public class ContentIndexData
    extends AbstractIndexData
    implements IndexData
{
    private final ContentId contentId;

    private final XContentBuilder data;

    public ContentIndexData( Content content )
    {
        Preconditions.checkNotNull( content.getId(), "Content Id could not be null" );

        this.contentId = content.getId();

        try
        {
            data = build( content );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to create content index-data", e );
        }
    }

    private XContentBuilder build( final Content content )
        throws Exception
    {
        final XContentBuilder result = buildContentStart( content );
        buildAccountEnd( result );
        return result;
    }


    private XContentBuilder buildContentStart( final Content content )
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();
        addField( result, ContentIndexField.KEY_FIELD.id(), contentId.toString() );
        addMetadata( content, result );
        addContentData( content, result );

        return result;
    }

    private void addMetadata( final Content content, final XContentBuilder result )
        throws Exception
    {
        addField( result, ContentIndexField.CREATED_FIELD.id(), content.getCreatedTime() );
        addField( result, ContentIndexField.LAST_MODIFIED_FIELD.id(), content.getModifiedTime() );
        addField( result, ContentIndexField.CONTENT_TYPE_NAME_FIELD.id(), content.getType().getContentTypeName() );
        addField( result, ContentIndexField.DISPLAY_NAME_FIELD.id(), content.getDisplayName() );
        addField( result, ContentIndexField.OWNER_FIELD.id(), content.getOwner().getQualifiedName() );
        addField( result, ContentIndexField.MODIFIER_FIELD.id(), content.getModifier().getQualifiedName() );
    }

    private void addContentData( final Content content, final XContentBuilder result )
        throws Exception
    {
        traverseDataSet( result, content.getDataSet() );
    }

    private void traverseDataSet( final XContentBuilder result, final DataSet dataSet )
        throws Exception
    {
        final Iterator<Entry> dataSetIterator = dataSet.iterator();

        while ( dataSetIterator.hasNext() )
        {
            final Entry entry = dataSetIterator.next();

            if ( entry.isData() )
            {
                final Data data = entry.toData();
                final Object value = data.getValue().getObject();

                addField( result, entry.getPath().toString(), value );
            }
            else if ( entry.isDataSet() )
            {
                final DataSet entryDataSet = entry.toDataSet();
                traverseDataSet( result, entryDataSet );
            }
        }
    }

    private void buildAccountEnd( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }

    @Override
    public IndexType getIndexType()
    {
        return IndexType.CONTENT;
    }

    @Override
    public XContentBuilder getData()
    {
        return data;
    }

    @Override
    public String getId()
    {
        return this.contentId.toString();
    }

    @Override
    public String getIndexName()
    {
        return WEM_INDEX;
    }
}

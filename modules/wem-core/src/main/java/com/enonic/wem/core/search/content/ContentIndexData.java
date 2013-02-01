package com.enonic.wem.core.search.content;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
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
        addField( result, ContentIndexField.KEY_FIELD.id(), contentId.id() );
        addField( result, ContentIndexField.CREATED_FIELD.id(), content.getCreatedTime() );
        addField( result, ContentIndexField.LAST_MODIFIED_FIELD.id(), content.getModifiedTime() );
        return result;
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
        return null;
    }

    @Override
    public String getId()
    {
        return this.contentId.id();
    }

    @Override
    public String getIndexName()
    {
        return WEM_INDEX;
    }
}

package com.enonic.wem.core.content.data;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.core.content.AbstractJsonSerializer;

public final class ContentDataJsonSerializer
    extends AbstractJsonSerializer<ContentData>
{
    private final EntryJsonSerializer dataSerializer;

    public ContentDataJsonSerializer()
    {
        dataSerializer = new EntryJsonSerializer( objectMapper() );
    }

    public ContentDataJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        dataSerializer = new EntryJsonSerializer( objectMapper );
    }

    @Override
    public JsonNode serialize( final ContentData contentData )
    {
        return dataSerializer.serialize( contentData );
    }

    public ContentData parse( final JsonNode jsonNode )
    {
        final ArrayNode contentDataArrayNode = (ArrayNode) jsonNode;

        ContentData contentData = new ContentData();
        for ( JsonNode dataNode : contentDataArrayNode )
        {
            final Entry entry = dataSerializer.parse( dataNode, contentData.getDataSet() );
            contentData.getDataSet().add( entry );
        }

        return contentData;
    }
}

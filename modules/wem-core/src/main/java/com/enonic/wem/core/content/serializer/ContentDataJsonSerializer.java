package com.enonic.wem.core.content.serializer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

public class ContentDataJsonSerializer
    extends AbstractJsonSerializer<ContentData>
{
    private DataSetJsonSerializer dataSetSerializer;

    public ContentDataJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        dataSetSerializer = new DataSetJsonSerializer( objectMapper );
    }

    public ContentDataJsonSerializer()
    {
        dataSetSerializer = new DataSetJsonSerializer( objectMapper() );
    }

    @Override
    public JsonNode serialize( final ContentData dataSet )
    {
        return dataSetSerializer.serializeEntries( dataSet );
    }


    @Override
    public ContentData parse( final JsonNode arrayNode )
    {
        final ContentData contentData = new ContentData();
        dataSetSerializer.parseEntries( (ArrayNode) arrayNode, contentData );
        return contentData;
    }
}

package com.enonic.wem.core.content.serializer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.core.data.serializer.RootDataSetJsonSerializer;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

public class ContentDataJsonSerializer
    extends AbstractJsonSerializer<ContentData>
{
    private RootDataSetJsonSerializer rootDataSetJsonSerializer;

    public ContentDataJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        rootDataSetJsonSerializer = new RootDataSetJsonSerializer( objectMapper );
    }

    public ContentDataJsonSerializer()
    {
        rootDataSetJsonSerializer = new RootDataSetJsonSerializer( objectMapper() );
    }

    @Override
    public JsonNode serialize( final ContentData dataSet )
    {
        return rootDataSetJsonSerializer.serialize( dataSet );
    }

    @Override
    public ContentData parse( final JsonNode arrayNode )
    {
        final RootDataSet rootDataSet = rootDataSetJsonSerializer.parse( arrayNode );
        return new ContentData( rootDataSet );
    }
}

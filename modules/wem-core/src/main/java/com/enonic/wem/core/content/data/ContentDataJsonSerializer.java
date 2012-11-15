package com.enonic.wem.core.content.data;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.core.content.AbstractJsonSerializer;

public class ContentDataJsonSerializer
    extends AbstractJsonSerializer<ContentData>
{
    private DataJsonSerializer dataSerializer = new DataJsonSerializer();

    @Override
    public JsonNode serialize( final ContentData contentData, final ObjectMapper objectMapper )
    {
        final ArrayNode jsonContents = objectMapper.createArrayNode();
        for ( final Data data : contentData )
        {
            jsonContents.add( dataSerializer.serialize( data, objectMapper ) );
        }
        return jsonContents;
    }

    public ContentData parse( final JsonNode jsonNode )
    {
        final ArrayNode contentDataNode = (ArrayNode) jsonNode;

        ContentData contentData = new ContentData();
        DataSet dataSet = new DataSet( new EntryPath( "" ) );
        for ( JsonNode eNode : contentDataNode )
        {
            dataSet.add( dataSerializer.parse( new EntryPath(), eNode ) );
        }
        contentData.setDataSet( dataSet );

        return contentData;
    }
}

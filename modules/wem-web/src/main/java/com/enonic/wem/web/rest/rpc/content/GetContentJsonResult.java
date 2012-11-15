package com.enonic.wem.web.rest.rpc.content;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.content.data.ContentDataSerializerJson;
import com.enonic.wem.web.json.JsonResult;

class GetContentJsonResult
    extends JsonResult
{

    private Content content;

    private final static ContentDataSerializerJson contentDataSerializerJson = new ContentDataSerializerJson();

    private final static ObjectMapper objectMapper = new ObjectMapper();

    GetContentJsonResult( final Content content )
    {
        this.content = content;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        json.put( "content", serializeContent( content ) );
    }

    private ObjectNode serializeContent( final Content content )
    {
        final ObjectNode node = objectNode();
        node.put( "path", content.getPath().toString() );
        node.put( "name", content.getName() );
        node.put( "type", content.getType() != null ? content.getType().toString() : null );
        node.put( "displayName", content.getDisplayName() );
        node.put( "owner", content.getOwner() != null ? content.getOwner().toString() : null );
        node.put( "modifier", content.getModifier() != null ? content.getModifier().toString() : null );
        node.put( "modifiedTime", content.getModifiedTime() != null ? content.getModifiedTime().toString() : null );
        node.put( "createdTime", content.getCreatedTime() != null ? content.getCreatedTime().toString() : null );
        node.put( "status", "Online" );
        node.put( "editable", true );
        node.put( "deletable", true );

        final JsonNode dataValue = contentDataSerializerJson.serialize( content.getData(), objectMapper );
        node.put( "data", dataValue );
        return node;
    }

}

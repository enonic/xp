package com.enonic.wem.web.rest.rpc.content;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.content.serializer.ContentJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.schema.SchemaImageUriResolver;

class GetContentJsonResult
    extends JsonResult
{
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final ContentJsonSerializer contentSerializerJson;

    private final Content content;

    private final Contents contents;

    GetContentJsonResult( final Content content )
    {
        this.content = content;
        this.contents = null;
        this.contentSerializerJson = new ContentJsonSerializer( objectMapper );
    }

    GetContentJsonResult( final Contents contents )
    {
        this.content = null;
        this.contents = contents;
        this.contentSerializerJson = new ContentJsonSerializer( objectMapper );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );

        if ( content != null )
        {
            final ObjectNode contentJson = (ObjectNode) contentSerializerJson.serialize( content );
            contentJson.put( "iconUrl", SchemaImageUriResolver.resolve( content.getType() ) );
            json.put( "content", contentJson );
        }
        else
        {
            ArrayNode array = json.putArray( "content" );
            for ( Content content : contents )
            {
                final ObjectNode contentJson = (ObjectNode) contentSerializerJson.serialize( content );
                contentJson.put( "iconUrl", SchemaImageUriResolver.resolve( content.getType() ) );
                array.add( contentJson );
            }
        }
    }
}

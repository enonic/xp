package com.enonic.wem.admin.rpc.content;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.content.ContentImageUriResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.content.serializer.ContentJsonSerializer;

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
            contentJson.put( "iconUrl", ContentImageUriResolver.resolve( content ) );
            json.put( "content", contentJson );
        }
        else
        {
            ArrayNode array = json.putArray( "content" );
            for ( Content content : contents )
            {
                final ObjectNode contentJson = (ObjectNode) contentSerializerJson.serialize( content );
                contentJson.put( "iconUrl", ContentImageUriResolver.resolve( content ) );
                array.add( contentJson );
            }
        }
    }
}

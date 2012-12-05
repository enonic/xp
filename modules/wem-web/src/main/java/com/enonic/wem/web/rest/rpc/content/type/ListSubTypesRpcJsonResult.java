package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.core.content.type.SubTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;

final class ListSubTypesRpcJsonResult
    extends JsonResult
{
    private final static SubTypeJsonSerializer subTypeJsonSerializer = new SubTypeJsonSerializer();

    private final SubTypes subTypes;

    public ListSubTypesRpcJsonResult( final SubTypes subTypes )
    {
        this.subTypes = subTypes;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode subTypeArray = arrayNode();
        for ( SubType subType : subTypes )
        {
            final JsonNode subTypeJson = serializeContentType( subType );
            subTypeArray.add( subTypeJson );
        }
        json.put( "subTypes", subTypeArray );
    }

    private JsonNode serializeContentType( final SubType subType )
    {
        return subTypeJsonSerializer.toJson( subType );
    }

}

package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.BaseType;
import com.enonic.wem.api.content.schema.BaseTypes;
import com.enonic.wem.core.content.BaseTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.BaseTypeImageUriResolver;

final class ListBaseTypesRpcJsonResult
    extends JsonResult
{
    private final BaseTypeJsonSerializer baseTypeSerializer = new BaseTypeJsonSerializer();

    private final BaseTypes baseTypes;

    public ListBaseTypesRpcJsonResult( final BaseTypes baseTypes )
    {
        this.baseTypes = baseTypes;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode contentTypeArray = arrayNode();
        for ( BaseType baseType : baseTypes )
        {
            final JsonNode contentTypeJson = serializeContentType( baseType );
            contentTypeArray.add( contentTypeJson );
        }
        json.put( "baseTypes", contentTypeArray );
    }

    private JsonNode serializeContentType( final BaseType baseType )
    {
        final ObjectNode baseTypeJson = (ObjectNode) baseTypeSerializer.toJson( baseType );
        baseTypeJson.put( "iconUrl", BaseTypeImageUriResolver.resolve( baseType.getBaseTypeKey() ) );
        return baseTypeJson;
    }
}

package com.enonic.wem.web.rest.rpc.schema;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.core.schema.SchemaJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.schema.SchemaImageUriResolver;

final class ListSchemasRpcJsonResult
    extends JsonResult
{
    private final SchemaJsonSerializer schemaSerializer = new SchemaJsonSerializer();

    private final Schemas schemas;

    public ListSchemasRpcJsonResult( final Schemas schemas )
    {
        this.schemas = schemas;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode contentTypeArray = arrayNode();
        for ( Schema schema : schemas )
        {
            final JsonNode contentTypeJson = serializeContentType( schema );
            contentTypeArray.add( contentTypeJson );
        }
        json.put( "schemas", contentTypeArray );
    }

    private JsonNode serializeContentType( final Schema schema )
    {
        final ObjectNode schemaJson = (ObjectNode) schemaSerializer.toJson( schema );
        schemaJson.put( "iconUrl", SchemaImageUriResolver.resolve( schema.getSchemaKey() ) );
        return schemaJson;
    }
}

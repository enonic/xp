package com.enonic.wem.core.schema;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

public class SchemaJsonSerializer
    extends AbstractJsonSerializer<Schema>
    implements SchemaSerializer
{
    @Override
    protected JsonNode serialize( final Schema schema )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "key", schema.getSchemaKey().toString() );
        objectNode.put( "name", schema.getName() );
        objectNode.put( "module", schema.getModuleName().toString() );
        objectNode.put( "qualifiedName", schema.getQualifiedName().toString() );
        objectNode.put( "displayName", schema.getDisplayName() );
        objectNode.put( "type", schema.getClass().getSimpleName() );
        JsonSerializerUtil.setDateTimeValue( "createdTime", schema.getCreatedTime(), objectNode );
        JsonSerializerUtil.setDateTimeValue( "modifiedTime", schema.getModifiedTime(), objectNode );
        return objectNode;
    }

    @Override
    protected ContentType parse( final JsonNode node )
    {
        throw new UnsupportedOperationException( "Parsing of Schema is not supported" );
    }
}

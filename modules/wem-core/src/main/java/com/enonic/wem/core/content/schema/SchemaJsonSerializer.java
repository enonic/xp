package com.enonic.wem.core.content.schema;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.Schema;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

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

        if ( schema.getCreatedTime() != null )
        {
            objectNode.put( "createdTime", schema.getCreatedTime().toString() );
        }
        else
        {
            objectNode.putNull( "createdTime" );
        }
        if ( schema.getModifiedTime() != null )
        {
            objectNode.put( "modifiedTime", schema.getModifiedTime().toString() );
        }
        else
        {
            objectNode.putNull( "modifiedTime" );
        }
        return objectNode;
    }

    @Override
    protected ContentType parse( final JsonNode node )
    {
        throw new UnsupportedOperationException( "Parsing of Schema is not supported" );
    }
}

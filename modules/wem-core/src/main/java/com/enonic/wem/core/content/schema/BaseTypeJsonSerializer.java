package com.enonic.wem.core.content.schema;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.BaseType;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

public class BaseTypeJsonSerializer
    extends AbstractJsonSerializer<BaseType>
    implements BaseTypeSerializer
{
    @Override
    protected JsonNode serialize( final BaseType baseType )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "key", baseType.getBaseTypeKey().toString() );
        objectNode.put( "name", baseType.getName() );
        objectNode.put( "module", baseType.getModuleName().toString() );
        objectNode.put( "qualifiedName", baseType.getQualifiedName().toString() );
        objectNode.put( "displayName", baseType.getDisplayName() );
        objectNode.put( "type", baseType.getClass().getSimpleName() );

        if ( baseType.getCreatedTime() != null )
        {
            objectNode.put( "createdTime", baseType.getCreatedTime().toString() );
        }
        else
        {
            objectNode.putNull( "createdTime" );
        }
        if ( baseType.getModifiedTime() != null )
        {
            objectNode.put( "modifiedTime", baseType.getModifiedTime().toString() );
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
        throw new UnsupportedOperationException( "Parsing of BaseType is not supported" );
    }
}

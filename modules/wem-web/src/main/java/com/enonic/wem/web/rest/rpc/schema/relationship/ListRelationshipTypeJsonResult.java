package com.enonic.wem.web.rest.rpc.schema.relationship;


import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.schema.relationship.RelationshipTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.schema.SchemaImageUriResolver;

class ListRelationshipTypeJsonResult
    extends JsonResult
{
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RelationshipTypeJsonSerializer relationshipTypeJsonSerializer;

    private RelationshipTypes relationshipTypes;

    ListRelationshipTypeJsonResult( final RelationshipTypes relationshipTypes )
    {
        this.relationshipTypes = relationshipTypes;
        this.relationshipTypeJsonSerializer = new RelationshipTypeJsonSerializer( objectMapper );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", relationshipTypes.getSize() );
        json.put( "relationshipTypes", serialize( relationshipTypes.getList() ) );
    }

    private JsonNode serialize( final List<RelationshipType> list )
    {
        final ArrayNode relationshipTypesNode = arrayNode();
        for ( final RelationshipType relationshipType : list )
        {
            final ObjectNode contentJson = (ObjectNode) relationshipTypeJsonSerializer.serialize( relationshipType );
            contentJson.put( "iconUrl", SchemaImageUriResolver.resolve( relationshipType.getSchemaKey() ) );
            relationshipTypesNode.add( contentJson );
        }
        return relationshipTypesNode;
    }

}

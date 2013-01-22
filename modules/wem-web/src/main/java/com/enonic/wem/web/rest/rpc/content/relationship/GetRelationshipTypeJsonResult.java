package com.enonic.wem.web.rest.rpc.content.relationship;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.core.content.relationship.RelationshipTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;

class GetRelationshipTypeJsonResult
    extends JsonResult
{
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RelationshipTypeJsonSerializer relationshipTypeJsonSerializer;

    private RelationshipType relationshipType;

    GetRelationshipTypeJsonResult( final RelationshipType relationshipType )
    {
        this.relationshipType = relationshipType;
        this.relationshipTypeJsonSerializer = new RelationshipTypeJsonSerializer( objectMapper );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        final JsonNode relationshipTypeJson = relationshipTypeJsonSerializer.serialize( relationshipType );
        json.put( "relationshipType", relationshipTypeJson );
    }
}

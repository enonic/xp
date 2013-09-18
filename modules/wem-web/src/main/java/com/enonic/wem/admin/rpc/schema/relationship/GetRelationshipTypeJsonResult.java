package com.enonic.wem.admin.rpc.schema.relationship;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.core.schema.relationship.RelationshipTypeJsonSerializer;

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
        final ObjectNode relationshipTypeJson = (ObjectNode) relationshipTypeJsonSerializer.serialize( relationshipType );
        relationshipTypeJson.put( "iconUrl", SchemaImageUriResolver.resolve( relationshipType.getSchemaKey() ) );
        json.put( "relationshipType", relationshipTypeJson );
    }
}

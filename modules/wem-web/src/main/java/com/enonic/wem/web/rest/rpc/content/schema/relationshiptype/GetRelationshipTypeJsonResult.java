package com.enonic.wem.web.rest.rpc.content.schema.relationshiptype;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.relationshiptype.RelationshipType;
import com.enonic.wem.core.content.schema.relationshiptype.RelationshipTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.schema.SchemaImageUriResolver;

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
        final ObjectNode relationshipTypeJson = (ObjectNode) relationshipTypeJsonSerializer.serialize( relationshipType );
        relationshipTypeJson.put( "iconUrl", SchemaImageUriResolver.resolve( relationshipType.getSchemaKey() ) );
        json.put( "relationshipType", relationshipTypeJson );
    }
}

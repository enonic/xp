package com.enonic.wem.web.rest.rpc.content.relationshiptype;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.core.content.relationshiptype.RelationshipTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.BaseTypeImageUriResolver;

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
        relationshipTypeJson.put( "iconUrl", BaseTypeImageUriResolver.resolve( relationshipType.getBaseTypeKey() ) );
        json.put( "relationshipType", relationshipTypeJson );
    }
}

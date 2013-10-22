package com.enonic.wem.admin.rpc.relationship;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.core.relationship.RelationshipJsonSerializer;

final class GetRelationshipJsonResult
    extends JsonResult
{
    private final RelationshipJsonSerializer serializer;

    private final Relationships relationships;

    GetRelationshipJsonResult( final Relationships relationships )
    {
        this.relationships = relationships;
        this.serializer = new RelationshipJsonSerializer();
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", relationships.getSize() );
        json.put( "relationships", serialize( relationships ) );
    }

    private ArrayNode serialize( final Relationships relationships )
    {
        final ArrayNode relationshipsArray = arrayNode();
        for ( Relationship relationship : relationships )
        {
            relationshipsArray.add( serializer.toJson( relationship ) );
        }
        return relationshipsArray;
    }
}

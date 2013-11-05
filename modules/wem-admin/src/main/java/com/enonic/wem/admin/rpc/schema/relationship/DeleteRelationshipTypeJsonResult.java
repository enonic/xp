package com.enonic.wem.admin.rpc.schema.relationship;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

final class DeleteRelationshipTypeJsonResult
    extends JsonResult
{
    private final RelationshipTypeDeletionResult relationshipTypeDeletionResult;

    public DeleteRelationshipTypeJsonResult( final RelationshipTypeDeletionResult RelationshipTypeDeletionResult )
    {
        this.relationshipTypeDeletionResult = RelationshipTypeDeletionResult;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", !relationshipTypeDeletionResult.hasFailures() );
        json.put( "successes", serializeSuccesses( relationshipTypeDeletionResult.successes() ) );
        json.put( "failures", serializeFailures( relationshipTypeDeletionResult.failures() ) );
    }

    private ArrayNode serializeFailures( Iterable<RelationshipTypeDeletionResult.Failure> failures )
    {
        final ArrayNode array = arrayNode();
        for ( RelationshipTypeDeletionResult.Failure failure : failures )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "qualifiedRelationshipTypeName", failure.relationshipTypeName.toString() );
            objectNode.put( "reason", failure.reason );
        }
        return array;
    }

    private ArrayNode serializeSuccesses( Iterable<RelationshipTypeName> successes )
    {
        final ArrayNode array = arrayNode();
        for ( RelationshipTypeName success : successes )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "qualifiedRelationshipTypeName", success.toString() );
        }
        return array;
    }
}


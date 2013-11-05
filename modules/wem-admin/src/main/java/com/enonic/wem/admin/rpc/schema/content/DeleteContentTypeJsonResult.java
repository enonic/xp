package com.enonic.wem.admin.rpc.schema.content;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.schema.content.ContentTypeName;

final class DeleteContentTypeJsonResult
    extends JsonResult
{
    private final ContentTypeDeletionResult contentTypeDeletionResult;

    public DeleteContentTypeJsonResult( final ContentTypeDeletionResult contentTypeDeletionResult )
    {
        this.contentTypeDeletionResult = contentTypeDeletionResult;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", !contentTypeDeletionResult.hasFailures() );
        json.put( "successes", serializeSuccesses( contentTypeDeletionResult.successes() ) );
        json.put( "failures", serializeFailures( contentTypeDeletionResult.failures() ) );
    }

    private ArrayNode serializeFailures( Iterable<ContentTypeDeletionResult.Failure> failures )
    {
        final ArrayNode array = arrayNode();
        for ( ContentTypeDeletionResult.Failure failure : failures )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "qualifiedContentTypeName", failure.qualifiedContentTypeName.toString() );
            objectNode.put( "reason", failure.reason );
        }
        return array;
    }

    private ArrayNode serializeSuccesses( Iterable<ContentTypeName> successes )
    {
        final ArrayNode array = arrayNode();
        for ( ContentTypeName success : successes )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "qualifiedContentTypeName", success.toString() );
        }
        return array;
    }
}

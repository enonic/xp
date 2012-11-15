package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.ContentDeletionResult;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.web.json.JsonResult;

final class DeleteContentJsonResult
    extends JsonResult
{
    private final ContentDeletionResult contentDeletionResult;

    public DeleteContentJsonResult( final ContentDeletionResult contentDeletionResult )
    {
        this.contentDeletionResult = contentDeletionResult;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", !contentDeletionResult.hasFailures() );
        json.put( "successes", serializeSuccesses( contentDeletionResult.successes() ) );
        json.put( "failures", serializeFailures( contentDeletionResult.failures() ) );
    }

    private ArrayNode serializeFailures( Iterable<ContentDeletionResult.Failure> failures )
    {
        final ArrayNode array = arrayNode();
        for ( ContentDeletionResult.Failure failure : failures )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "path", failure.contentPath.toString() );
            objectNode.put( "reason", failure.reason );
        }
        return array;
    }

    private ArrayNode serializeSuccesses( Iterable<ContentPath> successes )
    {
        final ArrayNode array = arrayNode();
        for ( ContentPath success : successes )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "path", success.toString() );
        }
        return array;
    }
}

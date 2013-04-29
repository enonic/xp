package com.enonic.wem.web.rest.rpc.content;

import java.util.Map;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.web.json.JsonResult;

final class DeleteContentJsonResult
    extends JsonResult
{
    private final ImmutableList<ContentPath> successes;

    private final ImmutableMap<ContentPath, DeleteContentResult> failures;

    public DeleteContentJsonResult( final Builder builder )
    {
        this.successes = builder.success.build();
        this.failures = builder.failures.build();
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "successes", serializeSuccesses() );
        json.put( "failures", serializeFailures() );
    }

    private ArrayNode serializeFailures()
    {
        final ArrayNode array = arrayNode();
        for ( final Map.Entry<ContentPath, DeleteContentResult> failure : failures.entrySet() )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "path", failure.getKey().toString() );
            objectNode.put( "reason", failure.getValue().toString() );
        }
        return array;
    }

    private ArrayNode serializeSuccesses()
    {
        final ArrayNode array = arrayNode();
        for ( final ContentPath success : successes )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "path", success.toString() );
        }
        return array;
    }

    public static Builder newDeleteContentJsonResult()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<ContentPath> success = ImmutableList.builder();

        private ImmutableMap.Builder<ContentPath, DeleteContentResult> failures = ImmutableMap.builder();

        public Builder registerResult( final ContentPath content, final DeleteContentResult deleteResult )
        {
            if ( deleteResult == DeleteContentResult.SUCCESS )
            {
                success.add( content );
            }
            else
            {
                failures.put( content, deleteResult );
            }
            return this;
        }

        public DeleteContentJsonResult build()
        {
            return new DeleteContentJsonResult( this );
        }
    }
}

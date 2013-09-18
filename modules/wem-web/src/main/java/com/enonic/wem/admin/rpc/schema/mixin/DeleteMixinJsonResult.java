package com.enonic.wem.admin.rpc.schema.mixin;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;

final class DeleteMixinJsonResult
    extends JsonResult
{
    private final MixinDeletionResult mixinDeletionResult;

    public DeleteMixinJsonResult( final MixinDeletionResult mixinDeletionResult )
    {
        this.mixinDeletionResult = mixinDeletionResult;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", !mixinDeletionResult.hasFailures() );
        json.put( "successes", serializeSuccesses( mixinDeletionResult.successes() ) );
        json.put( "failures", serializeFailures( mixinDeletionResult.failures() ) );
    }

    private ArrayNode serializeFailures( Iterable<MixinDeletionResult.Failure> failures )
    {
        final ArrayNode array = arrayNode();
        for ( MixinDeletionResult.Failure failure : failures )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "qualifiedMixinName", failure.qualifiedMixinName.toString() );
            objectNode.put( "reason", failure.reason );
        }
        return array;
    }

    private ArrayNode serializeSuccesses( Iterable<QualifiedMixinName> successes )
    {
        final ArrayNode array = arrayNode();
        for ( QualifiedMixinName success : successes )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "qualifiedMixinName", success.toString() );
        }
        return array;
    }
}

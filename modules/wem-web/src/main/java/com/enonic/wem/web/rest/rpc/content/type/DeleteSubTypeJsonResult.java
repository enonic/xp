package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.SubTypeDeletionResult;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.web.json.JsonResult;

final class DeleteSubTypeJsonResult
    extends JsonResult
{
    private final SubTypeDeletionResult subTypeDeletionResult;

    public DeleteSubTypeJsonResult( final SubTypeDeletionResult subTypeDeletionResult )
    {
        this.subTypeDeletionResult = subTypeDeletionResult;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", !subTypeDeletionResult.hasFailures() );
        json.put( "successes", serializeSuccesses( subTypeDeletionResult.successes() ) );
        json.put( "failures", serializeFailures( subTypeDeletionResult.failures() ) );
    }

    private ArrayNode serializeFailures( Iterable<SubTypeDeletionResult.Failure> failures )
    {
        final ArrayNode array = arrayNode();
        for ( SubTypeDeletionResult.Failure failure : failures )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "qualifiedSubTypeName", failure.qualifiedSubTypeName.toString() );
            objectNode.put( "reason", failure.reason );
        }
        return array;
    }

    private ArrayNode serializeSuccesses( Iterable<QualifiedSubTypeName> successes )
    {
        final ArrayNode array = arrayNode();
        for ( QualifiedSubTypeName success : successes )
        {
            final ObjectNode objectNode = array.addObject();
            objectNode.put( "qualifiedSubTypeName", success.toString() );
        }
        return array;
    }
}

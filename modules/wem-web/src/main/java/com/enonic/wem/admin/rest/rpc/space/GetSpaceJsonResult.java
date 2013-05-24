package com.enonic.wem.admin.rest.rpc.space;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.space.Space;

final class GetSpaceJsonResult
    extends JsonResult
{
    private final Space space;

    private final SpaceJsonRpcSerializer spaceJsonRpcSerializer;

    GetSpaceJsonResult( final Space space )
    {
        this.space = space;
        this.spaceJsonRpcSerializer = new SpaceJsonRpcSerializer();
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ObjectNode spaceNode = json.putObject( "space" );
        spaceJsonRpcSerializer.serialize( space, spaceNode );
    }
}

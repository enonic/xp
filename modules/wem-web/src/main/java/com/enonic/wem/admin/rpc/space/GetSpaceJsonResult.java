package com.enonic.wem.admin.rpc.space;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.space.model.SpaceJson;
import com.enonic.wem.api.space.Space;

final class GetSpaceJsonResult
    extends JsonResult
{
    private final Space space;

    GetSpaceJsonResult( final Space space )
    {
        this.space = space;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.putPOJO( "space", new SpaceJson( space ) );
    }
}

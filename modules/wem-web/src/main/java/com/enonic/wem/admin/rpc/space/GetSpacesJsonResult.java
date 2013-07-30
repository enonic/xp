package com.enonic.wem.admin.rpc.space;


import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.space.model.SpaceJson;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;

final class GetSpacesJsonResult
    extends JsonResult
{
    private final Spaces spaces;

    GetSpacesJsonResult( final Spaces spaces )
    {
        this.spaces = spaces;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode jsonSpaces = json.putArray( "spaces" );
        for ( Space space : spaces )
        {
            jsonSpaces.addPOJO( new SpaceJson( space ) );
        }
    }
}

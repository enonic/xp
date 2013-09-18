package com.enonic.wem.admin.rpc.space;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.json.space.SpaceJson;
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

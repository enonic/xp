package com.enonic.wem.admin.rpc.space;


import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.json.space.SpaceSummaryJson;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;

final class ListSpacesJsonResult
    extends JsonResult
{
    private Spaces spaces;

    ListSpacesJsonResult( final Spaces spaces )
    {
        this.spaces = spaces;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", spaces.getSize() );
        json.put( "spaces", serialize( spaces.getList() ) );
    }

    private JsonNode serialize( final List<Space> list )
    {
        final ArrayNode spacesNode = arrayNode();
        for ( Space space : list )
        {
            spacesNode.addPOJO( new SpaceSummaryJson( space ) );
        }
        return spacesNode;
    }
}

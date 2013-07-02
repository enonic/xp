package com.enonic.wem.admin.rpc.space;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class CreateOrUpdateSpaceJsonResult
    extends JsonResult
{
    private final boolean created;

    private CreateOrUpdateSpaceJsonResult( final boolean created )
    {
        this.created = created;
    }

    public static CreateOrUpdateSpaceJsonResult created()
    {
        return new CreateOrUpdateSpaceJsonResult( true );
    }

    public static CreateOrUpdateSpaceJsonResult updated()
    {
        return new CreateOrUpdateSpaceJsonResult( false );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
    }
}

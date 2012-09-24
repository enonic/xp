package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateContentJsonResult
    extends JsonResult
{
    private final boolean created;

    private CreateOrUpdateContentJsonResult( final boolean created )
    {
        this.created = created;
    }

    public static CreateOrUpdateContentJsonResult created()
    {
        return new CreateOrUpdateContentJsonResult( true );
    }

    public static CreateOrUpdateContentJsonResult updated()
    {
        return new CreateOrUpdateContentJsonResult( false );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
    }

}

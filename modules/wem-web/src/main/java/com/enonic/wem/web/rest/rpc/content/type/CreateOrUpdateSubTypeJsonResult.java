package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateSubTypeJsonResult
    extends JsonResult
{
    private final boolean created;

    private CreateOrUpdateSubTypeJsonResult( final boolean created )
    {
        this.created = created;
    }

    public static CreateOrUpdateSubTypeJsonResult created()
    {
        return new CreateOrUpdateSubTypeJsonResult( true );
    }

    public static CreateOrUpdateSubTypeJsonResult updated()
    {
        return new CreateOrUpdateSubTypeJsonResult( false );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
    }
}

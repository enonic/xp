package com.enonic.wem.admin.rpc.account;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class CreateOrUpdateAccountJsonResult
    extends JsonResult
{
    private final boolean created;

    private CreateOrUpdateAccountJsonResult( final boolean created )
    {
        this.created = created;
    }

    public static CreateOrUpdateAccountJsonResult created()
    {
        return new CreateOrUpdateAccountJsonResult( true );
    }

    public static CreateOrUpdateAccountJsonResult updated()
    {
        return new CreateOrUpdateAccountJsonResult( false );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
    }

}

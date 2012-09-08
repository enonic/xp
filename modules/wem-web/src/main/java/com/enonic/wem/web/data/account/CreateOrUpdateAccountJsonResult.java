package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

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

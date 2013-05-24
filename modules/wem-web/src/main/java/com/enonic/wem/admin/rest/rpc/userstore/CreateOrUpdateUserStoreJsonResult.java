package com.enonic.wem.admin.rest.rpc.userstore;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class CreateOrUpdateUserStoreJsonResult
    extends JsonResult
{
    private final boolean created;

    private CreateOrUpdateUserStoreJsonResult( final boolean created )
    {
        this.created = created;
    }

    public static CreateOrUpdateUserStoreJsonResult created()
    {
        return new CreateOrUpdateUserStoreJsonResult( true );
    }

    public static CreateOrUpdateUserStoreJsonResult updated()
    {
        return new CreateOrUpdateUserStoreJsonResult( false );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
    }

}

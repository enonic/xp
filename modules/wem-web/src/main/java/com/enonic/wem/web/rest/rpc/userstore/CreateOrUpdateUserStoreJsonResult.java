package com.enonic.wem.web.rest.rpc.userstore;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateUserStoreJsonResult
    extends JsonResult
{
    private final boolean created;

    private final int count;

    private CreateOrUpdateUserStoreJsonResult( final boolean created, final int count )
    {
        this.created = created;
        this.count = count;
    }

    public static CreateOrUpdateUserStoreJsonResult created()
    {
        return new CreateOrUpdateUserStoreJsonResult( true, 1 );
    }

    public static CreateOrUpdateUserStoreJsonResult updated( final int count )
    {
        return new CreateOrUpdateUserStoreJsonResult( false, count );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
        json.put( "count", count );
    }

}

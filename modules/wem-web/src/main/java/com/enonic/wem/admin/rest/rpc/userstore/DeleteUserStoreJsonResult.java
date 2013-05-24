package com.enonic.wem.admin.rest.rpc.userstore;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

public class DeleteUserStoreJsonResult
    extends JsonResult
{
    private int deletedUserStores;

    public DeleteUserStoreJsonResult( int deletedUserStores )
    {
        this.deletedUserStores = deletedUserStores;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "deleted", deletedUserStores );
    }
}

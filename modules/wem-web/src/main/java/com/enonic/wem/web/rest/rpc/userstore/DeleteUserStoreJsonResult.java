package com.enonic.wem.web.rest.rpc.userstore;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

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

package com.enonic.wem.web.rest.rpc.userstore;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class DeleteUserStoresJsonResult
    extends JsonResult
{
    private final int deletedAccounts;

    public DeleteUserStoresJsonResult( final int deletedAccounts )
    {
        this.deletedAccounts = deletedAccounts;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "deleted", deletedAccounts );
    }

}

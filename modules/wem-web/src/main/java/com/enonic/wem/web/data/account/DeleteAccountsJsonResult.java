package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class DeleteAccountsJsonResult
    extends JsonResult
{
    private final int deletedAccounts;

    public DeleteAccountsJsonResult( final int deletedAccounts )
    {
        this.deletedAccounts = deletedAccounts;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "deleted", deletedAccounts );
    }

}

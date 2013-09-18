package com.enonic.wem.admin.rpc.account;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class VerifyUniqueEmailJsonResult
    extends JsonResult
{
    private final boolean emailInUse;

    private final String userKey;

    public VerifyUniqueEmailJsonResult( final boolean emailInUse, final String userKey )
    {
        this.emailInUse = emailInUse;
        this.userKey = userKey;
    }

    public VerifyUniqueEmailJsonResult( final boolean emailInUse )
    {
        this.emailInUse = emailInUse;
        this.userKey = null;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "emailInUse", emailInUse );
        json.put( "key", userKey );
    }

}

package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.result.JsonSuccessResult;

final class VerifyUniqueEmailJsonResult
    extends JsonSuccessResult
{
    private final boolean emailInUse;

    private final String userKey;

    public VerifyUniqueEmailJsonResult( final boolean emailInUse, final String userKey )
    {
        this.emailInUse = emailInUse;
        this.userKey = userKey;
    }

    public VerifyUniqueEmailJsonResult( final boolean emailInUse)
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

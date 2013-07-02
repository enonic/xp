package com.enonic.wem.admin.rpc.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class SuggestUserNameJsonResult
    extends JsonResult
{
    private final String username;

    public SuggestUserNameJsonResult( final String username )
    {
        this.username = username;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "username", username );
    }

}

package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.result.JsonSuccessResult;

final class SuggestUserNameJsonResult
    extends JsonSuccessResult
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

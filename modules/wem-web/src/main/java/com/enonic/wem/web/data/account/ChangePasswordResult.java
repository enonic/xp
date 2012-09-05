package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.result.JsonDataResult;


final class ChangePasswordResult
    extends JsonDataResult
{

    protected String error;

    public ChangePasswordResult( final boolean success, String error )
    {
        super( success );
        this.error = error;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        if ( error != null )
        {
            json.put( "error", error );
        }
    }
}

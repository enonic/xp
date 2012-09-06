package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.result.JsonResult;

final class ChangePasswordJsonResult
    extends JsonResult
{

    protected String error;

    public ChangePasswordJsonResult( final boolean success, String error )
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

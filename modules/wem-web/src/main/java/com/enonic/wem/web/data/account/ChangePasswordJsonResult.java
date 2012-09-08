package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class ChangePasswordJsonResult
    extends JsonResult
{
    public ChangePasswordJsonResult( final boolean success, final String error )
    {
        super( success );

        if ( error != null )
        {
            error( error );
        }
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
    }
}

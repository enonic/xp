package com.enonic.wem.admin.rest.rpc.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class ChangePasswordJsonResult
    extends JsonResult
{
    private ChangePasswordJsonResult()
    {
        super( true );
    }

    public ChangePasswordJsonResult( final String error )
    {
        super( error );
    }

    public static ChangePasswordJsonResult success()
    {
        return new ChangePasswordJsonResult();
    }

    public static ChangePasswordJsonResult error( String error )
    {
        return new ChangePasswordJsonResult( error );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
    }
}

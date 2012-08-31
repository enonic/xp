package com.enonic.wem.web.data.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.json.JsonSerializable;
import com.enonic.wem.web.rest2.resource.account.user.UserResource;
import com.enonic.wem.web.rpc.WebRpcContext;

@Component
public final class ChangePasswordRpcHandler
    extends AbstractDataRpcHandler
{
    @Autowired
    private UserResource resource;

    public ChangePasswordRpcHandler()
    {
        super( "account_changePassword" );
    }

    @Override
    public void handle( final WebRpcContext context )
        throws Exception
    {
        final String key = context.param( "key" ).required().asString();
        final String password = context.param( "password" ).required().asString();

        final JsonSerializable json = this.resource.changePassword( key, password );
        context.setResult( json );
    }
}

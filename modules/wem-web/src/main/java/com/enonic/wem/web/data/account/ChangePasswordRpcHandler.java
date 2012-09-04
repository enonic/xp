package com.enonic.wem.web.data.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.ChangePassword;
import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;
import com.enonic.wem.web.rest2.resource.account.user.UserResource;

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
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String key = context.param( "key" ).required().asString();
        final String password = context.param( "password" ).required().asString();

        String error = null;
        boolean changed = false;

        if ( password.length() <= 64 && password.length() >= 8 )
        {
            try
            {
                AccountKey accountKey = AccountKey.from( key );
                if ( accountKey.isUser() )
                {
                    ChangePassword command = Commands.account().changePassword();
                    command.key( accountKey );
                    command.password( password );
                    changed = this.client.execute( command );
                }
                else
                {
                    error = "Passwords can be changed for users only";
                }
            }
            catch ( IllegalArgumentException e )
            {
                error = e.getMessage();
            }
        }
        else
        {
            error = "Password size must be between 8 and 64 symbols";
        }

        context.setResult( new ChangePasswordResult( changed, error ) );
    }


}

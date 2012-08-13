package com.enonic.wem.core.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.account.ChangePassword;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class ChangePasswordHandler
    extends CommandHandler<ChangePassword>
{
    public ChangePasswordHandler()
    {
        super( ChangePassword.class );
    }

    @Override
    public void handle( final CommandContext context, final ChangePassword command )
        throws Exception
    {
        // TODO: Implement
    }
}

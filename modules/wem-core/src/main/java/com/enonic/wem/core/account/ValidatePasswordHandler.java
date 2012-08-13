package com.enonic.wem.core.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.account.ValidatePassword;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class ValidatePasswordHandler
    extends CommandHandler<ValidatePassword>
{
    public ValidatePasswordHandler()
    {
        super( ValidatePassword.class );
    }

    @Override
    public void handle( final CommandContext context, final ValidatePassword command )
        throws Exception
    {
        // TODO: Implement
    }
}

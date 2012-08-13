package com.enonic.wem.core.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.account.DeleteAccounts;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class DeleteAccountsHandler
    extends CommandHandler<DeleteAccounts>
{
    public DeleteAccountsHandler()
    {
        super( DeleteAccounts.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteAccounts command )
        throws Exception
    {
        // TODO: Implement
    }
}

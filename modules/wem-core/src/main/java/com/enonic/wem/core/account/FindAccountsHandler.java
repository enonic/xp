package com.enonic.wem.core.account;

import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.core.command.CommandHandler;


public final class FindAccountsHandler
    extends CommandHandler<FindAccounts>
{
    @Override
    public void handle()
        throws Exception
    {
        throw new NoSuchMethodException( "Account query is no longer implemented this way" );
    }

}

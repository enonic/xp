package com.enonic.wem.core.account;

import com.enonic.wem.api.command.account.FindMemberships;
import com.enonic.wem.core.command.CommandHandler;


public final class FindMembershipsHandler
    extends CommandHandler<FindMemberships>
{
    @Override
    public void handle()
        throws Exception
    {
        throw new NoSuchMethodException( "Account search no longer implemented like this" );
    }
}

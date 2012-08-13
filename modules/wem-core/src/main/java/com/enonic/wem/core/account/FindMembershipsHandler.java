package com.enonic.wem.core.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.account.FindMemberships;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class FindMembershipsHandler
    extends CommandHandler<FindMemberships>
{
    public FindMembershipsHandler()
    {
        super( FindMemberships.class );
    }

    @Override
    public void handle( final CommandContext context, final FindMemberships command )
        throws Exception
    {
        // TODO: Implement
    }
}

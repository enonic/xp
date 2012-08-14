package com.enonic.wem.core.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.account.FindMembers;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class FindMembersHandler
    extends CommandHandler<FindMembers>
{
    public FindMembersHandler()
    {
        super( FindMembers.class );
    }

    @Override
    public void handle( final CommandContext context, final FindMembers command )
        throws Exception
    {
        // TODO: Implement
    }
}

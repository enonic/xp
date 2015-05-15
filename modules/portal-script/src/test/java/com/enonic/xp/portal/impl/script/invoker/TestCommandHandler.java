package com.enonic.xp.portal.impl.script.invoker;

import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

public final class TestCommandHandler
    implements CommandHandler
{
    @Override
    public String getName()
    {
        return "test";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        return "test";
    }
}

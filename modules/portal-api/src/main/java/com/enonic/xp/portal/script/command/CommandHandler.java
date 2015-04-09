package com.enonic.xp.portal.script.command;

import com.google.common.annotations.Beta;

@Beta
public interface CommandHandler
{
    String getName();

    Object execute( CommandRequest req );
}

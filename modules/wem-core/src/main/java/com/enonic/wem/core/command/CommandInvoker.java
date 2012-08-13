package com.enonic.wem.core.command;

import com.enonic.wem.api.command.Command;

public interface CommandInvoker
{
    public void invoke( CommandContext context, Command command );
}

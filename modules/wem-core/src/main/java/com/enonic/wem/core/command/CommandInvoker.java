package com.enonic.wem.core.command;

import com.google.inject.ImplementedBy;

import com.enonic.wem.api.command.Command;

@ImplementedBy(CommandInvokerImpl.class)
public interface CommandInvoker
{
    public void invoke( CommandContext context, Command command );
}

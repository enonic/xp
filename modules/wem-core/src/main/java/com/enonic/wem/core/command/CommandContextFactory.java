package com.enonic.wem.core.command;

import com.google.inject.ImplementedBy;

@ImplementedBy(CommandContextFactoryImpl.class)
public interface CommandContextFactory
{
    public CommandContext create();
}

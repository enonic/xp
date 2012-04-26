package com.enonic.wem.api;

import com.enonic.wem.api.command.Command;

public interface Client
{
    public <R, C extends Command<R>> R execute( C command );
}

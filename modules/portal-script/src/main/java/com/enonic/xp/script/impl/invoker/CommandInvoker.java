package com.enonic.xp.script.impl.invoker;

import com.enonic.xp.portal.script.command.CommandRequest;

public interface CommandInvoker
{
    public Object invoke( CommandRequest req );
}

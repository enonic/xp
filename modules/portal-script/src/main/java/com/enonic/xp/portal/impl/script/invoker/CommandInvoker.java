package com.enonic.xp.portal.impl.script.invoker;

import com.enonic.xp.portal.script.command.CommandRequest;

public interface CommandInvoker
{
    public Object invoke( CommandRequest req );
}

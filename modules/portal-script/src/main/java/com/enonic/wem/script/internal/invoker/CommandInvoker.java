package com.enonic.wem.script.internal.invoker;

import com.enonic.xp.portal.script.command.CommandRequest;

public interface CommandInvoker
{
    public Object invoke( CommandRequest req );
}

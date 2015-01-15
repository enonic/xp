package com.enonic.wem.script.internal.invoker;

import com.enonic.wem.script.command.CommandRequest;

public interface CommandInvoker
{
    public Object invoke( CommandRequest req );
}

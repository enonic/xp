package com.enonic.wem.script.command;

public interface CommandInvoker
{
    public Object invoke( CommandRequest req );
}

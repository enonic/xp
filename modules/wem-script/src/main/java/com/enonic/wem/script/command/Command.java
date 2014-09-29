package com.enonic.wem.script.command;

public abstract class Command<R>
{
    private R result;

    public final R getResult()
    {
        return result;
    }

    public final void setResult( final R result )
    {
        this.result = result;
    }
}

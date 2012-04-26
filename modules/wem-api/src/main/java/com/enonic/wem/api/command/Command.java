package com.enonic.wem.api.command;

public abstract class Command<R>
{
    private R result;

    public final R getResult()
    {
        return this.result;
    }

    public final void setResult( final R result )
    {
        this.result = result;
    }

    public abstract void validate();
}

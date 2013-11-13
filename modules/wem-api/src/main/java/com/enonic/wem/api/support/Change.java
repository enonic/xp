package com.enonic.wem.api.support;


public final class Change<T>
{
    public final T from;

    public final T to;

    public Change( final T from, final T to )
    {
        this.from = from;
        this.to = to;
    }
}

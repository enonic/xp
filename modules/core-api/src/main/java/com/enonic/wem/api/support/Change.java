package com.enonic.wem.api.support;


public final class Change<T>
{
    public final String property;

    public final T from;

    public final T to;

    public Change( final String property, final T from, final T to )
    {
        this.property = property;
        this.from = from;
        this.to = to;
    }
}

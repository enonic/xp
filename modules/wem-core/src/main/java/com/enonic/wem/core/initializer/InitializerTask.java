package com.enonic.wem.core.initializer;

public abstract class InitializerTask
    implements Comparable<InitializerTask>
{
    private final int order;

    public InitializerTask( final int order )
    {
        this.order = order;
    }

    public final int getOrder()
    {
        return this.order;
    }

    public abstract void initialize()
        throws Exception;

    @Override
    public int compareTo( final InitializerTask o )
    {
        return this.order - o.order;
    }
}

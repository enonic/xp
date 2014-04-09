package com.enonic.wem.core.rendering;


public abstract class BaseRenderer
{
    protected final Context context;

    protected BaseRenderer( final Context context )
    {
        this.context = context;
    }
}

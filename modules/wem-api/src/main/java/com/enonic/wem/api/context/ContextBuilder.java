package com.enonic.wem.api.context;

import com.enonic.wem.api.workspace.Workspace;

public final class ContextBuilder
{
    private final Context context;

    private ContextBuilder()
    {
        this.context = new Context();
    }

    public ContextBuilder workspace( final String workspace )
    {
        return object( Workspace.from( workspace ) );
    }

    @SuppressWarnings("unchecked")
    public <T> ContextBuilder object( final T instance )
    {
        return object( (Class<T>) instance.getClass(), instance );
    }

    public <T> ContextBuilder object( final Class<T> type, final T instance )
    {
        return object( type.getName(), instance );
    }

    public <T> ContextBuilder object( final String key, final T instance )
    {
        this.context.objects.put( key, instance );
        return this;
    }

    public Context build()
    {
        return this.context;
    }

    public static ContextBuilder create()
    {
        return new ContextBuilder();
    }

    public static ContextBuilder from( final Context context )
    {
        return new ContextBuilder();
    }
}

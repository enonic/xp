package com.enonic.wem.core.initializer;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public final class InitializerBinder
{
    private final Multibinder<InitializerTask> binder;

    private InitializerBinder( final Binder binder )
    {
        this.binder = Multibinder.newSetBinder( binder, InitializerTask.class );
    }

    public <T extends InitializerTask> void add( final Class<T> task )
    {
        this.binder.addBinding().to( task ).in( Scopes.SINGLETON );
    }

    public static InitializerBinder from( final Binder binder )
    {
        return new InitializerBinder( binder );
    }
}

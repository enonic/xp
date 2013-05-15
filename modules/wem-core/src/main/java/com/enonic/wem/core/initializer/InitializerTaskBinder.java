package com.enonic.wem.core.initializer;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public final class InitializerTaskBinder
{
    private final Multibinder<InitializerTask> binder;

    private InitializerTaskBinder( final Binder binder )
    {
        this.binder = Multibinder.newSetBinder( binder, InitializerTask.class );
    }

    public InitializerTaskBinder bind( final Class<? extends InitializerTask> task )
    {
        this.binder.addBinding().to( task ).in( Scopes.SINGLETON );
        return this;
    }

    public static InitializerTaskBinder from( final Binder binder )
    {
        return new InitializerTaskBinder( binder );
    }
}

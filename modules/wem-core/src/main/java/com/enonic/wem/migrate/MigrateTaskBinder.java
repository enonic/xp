package com.enonic.wem.migrate;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public final class MigrateTaskBinder
{
    private final Multibinder<MigrateTask> binder;

    private MigrateTaskBinder( final Binder binder )
    {
        this.binder = Multibinder.newSetBinder( binder, MigrateTask.class );
    }

    public MigrateTaskBinder bind( final Class<? extends MigrateTask> task )
    {
        this.binder.addBinding().to( task ).in( Scopes.SINGLETON );
        return this;
    }

    public static MigrateTaskBinder from( final Binder binder )
    {
        return new MigrateTaskBinder( binder );
    }
}

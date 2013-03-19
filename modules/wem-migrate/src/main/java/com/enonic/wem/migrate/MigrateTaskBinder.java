package com.enonic.wem.migrate;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;

public final class MigrateTaskBinder
{
    private final Multibinder<MigrateTask> binder;

    private MigrateTaskBinder( final Binder binder )
    {
        this.binder = Multibinder.newSetBinder( binder, MigrateTask.class );
    }

    public <T extends MigrateTask> void add( final Class<T> task )
    {
        this.binder.addBinding().to( task );
    }

    public static MigrateTaskBinder from( final Binder binder )
    {
        return new MigrateTaskBinder( binder );
    }
}

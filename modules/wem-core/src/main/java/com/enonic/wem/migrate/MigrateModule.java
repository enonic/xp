package com.enonic.wem.migrate;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.initializer.InitializerTaskBinder;
import com.enonic.wem.migrate.account.AccountMigrateTask;

public final class MigrateModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        InitializerTaskBinder.from( binder() ).bind( DataMigrator.class );
        MigrateTaskBinder.from( binder() ).bind( AccountMigrateTask.class );
    }
}

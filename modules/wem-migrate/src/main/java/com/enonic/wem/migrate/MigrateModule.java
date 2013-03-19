package com.enonic.wem.migrate;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.initializer.InitializerBinder;
import com.enonic.wem.migrate.account.MigrateAccountModule;

public final class MigrateModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new MigrateAccountModule() );
        InitializerBinder.from( binder() ).add( DataMigrator.class );
    }
}

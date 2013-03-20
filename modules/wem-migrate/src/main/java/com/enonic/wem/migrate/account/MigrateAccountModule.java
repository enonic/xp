package com.enonic.wem.migrate.account;

import com.google.inject.AbstractModule;
import com.enonic.wem.migrate.MigrateTaskBinder;

public final class MigrateAccountModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        MigrateTaskBinder.from( binder() ).add( AccountMigrateTask.class );
    }
}

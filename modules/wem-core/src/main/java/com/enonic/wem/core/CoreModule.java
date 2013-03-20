package com.enonic.wem.core;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.account.AccountModule;
import com.enonic.wem.core.client.ClientModule;
import com.enonic.wem.core.command.CommandModule;
import com.enonic.wem.core.config.ConfigModule;
import com.enonic.wem.core.content.ContentModule;
import com.enonic.wem.core.country.CountryModule;
import com.enonic.wem.core.home.HomeModule;
import com.enonic.wem.core.index.IndexModule;
import com.enonic.wem.core.initializer.InitializerModule;
import com.enonic.wem.core.jcr.JcrModule;
import com.enonic.wem.core.lifecycle.LifecycleModule;
import com.enonic.wem.core.locale.LocaleModule;
import com.enonic.wem.core.space.SpaceModule;
import com.enonic.wem.core.time.TimeModule;
import com.enonic.wem.core.userstore.UserStoreModule;

public final class CoreModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new LifecycleModule() );
        install( new HomeModule() );
        install( new ConfigModule() );
        install( new CountryModule() );
        install( new LocaleModule() );
        install( new TimeModule() );
        install( new CommandModule() );
        install( new ClientModule() );
        install( new JcrModule() );
        install( new IndexModule() );
        install( new AccountModule() );
        install( new UserStoreModule() );
        install( new SpaceModule() );
        install( new ContentModule() );
        install( new InitializerModule() );
    }
}

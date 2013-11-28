package com.enonic.wem.core.content.site;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.initializer.InitializerTaskBinder;

public class SiteModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        InitializerTaskBinder.from( binder() ).add( SitesInitializer.class );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( DeleteSiteHandler.class );
        commands.add( UpdateSiteHandler.class );
        commands.add( CreateSiteHandler.class );
        commands.add( DeleteSiteTemplateHandler.class );
//        commands.add( GetSiteTemplatesHandler.class );
        commands.add( CreateSiteTemplateHandler.class );
    }
}

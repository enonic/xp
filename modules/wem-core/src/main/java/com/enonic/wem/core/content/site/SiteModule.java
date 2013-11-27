package com.enonic.wem.core.content.site;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.command.CommandBinder;

public class SiteModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( DeleteSiteHandler.class );
        commands.add( UpdateSiteHandler.class );
        commands.add( CreateSiteHandler.class );
        commands.add( DeleteSiteTemplateHandler.class );
//        commands.add( GetSiteTemplatesHandler.class );
        commands.add( CreateSiteTemplateHandler.class );
    }
}

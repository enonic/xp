package com.enonic.wem.core.content.page;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.command.CommandBinder;

public class PageModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( GetPageTemplateByKeyHandler.class );
        commands.add( GetPartTemplateByKeyHandler.class );
        commands.add( GetLayoutTemplateByKeyHandler.class );
        commands.add( GetImageTemplateByKeyHandler.class );
        commands.add( GetPageTemplatesBySiteTemplateHandler.class );
        commands.add( GetPartTemplatesBySiteTemplateHandler.class );
        commands.add( GetLayoutTemplatesBySiteTemplateHandler.class );
        commands.add( GetImageTemplatesBySiteTemplateHandler.class );
    }
}

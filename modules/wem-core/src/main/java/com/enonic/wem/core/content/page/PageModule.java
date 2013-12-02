package com.enonic.wem.core.content.page;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.content.page.image.GetImageTemplateByKeyHandler;
import com.enonic.wem.core.content.page.image.GetImageTemplatesBySiteTemplateHandler;
import com.enonic.wem.core.content.page.image.UpdateImageTemplateHandler;
import com.enonic.wem.core.content.page.layout.GetLayoutTemplateByKeyHandler;
import com.enonic.wem.core.content.page.layout.GetLayoutTemplatesBySiteTemplateHandler;
import com.enonic.wem.core.content.page.layout.UpdateLayoutTemplateHandler;
import com.enonic.wem.core.content.page.part.GetPartTemplateByKeyHandler;
import com.enonic.wem.core.content.page.part.GetPartTemplatesBySiteTemplateHandler;
import com.enonic.wem.core.content.page.part.UpdatePartTemplateHandler;

public class PageModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreatePageHandler.class );
        commands.add( UpdatePageHandler.class );

        commands.add( GetPageTemplateByKeyHandler.class );
        commands.add( GetPageTemplatesBySiteTemplateHandler.class );
        commands.add( UpdatePageTemplateHandler.class );

        commands.add( GetPartTemplateByKeyHandler.class );
        commands.add( GetPartTemplatesBySiteTemplateHandler.class );
        commands.add( UpdatePartTemplateHandler.class );

        commands.add( GetLayoutTemplateByKeyHandler.class );
        commands.add( GetLayoutTemplatesBySiteTemplateHandler.class );
        commands.add( UpdateLayoutTemplateHandler.class );

        commands.add( GetImageTemplateByKeyHandler.class );
        commands.add( GetImageTemplatesBySiteTemplateHandler.class );
        commands.add( UpdateImageTemplateHandler.class );
    }
}

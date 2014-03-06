package com.enonic.wem.core.content.page;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.content.page.text.TextDescriptorService;
import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.content.page.image.ImageDescriptorServiceImpl;
import com.enonic.wem.core.content.page.layout.CreateLayoutDescriptorHandler;
import com.enonic.wem.core.content.page.layout.GetLayoutDescriptorHandler;
import com.enonic.wem.core.content.page.layout.GetLayoutDescriptorsByModulesHandler;
import com.enonic.wem.core.content.page.layout.LayoutDescriptorServiceImpl;
import com.enonic.wem.core.content.page.part.CreatePartDescriptorHandler;
import com.enonic.wem.core.content.page.part.GetPartDescriptorHandler;
import com.enonic.wem.core.content.page.part.GetPartDescriptorsByModulesHandler;
import com.enonic.wem.core.content.page.part.PartDescriptorServiceImpl;
import com.enonic.wem.core.content.page.text.TextDescriptorServiceImpl;

public class PageModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ImageDescriptorService.class ).to( ImageDescriptorServiceImpl.class ).in( Singleton.class );
        bind( LayoutDescriptorService.class ).to( LayoutDescriptorServiceImpl.class ).in( Singleton.class );
        bind( PartDescriptorService.class ).to( PartDescriptorServiceImpl.class ).in( Singleton.class );
        bind( PageDescriptorService.class ).to( PageDescriptorServiceImpl.class ).in( Singleton.class );
        bind( PageTemplateService.class ).to( PageTemplateServiceImpl.class ).in( Singleton.class );
        bind( TextDescriptorService.class ).to( TextDescriptorServiceImpl.class ).in( Singleton.class );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreatePageHandler.class );
        commands.add( UpdatePageHandler.class );

        commands.add( GetPageTemplateByKeyHandler.class );
        commands.add( GetPageTemplatesBySiteTemplateHandler.class );
        commands.add( UpdatePageTemplateHandler.class );

        commands.add( CreatePageDescriptorHandler.class );
        commands.add( CreatePartDescriptorHandler.class );
        commands.add( GetLayoutDescriptorHandler.class );
        commands.add( GetPartDescriptorHandler.class );
        commands.add( GetPageDescriptorHandler.class );
        commands.add( GetPartDescriptorsByModulesHandler.class );
        commands.add( GetLayoutDescriptorsByModulesHandler.class );
        commands.add( CreateLayoutDescriptorHandler.class );
    }
}

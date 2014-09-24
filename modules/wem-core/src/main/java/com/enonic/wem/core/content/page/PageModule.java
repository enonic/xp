package com.enonic.wem.core.content.page;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.content.page.PageComponentService;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.image.ImageComponentType;
import com.enonic.wem.api.content.page.layout.LayoutComponentType;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartComponentType;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.content.page.text.TextComponentType;
import com.enonic.wem.core.content.page.layout.LayoutDescriptorServiceImpl;
import com.enonic.wem.core.content.page.part.PartDescriptorServiceImpl;

public class PageModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        // Ensure all component types are instantiated once, so that they get registered in PageComponentType
        new ImageComponentType();
        new PartComponentType();
        new LayoutComponentType();
        new TextComponentType();

        bind( LayoutDescriptorService.class ).to( LayoutDescriptorServiceImpl.class ).in( Singleton.class );
        bind( PartDescriptorService.class ).to( PartDescriptorServiceImpl.class ).in( Singleton.class );
        bind( PageDescriptorService.class ).to( PageDescriptorServiceImpl.class ).in( Singleton.class );
        bind( PageTemplateService.class ).to( PageTemplateServiceImpl.class ).in( Singleton.class );
        bind( PageComponentService.class ).to( PageComponentServiceImpl.class ).in( Singleton.class );
        bind( PageService.class ).to( PageServiceImpl.class ).in( Singleton.class );
    }
}

package com.enonic.wem.portal.rendering;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.part.PartComponent;

public final class RenderingModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( RendererFactory.class ).to( RendererFactoryImpl.class ).in( Singleton.class );

        MapBinder<Class, Renderer> componentRendererMapBinder = MapBinder.newMapBinder( binder(), Class.class, Renderer.class );
        componentRendererMapBinder.addBinding( PartComponent.class ).to( PartRenderer.class );
        componentRendererMapBinder.addBinding( ImageComponent.class ).to( ImageRenderer.class );
        componentRendererMapBinder.addBinding( LayoutComponent.class ).to( LayoutRenderer.class );
    }
}

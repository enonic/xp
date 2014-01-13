package com.enonic.wem.portal;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.portal.rendering.ImageRenderer;
import com.enonic.wem.portal.rendering.LayoutRenderer;
import com.enonic.wem.portal.rendering.PartRenderer;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.script.ScriptModule;
import com.enonic.wem.web.WebInitializerBinder;

public final class PortalModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ScriptModule() );
        WebInitializerBinder.from( binder() ).add( PortalWebInitializer.class );

        MapBinder<Class, Renderer> componentRendererMapBinder = MapBinder.newMapBinder( binder(), Class.class, Renderer.class );
        componentRendererMapBinder.addBinding( PartComponent.class ).to( PartRenderer.class );
        componentRendererMapBinder.addBinding( ImageComponent.class ).to( ImageRenderer.class );
        componentRendererMapBinder.addBinding( LayoutComponent.class ).to( LayoutRenderer.class );
    }
}

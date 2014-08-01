package com.enonic.wem.portal.rendering;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.text.TextComponent;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.content.page.ImageRenderer;
import com.enonic.wem.portal.content.page.LayoutRenderer;
import com.enonic.wem.portal.content.page.PartRenderer;
import com.enonic.wem.portal.content.page.TextRenderer;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

public final class RenderingModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( RendererFactory.class ).to( RendererFactoryImpl.class ).in( Singleton.class );

        final TypeLiteral<Class<? extends Renderable>> keyType = new TypeLiteral<Class<? extends Renderable>>()
        {
        };
        final TypeLiteral<Renderer> valueType = new TypeLiteral<Renderer>()
        {
        };
        MapBinder<Class<? extends Renderable>, Renderer> componentRendererMapBinder = newMapBinder( binder(), keyType, valueType );
        componentRendererMapBinder.addBinding( PartComponent.class ).to( PartRenderer.class );
        componentRendererMapBinder.addBinding( ImageComponent.class ).to( ImageRenderer.class );
        componentRendererMapBinder.addBinding( LayoutComponent.class ).to( LayoutRenderer.class );
        componentRendererMapBinder.addBinding( TextComponent.class ).to( TextRenderer.class );
    }
}

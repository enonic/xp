package com.enonic.wem.portal.internal.rendering;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import com.enonic.wem.portal.internal.content.page.ImageRenderer;
import com.enonic.wem.portal.internal.content.page.LayoutRenderer;
import com.enonic.wem.portal.internal.content.page.PartRenderer;
import com.enonic.wem.portal.internal.content.page.TextRenderer;

public final class RenderingModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( RendererFactory.class ).to( RendererFactoryImpl.class ).in( Singleton.class );

        final Multibinder<Renderer> renderers = Multibinder.newSetBinder( binder(), Renderer.class );
        renderers.addBinding().to( PartRenderer.class ).in( Singleton.class );
        renderers.addBinding().to( ImageRenderer.class ).in( Singleton.class );
        renderers.addBinding().to( LayoutRenderer.class ).in( Singleton.class );
        renderers.addBinding().to( TextRenderer.class ).in( Singleton.class );
    }
}

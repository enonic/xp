package com.enonic.wem.core.web;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.web.mvc.FreeMarkerRenderer;
import com.enonic.wem.core.web.mvc.FreeMarkerRendererImpl;
import com.enonic.wem.core.web.servlet.ServletModule;

public final class WebModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ServletModule() );
        bind( FreeMarkerRenderer.class ).to( FreeMarkerRendererImpl.class ).in( Singleton.class );
    }
}

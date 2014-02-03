package com.enonic.wem.web;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.web.mvc.FreeMarkerRenderer;
import com.enonic.wem.web.mvc.FreeMarkerRendererImpl;
import com.enonic.wem.web.servlet.ServletModule;

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

package com.enonic.wem.core.web;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.web.servlet.ServletModule;

public final class WebModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ServletModule() );
    }
}

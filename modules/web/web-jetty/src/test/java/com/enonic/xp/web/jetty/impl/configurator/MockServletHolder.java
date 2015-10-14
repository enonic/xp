package com.enonic.xp.web.jetty.impl.configurator;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.servlet.ServletHolder;

final class MockServletHolder
    extends ServletHolder
{
    public MultipartConfigElement getMultipartConfig()
    {
        return ( (Registration) getRegistration() ).getMultipartConfig();
    }
}

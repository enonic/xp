package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.ee10.servlet.ServletHolder;

import jakarta.servlet.MultipartConfigElement;

final class MockServletHolder
    extends ServletHolder
{
    public MultipartConfigElement getMultipartConfig()
    {
        return ( (Registration) getRegistration() ).getMultipartConfig();
    }
}

package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.base.StandardSystemProperty;

import jakarta.servlet.MultipartConfigElement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultipartConfiguratorTest
    extends JettyConfiguratorTest<ServletHolder>
{
    private MockServletHolder servletHolder;

    @Override
    protected ServletHolder setupObject()
    {
        this.servletHolder = new MockServletHolder();
        return this.servletHolder;
    }

    @Override
    protected JettyConfigurator<ServletHolder> newConfigurator()
    {
        return new MultipartConfigurator();
    }

    private MultipartConfigElement getMultipartConfig()
    {
        return this.servletHolder.getMultipartConfig();
    }

    @Test
    void testConfigure()
    {
        configure();

        final MultipartConfigElement multipartConfig = getMultipartConfig();
        assertEquals( StandardSystemProperty.JAVA_IO_TMPDIR.value(), multipartConfig.getLocation() );
        assertEquals( -1, multipartConfig.getMaxFileSize() );
        assertEquals( -1, multipartConfig.getMaxRequestSize() );
        assertEquals( 1000, multipartConfig.getFileSizeThreshold() );
    }

    @Test
    void overrideConfig()
    {
        Mockito.when( this.config.multipart_store() ).thenReturn( "/other" );
        Mockito.when( this.config.multipart_maxFileSize() ).thenReturn( 2000L );
        Mockito.when( this.config.multipart_maxRequestSize() ).thenReturn( 20000L );
        Mockito.when( this.config.multipart_fileSizeThreshold() ).thenReturn( 2000 );

        configure();

        final MultipartConfigElement multipartConfig = getMultipartConfig();
        assertEquals( "/other", multipartConfig.getLocation() );
        assertEquals( 2000L, multipartConfig.getMaxFileSize() );
        assertEquals( 20000L, multipartConfig.getMaxRequestSize() );
        assertEquals( 2000, multipartConfig.getFileSizeThreshold() );
    }

    private static class MockServletHolder
        extends ServletHolder
    {
        public MultipartConfigElement getMultipartConfig()
        {
            return getRegistration().getMultipartConfigElement();
        }
    }
}

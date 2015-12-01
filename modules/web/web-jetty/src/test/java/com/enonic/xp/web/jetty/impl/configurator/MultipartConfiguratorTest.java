package com.enonic.xp.web.jetty.impl.configurator;

import javax.servlet.MultipartConfigElement;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class MultipartConfiguratorTest
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
    public void testConfigure()
    {
        configure();

        final MultipartConfigElement multipartConfig = getMultipartConfig();
        assertEquals( FileUtils.getTempDirectoryPath(), multipartConfig.getLocation() );
        assertEquals( -1, multipartConfig.getMaxFileSize() );
        assertEquals( -1, multipartConfig.getMaxRequestSize() );
        assertEquals( 1000, multipartConfig.getFileSizeThreshold() );
    }

    @Test
    public void overrideConfig()
    {
        Mockito.when( this.config.multipart_store() ).thenReturn( FileUtils.getTempDirectoryPath() + "/other" );
        Mockito.when( this.config.multipart_maxFileSize() ).thenReturn( 2000L );
        Mockito.when( this.config.multipart_maxRequestSize() ).thenReturn( 20000L );
        Mockito.when( this.config.multipart_fileSizeThreshold() ).thenReturn( 2000 );

        configure();

        final MultipartConfigElement multipartConfig = getMultipartConfig();
        assertEquals( FileUtils.getTempDirectoryPath() + "/other", multipartConfig.getLocation() );
        assertEquals( 2000L, multipartConfig.getMaxFileSize() );
        assertEquals( 20000L, multipartConfig.getMaxRequestSize() );
        assertEquals( 2000, multipartConfig.getFileSizeThreshold() );
    }
}
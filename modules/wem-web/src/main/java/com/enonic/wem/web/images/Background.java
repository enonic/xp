package com.enonic.wem.web.images;

import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import com.enonic.wem.core.config.ConfigProperties;

public class Background
{
    private final static Logger LOG = LoggerFactory.getLogger( Background.class );

    public static void stream( final ServletConfig servletConfig, final HttpServletResponse response )
    {
        try
        {
            final ServletContext servletContext = servletConfig.getServletContext();
            final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext( servletContext );
            final ConfigProperties configProperties = springContext.getBean( ConfigProperties.class );

            final Object backgroundImagePathObject = configProperties.get( "cms.admin.background-image" );

            final String backgroundImagePath =
                backgroundImagePathObject != null ? backgroundImagePathObject.toString() : "$cms-home/customize/home-background.jpg";

            final String backgroundImagePathResolved =
                backgroundImagePath.replace( "$cms-home", configProperties.get( "cms.home" ).toString() );

            final File source = new File( backgroundImagePathResolved );
            if ( source.exists() )
            {
                response.setContentType( servletContext.getMimeType( backgroundImagePathResolved ) );
                Files.copy( source, response.getOutputStream() );
            }
            else
            {
                response.setContentType( "image/jpeg" );
                final InputStream image = servletContext.getResourceAsStream( "/admin/resources/images/background.jpg" );
                ByteStreams.copy( image, response.getOutputStream() );
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Cannot stream background image!", e );
        }
    }
}

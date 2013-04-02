package com.enonic.wem.web.rest.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import com.enonic.wem.core.config.ConfigProperties;

@Component
@Path("ui")
public final class BackgroundImageResource
{
    private final static Logger LOG = LoggerFactory.getLogger( BackgroundImageResource.class );

    private ConfigProperties configProperties;

    @javax.ws.rs.core.Context
    private ServletContext servletContext;

    public BackgroundImageResource()
        throws Exception
    {
    }

    @GET
    @Path("background.jpg")
    @Produces("image/jpg")
    public Response streamBackgroundImage()
    {
        final StreamingOutput stream = new StreamingOutput()
        {
            @Override
            public void write( OutputStream os )
                throws IOException, WebApplicationException
            {
                try
                {
                    final String backgroundImagePath = configProperties.get( "cms.home" ).toString() + "/custom/background.jpg";

                    final File source = new File( backgroundImagePath );
                    if ( source.exists() )
                    {
                        Files.copy( source, os );
                    }
                    else
                    {
                        final InputStream image = servletContext.getResourceAsStream( "/META-INF/admin/background.jpg" );
                        ByteStreams.copy( image, os );
                    }
                }
                catch ( Exception e )
                {
                    LOG.error( "Cannot stream background image!", e );
                }
            }
        };

        return javax.ws.rs.core.Response.ok( stream ).build();
    }

    @Inject
    public void setConfigProperties( final ConfigProperties configProperties )
    {
        this.configProperties = configProperties;
    }

}

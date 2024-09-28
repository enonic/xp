package com.enonic.xp.admin.impl.portal.widget;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Instant;

import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.google.common.base.Strings.isNullOrEmpty;

public class GetWidgetIconHandler
{
    private final WidgetDescriptorService widgetDescriptorService;

    private final ApplicationDescriptorService applicationDescriptorService;

    public GetWidgetIconHandler( WidgetDescriptorService widgetDescriptorService,
                                 final ApplicationDescriptorService applicationDescriptorService )
    {
        this.widgetDescriptorService = widgetDescriptorService;
        this.applicationDescriptorService = applicationDescriptorService;
    }

    public WebResponse handle( final WebRequest webRequest )
    {
        final String appKeyStr = webRequest.getParams().get( "app" ).iterator().next();
        final String descriptorName = webRequest.getParams().get( "widget" ).iterator().next();
        final String hash = webRequest.getParams().containsKey( "hash" ) ? webRequest.getParams().get( "hash" ).iterator().next() : null;

        final ApplicationKey appKey = ApplicationKey.from( appKeyStr );
        final DescriptorKey descriptorKey = DescriptorKey.from( appKey, descriptorName );
        final WidgetDescriptor widgetDescriptor = this.widgetDescriptorService.getByKey( descriptorKey );
        final Icon icon = widgetDescriptor == null ? null : widgetDescriptor.getIcon();

        final WebResponse.Builder responseBuilder;
        if ( icon == null )
        {
            final ApplicationDescriptor appDescriptor = applicationDescriptorService.get( appKey );
            final Icon appIcon = appDescriptor == null ? null : appDescriptor.getIcon();

            if ( appIcon == null )
            {
                final Icon defaultAppIcon = loadDefaultIcon( "widget" );
                responseBuilder = WebResponse.create()
                    .status( HttpStatus.OK )
                    .body( defaultAppIcon.asInputStream() )
                    .header( HttpHeaders.CACHE_CONTROL, "max-age=3600" )
                    .contentType( MediaType.parse( defaultAppIcon.getMimeType() ) );
            }
            else
            {
                responseBuilder = WebResponse.create()
                    .status( HttpStatus.OK )
                    .body( appIcon.toByteArray() )
                    .contentType( MediaType.parse( appIcon.getMimeType() ) );
                if ( !isNullOrEmpty( hash ) )
                {
                    responseBuilder.header( HttpHeaders.CACHE_CONTROL, "max-age=3600" );
                }
            }
        }
        else
        {
            responseBuilder = WebResponse.create()
                .status( HttpStatus.OK )
                .body( icon.toByteArray() )
                .contentType( MediaType.parse( icon.getMimeType() ) );
            if ( !isNullOrEmpty( hash ) )
            {
                responseBuilder.header( HttpHeaders.CACHE_CONTROL, "max-age=3600" );
            }
        }

        return responseBuilder.build();
    }

    protected final Icon loadDefaultIcon( final String iconName )
    {
        final byte[] image = loadDefaultImage( iconName );
        return Icon.from( image, "image/svg+xml", Instant.ofEpochMilli( 0L ) );
    }

    protected final byte[] loadDefaultImage( final String imageName )
    {
        try (InputStream in = getClass().getResourceAsStream( imageName + ".svg" ))
        {
            if ( in == null )
            {
                throw new IllegalArgumentException( "Image [" + imageName + "] not found" );
            }

            return ByteStreams.toByteArray( in );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( "Failed to load default image: " + imageName, e );
        }
    }
}

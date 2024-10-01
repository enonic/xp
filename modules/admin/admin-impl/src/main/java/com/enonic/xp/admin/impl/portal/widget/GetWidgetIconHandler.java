package com.enonic.xp.admin.impl.portal.widget;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(immediate = true, service = GetWidgetIconHandler.class)
public class GetWidgetIconHandler
{
    private final WidgetDescriptorService widgetDescriptorService;

    private final WidgetIconResolver widgetIconResolver;

    @Activate
    public GetWidgetIconHandler( @Reference final WidgetDescriptorService widgetDescriptorService,
                                 @Reference final WidgetIconResolver widgetIconResolver )
    {
        this.widgetDescriptorService = widgetDescriptorService;
        this.widgetIconResolver = widgetIconResolver;
    }

    public WebResponse handle( final WebRequest webRequest )
    {
        final Multimap<String, String> params = webRequest.getParams();

        final String appKeyStr = params.get( "app" ).iterator().next();
        final String descriptorName = params.get( "widget" ).iterator().next();
        final String version = params.containsKey( "v" ) ? params.get( "v" ).iterator().next() : null;

        final DescriptorKey descriptorKey = DescriptorKey.from( resolveApplicationKey( appKeyStr ), descriptorName );
        final WidgetDescriptor widgetDescriptor = this.widgetDescriptorService.getByKey( descriptorKey );

        final Icon icon = widgetIconResolver.resolve( widgetDescriptor );

        final WebResponse.Builder<?> responseBuilder =
            WebResponse.create().status( HttpStatus.OK ).body( icon.toByteArray() ).contentType( MediaType.parse( icon.getMimeType() ) );

        if ( Objects.equals( IconHashResolver.resolve( icon ), version ) )
        {
            responseBuilder.header( HttpHeaders.CACHE_CONTROL, "public, max-age=" + 60 * 60 * 24 * 365 + ", immutable" );
        }

        return responseBuilder.build();
    }

    private ApplicationKey resolveApplicationKey( final String value )
    {
        try
        {
            return ApplicationKey.from( value );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( String.format( "Invalid application key: %s", value ), e );
        }
    }
}

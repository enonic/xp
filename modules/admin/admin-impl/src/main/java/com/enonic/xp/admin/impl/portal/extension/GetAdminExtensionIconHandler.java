package com.enonic.xp.admin.impl.portal.extension;

import java.util.Iterator;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(immediate = true, service = GetAdminExtensionIconHandler.class)
public class GetAdminExtensionIconHandler
{
    private static final MediaType SVG = MediaType.SVG_UTF_8.withoutParameters();

    private static final String SVG_CONTENT_SECURITY_POLICY =
        "sandbox; default-src 'none'; base-uri 'none'; form-action 'none'; style-src 'self' 'unsafe-inline'";

    private final AdminExtensionDescriptorService descriptorService;

    private final AdminExtensionIconResolver extensionIconResolver;

    @Activate
    public GetAdminExtensionIconHandler( @Reference final AdminExtensionDescriptorService descriptorService,
                                         @Reference final AdminExtensionIconResolver extensionIconResolver )
    {
        this.descriptorService = descriptorService;
        this.extensionIconResolver = extensionIconResolver;
    }

    public WebResponse handle( final WebRequest webRequest )
    {
        final Multimap<String, String> params = webRequest.getParams();

        final String appKeyStr = requireParam( params, "app" );
        final String descriptorName = requireParam( params, "extension" );
        final String version = params.containsKey( "v" ) ? params.get( "v" ).iterator().next() : null;

        final DescriptorKey descriptorKey = resolveDescriptorKey( resolveApplicationKey( appKeyStr ), descriptorName );
        final AdminExtensionDescriptor descriptor = this.descriptorService.getByKey( descriptorKey );
        if ( descriptor == null )
        {
            throw WebException.notFound( String.format( "Extension [%s] not found", descriptorKey ) );
        }

        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
        if ( !descriptor.isAccessAllowed( principals ) )
        {
            throw WebException.forbidden( String.format( "You don't have permission to access [%s]", descriptorKey ) );
        }

        final Icon icon = extensionIconResolver.resolve( descriptor );
        final MediaType contentType = MediaType.parse( icon.getMimeType() );

        final WebResponse.Builder<?> responseBuilder = WebResponse.create()
            .status( HttpStatus.OK )
            .body( icon.toByteArray() )
            .contentType( contentType )
            .header( HttpHeaders.X_CONTENT_TYPE_OPTIONS, "nosniff" );

        if ( contentType.is( SVG ) )
        {
            responseBuilder.header( HttpHeaders.CONTENT_SECURITY_POLICY, SVG_CONTENT_SECURITY_POLICY );
        }

        if ( Objects.equals( IconHashResolver.resolve( icon ), version ) )
        {
            responseBuilder.header( HttpHeaders.CACHE_CONTROL, "public, max-age=" + 60 * 60 * 24 * 365 + ", immutable" );
        }

        return responseBuilder.build();
    }

    private static String requireParam( final Multimap<String, String> params, final String name )
    {
        final Iterator<String> values = params.get( name ).iterator();
        if ( !values.hasNext() )
        {
            throw WebException.badRequest( String.format( "Missing parameter: %s", name ) );
        }
        return values.next();
    }

    private static DescriptorKey resolveDescriptorKey( final ApplicationKey applicationKey, final String name )
    {
        try
        {
            return DescriptorKey.from( applicationKey, name );
        }
        catch ( IllegalArgumentException e )
        {
            throw WebException.badRequest( String.format( "Invalid extension name: %s", name ), e );
        }
    }

    private ApplicationKey resolveApplicationKey( final String value )
    {
        try
        {
            return ApplicationKey.from( value );
        }
        catch ( Exception e )
        {
            throw WebException.badRequest( String.format( "Invalid application key: %s", value ), e );
        }
    }
}

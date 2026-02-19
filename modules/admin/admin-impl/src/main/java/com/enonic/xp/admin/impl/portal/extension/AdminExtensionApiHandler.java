package com.enonic.xp.admin.impl.portal.extension;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.admin.impl.portal.AdminToolPortalHandler;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(immediate = true, service = AdminExtensionApiHandler.class)
public class AdminExtensionApiHandler
{
    private static final Pattern EXTENSION_API_PATTERN = Pattern.compile( "^/admin:extension/(?<descriptor>[^/]+:[^/]+)" );

    private static final String GENERIC_EXTENSION_INTERFACE = "generic";

    private final AdminExtensionDescriptorService descriptorService;

    private final ControllerScriptFactory controllerScriptFactory;

    private final AdminToolDescriptorService adminToolDescriptorService;

    @Activate
    public AdminExtensionApiHandler( @Reference final AdminExtensionDescriptorService descriptorService,
                                     @Reference final ControllerScriptFactory controllerScriptFactory,
                                     @Reference final AdminToolDescriptorService adminToolDescriptorService )
    {
        this.descriptorService = descriptorService;
        this.controllerScriptFactory = controllerScriptFactory;
        this.adminToolDescriptorService = adminToolDescriptorService;
    }

    public WebResponse handle( final WebRequest webRequest )
    {
        final String path = Objects.requireNonNull( webRequest.getEndpointPath(), "Endpoint path cannot be null" );

        final Matcher matcher = EXTENSION_API_PATTERN.matcher( path );

        if ( !matcher.find() )
        {
            throw WebException.notFound( "Invalid Extension API path: " + path );
        }

        final DescriptorKey descriptorKey = resolveDescriptorKey( matcher.group( "descriptor" ) );

        final AdminExtensionDescriptor descriptor = descriptorService.getByKey( descriptorKey );
        if ( descriptor == null )
        {
            throw WebException.notFound( String.format( "Extension [%s] not found", descriptorKey ) );
        }

        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
        if ( !descriptor.isAccessAllowed( principals ) )
        {
            throw WebException.forbidden( String.format( "You don't have permission to access [%s]", descriptorKey ) );
        }

        verifyMounts( descriptor, webRequest );

        final PortalRequest portalRequest = createPortalRequest( webRequest, descriptorKey );

        final ResourceKey script = ResourceKey.from( descriptorKey.getApplicationKey(),
                                                     "admin/extensions/" + descriptorKey.getName() + "/" + descriptorKey.getName() +
                                                         ".js" );

        return controllerScriptFactory.fromScript( script ).execute( portalRequest );
    }

    private void verifyMounts( final AdminExtensionDescriptor descriptor, final WebRequest webRequest )
    {
        if ( descriptor.hasInterface( GENERIC_EXTENSION_INTERFACE ) )
        {
            return;
        }

        final Matcher toolMatcher = AdminToolPortalHandler.ADMIN_TOOL_PATH_PATTERN.matcher( webRequest.getBasePath() );
        if ( toolMatcher.find() )
        {
            final DescriptorKey toolDescriptorKey =
                DescriptorKey.from( resolveApplicationKey( toolMatcher.group( "app" ) ), toolMatcher.group( "tool" ) );
            final AdminToolDescriptor adminToolDescriptor = adminToolDescriptorService.getByKey( toolDescriptorKey );
            if ( adminToolDescriptor == null || descriptor.getInterfaces().stream().noneMatch( adminToolDescriptor::hasInterface ) )
            {
                throw WebException.notFound(
                    String.format( "Extension [%s] is not mounted to admin tool [%s]", descriptor.getKey(), toolDescriptorKey ) );
            }
        }
        else
        {
            throw WebException.notFound( String.format( "Invalid admin tool URL [%s]", webRequest.getRawPath() ) );
        }
    }

    private PortalRequest createPortalRequest( final WebRequest webRequest, final DescriptorKey descriptorKey )
    {
        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

        portalRequest.setApplicationKey( descriptorKey.getApplicationKey() );

        return portalRequest;
    }

    private ApplicationKey resolveApplicationKey( final String value )
    {
        try
        {
            return ApplicationKey.from( value );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Invalid application key: " + value, e );
        }
    }

    private DescriptorKey resolveDescriptorKey( final String value )
    {
        try
        {
            return DescriptorKey.from( value );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Invalid descriptor key: " + value, e );
        }
    }
}

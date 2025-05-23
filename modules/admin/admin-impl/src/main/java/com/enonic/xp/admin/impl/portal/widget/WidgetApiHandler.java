package com.enonic.xp.admin.impl.portal.widget;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
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

@Component(immediate = true, service = WidgetApiHandler.class)
public class WidgetApiHandler
{
    private static final Pattern WIDGET_API_PATTERN = Pattern.compile( "^/(_|api)/admin:widget/(?<appKey>[^/]+)/(?<widgetKey>[^/]+)" );

    private static final Pattern TOOL_PREFIX_PATTERN = Pattern.compile( "^/admin/(?<appKey>[^/]+)/(?<toolName>[^/]+)" );

    private static final String GENERIC_WIDGET_INTERFACE = "generic";

    private final WidgetDescriptorService widgetDescriptorService;

    private final ControllerScriptFactory controllerScriptFactory;

    private final AdminToolDescriptorService adminToolDescriptorService;

    @Activate
    public WidgetApiHandler( @Reference final WidgetDescriptorService widgetDescriptorService,
                             @Reference final ControllerScriptFactory controllerScriptFactory,
                             @Reference final AdminToolDescriptorService adminToolDescriptorService )
    {
        this.widgetDescriptorService = widgetDescriptorService;
        this.controllerScriptFactory = controllerScriptFactory;
        this.adminToolDescriptorService = adminToolDescriptorService;
    }

    public WebResponse handle( final WebRequest webRequest )
    {
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );

        final Matcher matcher = WIDGET_API_PATTERN.matcher( path );

        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Invalid Widget API path: " + path );
        }

        final DescriptorKey descriptorKey =
            DescriptorKey.from( resolveApplicationKey( matcher.group( "appKey" ) ), matcher.group( "widgetKey" ) );

        final WidgetDescriptor widgetDescriptor = widgetDescriptorService.getByKey( descriptorKey );
        if ( widgetDescriptor == null )
        {
            throw WebException.notFound( String.format( "Widget [%s] not found", descriptorKey ) );
        }

        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
        if ( !widgetDescriptor.isAccessAllowed( principals ) )
        {
            throw WebException.forbidden( String.format( "You don't have permission to access [%s]", descriptorKey ) );
        }

        verifyMounts( widgetDescriptor, webRequest );

        final PortalRequest portalRequest = createPortalRequest( webRequest, descriptorKey );

        final ResourceKey script = ResourceKey.from( descriptorKey.getApplicationKey(),
                                                     "admin/widgets/" + descriptorKey.getName() + "/" + descriptorKey.getName() + ".js" );

        return controllerScriptFactory.fromScript( script ).execute( portalRequest );
    }

    private void verifyMounts( final WidgetDescriptor widgetDescriptor, final WebRequest webRequest )
    {
        if ( !widgetDescriptor.hasInterface( GENERIC_WIDGET_INTERFACE ) && webRequest.getEndpointPath() != null )
        {
            final Matcher toolMatcher = TOOL_PREFIX_PATTERN.matcher( webRequest.getRawPath() );
            if ( toolMatcher.find() )
            {
                final DescriptorKey toolDescriptorKey =
                    DescriptorKey.from( resolveApplicationKey( toolMatcher.group( "appKey" ) ), toolMatcher.group( "toolName" ) );
                final AdminToolDescriptor adminToolDescriptor = adminToolDescriptorService.getByKey( toolDescriptorKey );
                if ( adminToolDescriptor != null && !adminToolDescriptor.getInterfaces().isEmpty() &&
                    widgetDescriptor.getInterfaces().stream().noneMatch( adminToolDescriptor::hasInterface ) )
                {
                    throw WebException.notFound(
                        String.format( "Widget [%s] is not mounted to admin tool [%s]", widgetDescriptor.getKey(), toolDescriptorKey ) );
                }
            }
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
}

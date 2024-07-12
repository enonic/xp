package com.enonic.xp.admin.impl.portal;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.api.ApiHandler;

@Component(immediate = true, service = ApiHandler.class, property = ApiHandler.APPLICATION_KEY_PROPERTY + "=widget")
public class ApiWidgetHandler
    implements ApiHandler
{
    private static final Pattern WIDGET_API_PATTERN = Pattern.compile( "^/(_|api)/widget/(?<appKey>[^/]+)/(?<widgetKey>[^/]+)" );

    private final ApiDescriptor descriptor;

    private final ControllerScriptFactory controllerScriptFactory;

    private final WidgetDescriptorService widgetDescriptorService;

    @Activate
    public ApiWidgetHandler( @Reference final ControllerScriptFactory controllerScriptFactory,
                             @Reference final WidgetDescriptorService widgetDescriptorService, final Map<String, ?> properties )
    {
        this.controllerScriptFactory = controllerScriptFactory;
        this.widgetDescriptorService = widgetDescriptorService;
        this.descriptor = resolveApiDescriptor( properties );
    }

    @Override
    public ApiDescriptor getDescriptor()
    {
        return descriptor;
    }

    @Override
    public WebResponse handle( final WebRequest webRequest )
        throws Exception
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

        final PortalRequest portalRequest = createPortalRequest( webRequest, descriptorKey );

        final ResourceKey script = ResourceKey.from( descriptorKey.getApplicationKey(),
                                                     "admin/widgets/" + descriptorKey.getName() + "/" + descriptorKey.getName() + ".js" );

        return controllerScriptFactory.fromScript( script ).execute( portalRequest );
    }

    private ApiDescriptor resolveApiDescriptor( final Map<String, ?> properties )
    {
        final ApiDescriptor.Builder builder = ApiDescriptor.create();

        builder.key( DescriptorKey.from( resolveApplicationKey( (String) properties.get( APPLICATION_KEY_PROPERTY ) ),
                                         Objects.requireNonNullElse( (String) properties.get( API_KEY_PROPERTY ), DEFAULT_API_KEY ) ) );
        builder.allowedPrincipals( PrincipalKeys.from( RoleKeys.ADMIN_LOGIN, RoleKeys.ADMIN ) );

        return builder.build();
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

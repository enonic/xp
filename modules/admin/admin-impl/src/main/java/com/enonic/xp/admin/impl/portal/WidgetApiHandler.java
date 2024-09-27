package com.enonic.xp.admin.impl.portal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;
import com.google.common.net.UrlEscapers;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.universalapi.UniversalApiHandler;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component(immediate = true, service = UniversalApiHandler.class, property = {"applicationKey=admin", "apiKey=widget",
    "allowedPrincipals=role:system.admin.login", "allowedPrincipals=role:system.admin"})
public class WidgetApiHandler
    implements UniversalApiHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( WidgetApiHandler.class );

    private static final Pattern WIDGET_API_PATTERN = Pattern.compile( "^/(_|api)/admin/widget/(?<appKey>[^/]+)/(?<widgetKey>[^/]+)" );
    private static final Pattern LIST_WIDGETS_API_PATTERN = Pattern.compile( "^/(_|api)/admin/widget/?$" );

    private static final Pattern TOOL_PREFIX_PATTERN = Pattern.compile( "^/admin/(?<appKey>[^/]+)/(?<toolName>[^/]+)" );

    private static final String GENERIC_WIDGET_INTERFACE = "generic";

    private final ControllerScriptFactory controllerScriptFactory;

    private final WidgetDescriptorService widgetDescriptorService;

    private final AdminToolDescriptorService adminToolDescriptorService;

    private final LocaleService localeService;

    private final PortalUrlService portalUrlService;

    @Activate
    public WidgetApiHandler( @Reference final ControllerScriptFactory controllerScriptFactory,
                             @Reference final WidgetDescriptorService widgetDescriptorService,
                             @Reference final AdminToolDescriptorService adminToolDescriptorService,
                             @Reference final LocaleService localeService,
                             @Reference final PortalUrlService portalUrlService)
    {
        this.controllerScriptFactory = controllerScriptFactory;
        this.widgetDescriptorService = widgetDescriptorService;
        this.adminToolDescriptorService = adminToolDescriptorService;
        this.localeService = localeService;
        this.portalUrlService = portalUrlService;
    }

    @Override
    public WebResponse handle( final WebRequest webRequest )
    {
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );

        Matcher matcher = LIST_WIDGETS_API_PATTERN.matcher( path );

        if ( matcher.matches()  )
        {
//            /api/admin/widget?appKey=app&widgetName=widgetName&icon
//            /api/admin/widget?&widgetInterfaces=i1&widgetInterfaces=i2
            final Collection<String> values = webRequest.getParams().get( "widgetInterfaces" );
            final Descriptors<WidgetDescriptor> widgetDescriptors =
                widgetDescriptorService.getAllowedByInterfaces( values.toArray( new String[0] ) );

            final List<ObjectNode> result = new ArrayList<>();
            if ( widgetDescriptors.isNotEmpty() )
            {
                final String widgetBaseUrl = portalUrlService.apiUrl( new ApiUrlParams().portalRequest( new PortalRequest( webRequest ) )
                                                                          .application( "admin" )
                                                                          .api( "widget" )
                                                                          .type( UrlTypeConstants.ABSOLUTE ) );

                widgetDescriptors.forEach( widgetDescriptor -> {

                    final ObjectNode json = JsonNodeFactory.instance.objectNode();

                    json.put( "key", widgetDescriptor.getKeyString() );
                    json.put( "displayName", widgetDescriptor.getDisplayName() );
                    json.put( "description", widgetDescriptor.getDescription() );

                    if ( widgetDescriptor.getIcon() != null )
                    {
                        final StringBuilder iconUrl = new StringBuilder( widgetBaseUrl );

                        iconUrl.append( "?" );
                        appendParam( iconUrl, "app", widgetDescriptor.getApplicationKey().toString() );
                        iconUrl.append( "&" );
                        appendParam( iconUrl, "widget", widgetDescriptor.getName() );
                        iconUrl.append( "&" );
                        appendParam( iconUrl, "hash", "hash" );
                        iconUrl.append( "&" );
                        appendParam( iconUrl, "icon", null );

                        json.put( "iconUrl", iconUrl.toString() );
                    }

                    json.put( "url",
                              widgetBaseUrl + "/" + widgetDescriptor.getApplicationKey().toString() + "/" + widgetDescriptor.getName() );

                    if ( !isNullOrEmpty( widgetDescriptor.getDisplayNameI18nKey() ) ||
                        !isNullOrEmpty( widgetDescriptor.getDescriptionI18nKey() ) )
                    {
                        final MessageBundle bundle =
                            localeService.getBundle( widgetDescriptor.getApplicationKey(), getLocale( widgetDescriptor.getApplicationKey() ) );

                        addLocalizedJson( json, bundle, "displayName", widgetDescriptor.getDisplayNameI18nKey(),
                                          widgetDescriptor.getDisplayName() );
                        addLocalizedJson( json, bundle, "description", widgetDescriptor.getDescriptionI18nKey(),
                                          widgetDescriptor.getDescription() );
                    }

                    if ( widgetDescriptor.getConfig() != null )
                    {
                        final ObjectNode config = JsonNodeFactory.instance.objectNode();
                        widgetDescriptor.getConfig().forEach( config::put );

                        json.set( "config", config );
                    }

                    if ( widgetDescriptor.getInterfaces() != null )
                    {
                        final ArrayNode interfaces = JsonNodeFactory.instance.arrayNode();
                        widgetDescriptor.getInterfaces().forEach( interfaces::add );
                        json.set( "interfaces", interfaces );
                    }
                    result.add( json );
                } );
            }

            return WebResponse.create().contentType( MediaType.JSON_UTF_8 ).body( result ).build();
        }
        else
        {
            matcher = WIDGET_API_PATTERN.matcher( path );

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
                                                         "admin/widgets/" + descriptorKey.getName() + "/" + descriptorKey.getName() +
                                                             ".js" );

            return controllerScriptFactory.fromScript( script ).execute( portalRequest );
        }
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

    private void addLocalizedJson( ObjectNode json, MessageBundle bundle, String fieldName, String i18nKey, String value )
    {
        if ( !isNullOrEmpty( i18nKey ) )
        {
            json.put( fieldName, localizeMessage( bundle, i18nKey, value ) );
        }
    }

    private String localizeMessage( final MessageBundle bundle, final String key, final String defaultValue )
    {
        if ( bundle == null )
        {
            return defaultValue;
        }
        if ( key == null )
        {
            return defaultValue;
        }

        final String localizedValue;
        try
        {
            localizedValue = bundle.localize( key );
        }
        catch ( IllegalArgumentException e )
        {
            LOG.error( "Error on localization of message with key [{}].", key, e );
            return bundle.getMessage( key );
        }

        return localizedValue != null ? localizedValue : defaultValue;
    }

    private void appendParam( final StringBuilder url, final String name, final String value )
    {
        url.append( urlEncode( name ) );
        if ( value != null )
        {
            url.append( "=" ).append( urlEncode( value ) );
        }
    }

    private String urlEncode( final String value )
    {
        return UrlEscapers.urlFormParameterEscaper().escape( value );
    }

    private Locale getLocale( final ApplicationKey applicationKey )
    {
        final HttpServletRequest req = ServletRequestHolder.getRequest();
        if ( req == null )
        {
            return null;
        }

        return localeService.getSupportedLocale( Collections.list( req.getLocales() ), applicationKey );
    }
}

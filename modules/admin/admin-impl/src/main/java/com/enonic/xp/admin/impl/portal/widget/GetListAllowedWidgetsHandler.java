package com.enonic.xp.admin.impl.portal.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component(immediate = true, service = GetListAllowedWidgetsHandler.class)
public class GetListAllowedWidgetsHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( GetListAllowedWidgetsHandler.class );

    private final WidgetDescriptorService widgetDescriptorService;

    private final LocaleService localeService;

    private final WidgetIconResolver widgetIconResolver;

    @Activate
    public GetListAllowedWidgetsHandler( @Reference final WidgetDescriptorService widgetDescriptorService,
                                         @Reference final LocaleService localeService,
                                         @Reference final WidgetIconResolver widgetIconResolver )
    {
        this.widgetDescriptorService = widgetDescriptorService;
        this.localeService = localeService;
        this.widgetIconResolver = widgetIconResolver;
    }

    public WebResponse handle( final WebRequest webRequest )
    {
        final Collection<String> values = webRequest.getParams().get( "widgetInterface" );

        final PrincipalKeys userPrincipalKeys = ContextAccessor.current().getAuthInfo().getPrincipals();
        final Descriptors<WidgetDescriptor> widgetDescriptors = widgetDescriptorService.getByInterfaces( values.toArray( String[]::new ) )
            .filter( widgetDescriptor -> widgetDescriptor.isAccessAllowed( userPrincipalKeys ) );

        final List<ObjectNode> result = new ArrayList<>();
        if ( widgetDescriptors.isNotEmpty() )
        {
            final PortalRequest portalRequest = (PortalRequest) webRequest;
            final List<Locale> preferredLocales = Collections.list( portalRequest.getRawRequest().getLocales() );
            widgetDescriptors.forEach( widgetDescriptor -> result.add( convertToJson( widgetDescriptor, preferredLocales ) ) );
        }

        return WebResponse.create().contentType( MediaType.JSON_UTF_8 ).body( result ).build();
    }

    private ObjectNode convertToJson( final WidgetDescriptor widgetDescriptor, final List<Locale> preferredLocales )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put( "key", widgetDescriptor.getKeyString() );
        json.put( "displayName", widgetDescriptor.getDisplayName() );
        json.put( "description", widgetDescriptor.getDescription() );
        json.put( "iconUrl", resolveIconUrl( widgetDescriptor ) );

        json.put( "url", widgetDescriptor.getApplicationKey().toString() + "/" + widgetDescriptor.getName() );

        if ( !isNullOrEmpty( widgetDescriptor.getDisplayNameI18nKey() ) || !isNullOrEmpty( widgetDescriptor.getDescriptionI18nKey() ) )
        {
            final MessageBundle bundle = localeService.getBundle( widgetDescriptor.getApplicationKey(),
                                                                  localeService.getSupportedLocale( preferredLocales,
                                                                                                    widgetDescriptor.getApplicationKey() ) );

            addLocalizedJson( json, bundle, "displayName", widgetDescriptor.getDisplayNameI18nKey(), widgetDescriptor.getDisplayName() );
            addLocalizedJson( json, bundle, "description", widgetDescriptor.getDescriptionI18nKey(), widgetDescriptor.getDescription() );
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

        return json;
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
        if ( bundle == null || key == null )
        {
            return defaultValue;
        }

        try
        {
            return Objects.requireNonNullElse( bundle.localize( key ), defaultValue );
        }
        catch ( IllegalArgumentException e )
        {
            LOG.error( "Error on localization of message with key [{}].", key, e );
            return bundle.getMessage( key );
        }
    }

    private String resolveIconUrl( final WidgetDescriptor widgetDescriptor )
    {
        final StringBuilder iconUrl = new StringBuilder();

        iconUrl.append( "?" );
        appendParam( iconUrl, "icon", null );
        iconUrl.append( "&" );
        appendParam( iconUrl, "app", widgetDescriptor.getApplicationKey().toString() );
        iconUrl.append( "&" );
        appendParam( iconUrl, "widget", widgetDescriptor.getName() );

        final Icon icon = widgetIconResolver.resolve( widgetDescriptor );
        iconUrl.append( "&" );
        appendParam( iconUrl, "v", IconHashResolver.resolve( icon ) );

        return iconUrl.toString();
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
}

package com.enonic.xp.admin.impl.portal.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.hash.Hashing;
import com.google.common.net.MediaType;
import com.google.common.net.UrlEscapers;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static com.google.common.base.Strings.isNullOrEmpty;

public class GetListAllowedWidgetsHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( GetListAllowedWidgetsHandler.class );

    private final WidgetDescriptorService widgetDescriptorService;

    private final PortalUrlService portalUrlService;

    private final LocaleService localeService;

    public GetListAllowedWidgetsHandler( WidgetDescriptorService widgetDescriptorService, PortalUrlService portalUrlService,
                                         LocaleService localeService )
    {
        this.widgetDescriptorService = widgetDescriptorService;
        this.portalUrlService = portalUrlService;
        this.localeService = localeService;
    }

    public WebResponse handle( final WebRequest webRequest )
    {
        final Collection<String> values = webRequest.getParams().get( "widgetInterfaces" );
        final Descriptors<WidgetDescriptor> widgetDescriptors =
            widgetDescriptorService.getAllowedByInterfaces( values.toArray( new String[0] ) );

        final List<ObjectNode> result = new ArrayList<>();
        if ( widgetDescriptors.isNotEmpty() )
        {
            final String widgetApiBaseUrl = portalUrlService.apiUrl( new ApiUrlParams().portalRequest( new PortalRequest( webRequest ) )
                                                                         .application( "admin" )
                                                                         .api( "widget" )
                                                                         .type( UrlTypeConstants.ABSOLUTE ) );

            widgetDescriptors.forEach( widgetDescriptor -> {

                final ObjectNode json = JsonNodeFactory.instance.objectNode();

                json.put( "key", widgetDescriptor.getKeyString() );
                json.put( "displayName", widgetDescriptor.getDisplayName() );
                json.put( "description", widgetDescriptor.getDescription() );
                json.put( "iconUrl", widgetDescriptor.getIcon() != null
                    ? new WidgetIconUrlResolver( widgetDescriptor, widgetApiBaseUrl ).resolve()
                    : null );

                json.put( "url",
                          widgetApiBaseUrl + "/" + widgetDescriptor.getApplicationKey().toString() + "/" + widgetDescriptor.getName() );

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

    private Locale getLocale( final ApplicationKey applicationKey )
    {
        final HttpServletRequest req = ServletRequestHolder.getRequest();
        if ( req == null )
        {
            return null;
        }

        return localeService.getSupportedLocale( Collections.list( req.getLocales() ), applicationKey );
    }

    private static class WidgetIconUrlResolver
    {
        private final WidgetDescriptor widgetDescriptor;

        private final String widgetBaseUrl;

        private WidgetIconUrlResolver( WidgetDescriptor widgetDescriptor, String widgetBaseUrl )
        {
            this.widgetDescriptor = widgetDescriptor;
            this.widgetBaseUrl = widgetBaseUrl;
        }

        String resolve()
        {
            final StringBuilder iconUrl = new StringBuilder( widgetBaseUrl );

            iconUrl.append( "?" );
            appendParam( iconUrl, "icon", null );
            iconUrl.append( "&" );
            appendParam( iconUrl, "app", widgetDescriptor.getApplicationKey().toString() );
            iconUrl.append( "&" );
            appendParam( iconUrl, "widget", widgetDescriptor.getName() );
            iconUrl.append( "&" );
            final byte[] iconData = widgetDescriptor.getIcon().toByteArray();
            appendParam( iconUrl, "hash", Hashing.md5().hashBytes( iconData ).toString() );

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
}

package com.enonic.xp.admin.impl.portal.extension;

import java.util.Collection;
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

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component(immediate = true, service = GetListAllowedAdminExtensionsHandler.class)
public class GetListAllowedAdminExtensionsHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( GetListAllowedAdminExtensionsHandler.class );

    private final AdminExtensionDescriptorService descriptorService;

    private final LocaleService localeService;

    private final AdminExtensionIconResolver extensionIconResolver;

    @Activate
    public GetListAllowedAdminExtensionsHandler( @Reference final AdminExtensionDescriptorService descriptorService,
                                                 @Reference final LocaleService localeService,
                                                 @Reference final AdminExtensionIconResolver extensionIconResolver )
    {
        this.descriptorService = descriptorService;
        this.localeService = localeService;
        this.extensionIconResolver = extensionIconResolver;
    }

    public WebResponse handle( final WebRequest webRequest )
    {
        final Collection<String> values = webRequest.getParams().get( "interface" );

        final PrincipalKeys userPrincipalKeys = ContextAccessor.current().getAuthInfo().getPrincipals();
        final List<ObjectNode> result = descriptorService.getByInterfaces( values.toArray( String[]::new ) )
            .stream()
            .filter( wd -> wd.isAccessAllowed( userPrincipalKeys ) )
            .map( wd -> convertToJson( wd, webRequest.getLocales() ) )
            .toList();
        return WebResponse.create().contentType( MediaType.JSON_UTF_8 ).body( result ).build();
    }

    private ObjectNode convertToJson( final AdminExtensionDescriptor descriptor, final List<Locale> preferredLocales )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put( "key", descriptor.getKey().toString() );
        json.put( "displayName", descriptor.getDisplayName() );
        json.put( "description", descriptor.getDescription() );
        json.put( "iconUrl", resolveIconUrl( descriptor ) );

        json.put( "url", descriptor.getApplicationKey() + ":" + descriptor.getName() );

        if ( !isNullOrEmpty( descriptor.getDisplayNameI18nKey() ) || !isNullOrEmpty( descriptor.getDescriptionI18nKey() ) )
        {
            final MessageBundle bundle = localeService.getBundle( descriptor.getApplicationKey(),
                                                                  localeService.getSupportedLocale( preferredLocales,
                                                                                                    descriptor.getApplicationKey() ) );

            addLocalizedJson( json, bundle, "displayName", descriptor.getDisplayNameI18nKey(), descriptor.getDisplayName() );
            addLocalizedJson( json, bundle, "description", descriptor.getDescriptionI18nKey(), descriptor.getDescription() );
        }

        if ( descriptor.getConfig() != null )
        {
            final ObjectNode config = JsonNodeFactory.instance.objectNode();
            descriptor.getConfig().forEach( config::put );

            json.set( "config", config );
        }

        if ( descriptor.getInterfaces() != null )
        {
            final ArrayNode interfaces = JsonNodeFactory.instance.arrayNode();
            descriptor.getInterfaces().forEach( interfaces::add );
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

    private String resolveIconUrl( final AdminExtensionDescriptor descriptor )
    {
        final StringBuilder iconUrl = new StringBuilder();

        iconUrl.append( "?" );
        appendParam( iconUrl, "icon", null );
        iconUrl.append( "&" );
        appendParam( iconUrl, "app", descriptor.getApplicationKey().toString() );
        iconUrl.append( "&" );
        appendParam( iconUrl, "extension", descriptor.getName() );

        final Icon icon = extensionIconResolver.resolve( descriptor );
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

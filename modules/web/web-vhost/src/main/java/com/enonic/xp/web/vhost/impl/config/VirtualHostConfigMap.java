package com.enonic.xp.web.vhost.impl.config;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static com.google.common.base.Strings.isNullOrEmpty;

final class VirtualHostConfigMap
{
    private static final String DEFAULT_ID_PROVIDER_VALUE = "default";

    private static final String ENABLED_ID_PROVIDER_VALUE = "enabled";

    private static final String FLOWS_ID_PROVIDER_SETTING = "flows";

    private static final String API_CONTEXT_PREFIX = "api.";

    private static final String API_REST_ENABLED = "rest.enabled";

    private static final List<HttpStatus> DENIED_STATUSES =
        List.of( HttpStatus.UNAUTHORIZED, HttpStatus.PAYMENT_REQUIRED, HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND );

    private static final Pattern MAPPING_NAME_PATTERN = Pattern.compile( "mapping\\.(?<name>[^.]+)\\..+" );

    // Endpoints are configured by their documented names, not the internal connector names.
    private static final Map<String, String> ENDPOINTS =
        Map.of( "web", DispatchConstants.WEB_CONNECTOR, "management", DispatchConstants.MANAGEMENT_CONNECTOR, "statistics",
                DispatchConstants.STATISTICS_CONNECTOR );

    private final Map<String, String> map;

    VirtualHostConfigMap( final Map<String, String> map )
    {
        this.map = map;
    }

    public boolean isEnabled()
    {
        return getBoolean( "enabled", false );
    }

    public List<VirtualHost> buildMappings()
    {
        return findMappingNames().stream()
            .map( this::buildMapping )
            .sorted( Comparator.comparing( VirtualHost::getOrder )
                         .thenComparing( VirtualHost::getSource, Comparator.comparing( String::length ).reversed() )
                         .thenComparing( VirtualHost::getSource ) )
            .collect( Collectors.toUnmodifiableList() );
    }

    private VirtualHostMapping buildMapping( final String name )
    {

        final String prefix = "mapping." + name + ".";
        final String hostString = getString( prefix + "host" );
        final String host = hostString != null ? hostString : "localhost";

        final String source = normalizePath( getString( prefix + "source" ) );
        final String target = normalizePath( getString( prefix + "target" ) );
        final VirtualHostIdProvidersMapping idProvidersMapping = getHostIdProvidersMapping( prefix );
        final int order = getInt( prefix + "order", Integer.MAX_VALUE );
        final Map<String, String> context = getVirtualHostContext( prefix );
        final String connector = getConnector( prefix );
        final PrincipalKeys allowedPrincipals = getAllowedPrincipals( prefix );
        final Set<String> audience = getAudience( prefix );
        final HttpStatus anonymousDeniedStatus = getDeniedStatus( prefix + "deniedStatus.anonymous", HttpStatus.UNAUTHORIZED );
        final HttpStatus authenticatedDeniedStatus = getDeniedStatus( prefix + "deniedStatus.authenticated", HttpStatus.FORBIDDEN );

        return new VirtualHostMapping( name, host, source, target, idProvidersMapping, order, context, connector, allowedPrincipals,
                                       audience, anonymousDeniedStatus, authenticatedDeniedStatus );
    }

    private HttpStatus getDeniedStatus( final String key, final HttpStatus defaultStatus )
    {
        final String value = getString( key );
        if ( value == null )
        {
            return defaultStatus;
        }
        return DENIED_STATUSES.stream()
            .filter( status -> Integer.toString( status.value() ).equals( value ) )
            .findFirst()
            .orElseThrow( () -> new IllegalArgumentException(
                "Invalid value [" + value + "] for [" + key + "] in vhost mapping, must be one of 401, 402, 403 or 404" ) );
    }

    private Set<String> getAudience( final String mappingPrefix )
    {
        final String value = getString( mappingPrefix + "audience" );
        if ( value == null )
        {
            return Set.of();
        }
        return list( value ).collect( Collectors.toUnmodifiableSet() );
    }

    private PrincipalKeys getAllowedPrincipals( final String mappingPrefix )
    {
        final String value = getString( mappingPrefix + "allow" );
        if ( value == null || value.isBlank() )
        {
            return PrincipalKeys.empty();
        }

        return Stream.of( value.split( "," ) ).map( String::trim ).map( PrincipalKey::from ).collect( PrincipalKeys.collector() );
    }

    private String getConnector( final String mappingPrefix )
    {
        final String value = getString( mappingPrefix + "endpoint" );
        if ( value == null )
        {
            return DispatchConstants.WEB_CONNECTOR;
        }

        final String connector = ENDPOINTS.get( value );
        if ( connector == null )
        {
            throw new IllegalArgumentException(
                "Unknown endpoint [" + value + "] in vhost mapping, must be one of " + ENDPOINTS.keySet() );
        }
        return connector;
    }

    private VirtualHostIdProvidersMapping getHostIdProvidersMapping( final String mappingPrefix )
    {
        final String idProviderPrefix = mappingPrefix + "idProvider" + ".";

        final Map<String, IdProviderSettings> idProviders = new TreeMap<>();

        // Keys: "idProvider.<name> = default|enabled" enables the provider; "idProvider.<name>.<setting> = <value>"
        // carries its settings. The first segment is the name: an id provider named with a dot cannot be configured.
        getIdProviders( idProviderPrefix ).forEach( ( remainder, value ) -> {
            final int dot = remainder.indexOf( '.' );
            final String idProviderName = dot < 0 ? remainder : remainder.substring( 0, dot );
            final IdProviderSettings settings = idProviders.computeIfAbsent( idProviderName, name -> {
                IdProviderKey.from( name );
                return new IdProviderSettings();
            } );

            if ( dot < 0 )
            {
                switch ( value.trim() )
                {
                    case DEFAULT_ID_PROVIDER_VALUE -> settings.isDefault = true;
                    case ENABLED_ID_PROVIDER_VALUE -> settings.isEnabled = true;
                    default -> throw new IllegalArgumentException(
                        "Invalid value [" + value + "] for id provider [" + idProviderName + "] in vhost mapping, must be [" +
                            DEFAULT_ID_PROVIDER_VALUE + "] or [" + ENABLED_ID_PROVIDER_VALUE + "]" );
                }
                return;
            }

            final String setting = remainder.substring( dot + 1 );
            switch ( setting )
            {
                case FLOWS_ID_PROVIDER_SETTING -> settings.flows =
                    list( value ).map( flow -> flow.toLowerCase( Locale.ROOT ) ).collect( Collectors.toUnmodifiableSet() );
                default -> throw new IllegalArgumentException(
                    "Unknown setting [" + setting + "] for id provider [" + idProviderName + "] in vhost mapping" +
                        " (an id provider whose name contains a dot cannot be configured in a vhost mapping)" );
            }
        } );

        final VirtualHostIdProvidersMapping.Builder hostIdProvidersMapping = VirtualHostIdProvidersMapping.create();

        String defaultIdProvider = null;
        for ( final Map.Entry<String, IdProviderSettings> entry : idProviders.entrySet() )
        {
            final IdProviderSettings settings = entry.getValue();
            if ( settings.isDefault )
            {
                if ( defaultIdProvider != null )
                {
                    throw new IllegalArgumentException(
                        "Multiple default id providers in vhost mapping: [" + defaultIdProvider + "] and [" + entry.getKey() + "]" );
                }
                defaultIdProvider = entry.getKey();
            }
            // Both default and enabled turn the id provider on; absent means off.
            if ( settings.isDefault || settings.isEnabled )
            {
                final IdProviderKey idProviderKey = IdProviderKey.from( entry.getKey() );
                hostIdProvidersMapping.addIdProviderWithSettings( idProviderKey, settings.build() );
                if ( settings.isDefault )
                {
                    hostIdProvidersMapping.setDefaultIdProvider( idProviderKey );
                }
            }
        }

        return hostIdProvidersMapping.build();
    }

    /**
     * The items of a comma-separated setting, trimmed, blanks dropped.
     */
    private static Stream<String> list( final String value )
    {
        return Stream.of( value.split( "," ) ).map( String::trim ).filter( s -> !s.isEmpty() );
    }

    private static final class IdProviderSettings
    {
        boolean isDefault;

        boolean isEnabled;

        Set<String> flows;

        VirtualHostIdProvider build()
        {
            final VirtualHostIdProvider.Builder builder = VirtualHostIdProvider.create();
            if ( flows != null )
            {
                builder.flows( flows );
            }
            return builder.build();
        }
    }

    private Map<String, String> getVirtualHostContext( final String mappingPrefix )
    {
        final String configPrefix = mappingPrefix + "context" + ".";

        final Map<String, String> context = this.map.entrySet()
            .stream()
            .filter( entry -> entry.getKey().startsWith( configPrefix ) )
            .collect( Collectors.toMap( entry -> entry.getKey().replace( configPrefix, "" ), Map.Entry::getValue ) );

        context.keySet().forEach( key -> validateApiContextKey( mappingPrefix, key ) );
        return context;
    }

    /**
     * The {@code api.} context namespace carries the management API policies of a vhost and is validated eagerly: a
     * key that fails to parse would otherwise be ignored, leaving the API it was meant to restrict unrestricted.
     * Accepted forms are {@code api.rest.enabled} and {@code api.<application>:<api>.<setting>}.
     */
    private static void validateApiContextKey( final String mappingPrefix, final String key )
    {
        if ( !key.startsWith( API_CONTEXT_PREFIX ) )
        {
            return;
        }
        final String remainder = key.substring( API_CONTEXT_PREFIX.length() );
        if ( API_REST_ENABLED.equals( remainder ) )
        {
            return;
        }

        final int colon = remainder.indexOf( ':' );
        final int dot = colon < 0 ? -1 : remainder.indexOf( '.', colon );
        if ( colon <= 0 || dot <= colon + 1 || dot == remainder.length() - 1 )
        {
            throw new IllegalArgumentException(
                "Invalid context key [" + mappingPrefix + "context." + key + "] in vhost mapping, expected api.rest.enabled or " +
                    "api.<application>\\:<api>.<setting> (the colon must be escaped in .cfg files)" );
        }
        ApplicationKey.from( remainder.substring( 0, colon ) );
    }

    private Map<String, String> getIdProviders( final String idProviderPrefix )
    {
        // Sorted by name so the id provider iteration order (after the default) is deterministic.
        return this.map.entrySet()
            .stream()
            .filter( entry -> entry.getKey().startsWith( idProviderPrefix ) )
            .collect(
                Collectors.toMap( entry -> entry.getKey().replace( idProviderPrefix, "" ), Map.Entry::getValue, ( a, b ) -> a,
                                  TreeMap::new ) );
    }

    private String getString( final String name )
    {
        final String value = this.map.get( name );
        if ( isNullOrEmpty( value ) )
        {
            return null;
        }

        return value.trim();
    }

    private boolean getBoolean( final String name, final boolean defValue )
    {
        final String value = getString( name );
        return value != null ? "true".equals( value ) : defValue;
    }

    private int getInt( final String name, final int defValue )
    {
        final String value = getString( name );
        return value != null ? Integer.parseInt( value ) : defValue;
    }

    private Set<String> findMappingNames()
    {
        return this.map.keySet().stream().map( this::findMappingName ).filter( Objects::nonNull ).collect( Collectors.toSet() );
    }

    private String findMappingName( final String key )
    {
        final Matcher matcher = MAPPING_NAME_PATTERN.matcher( key );
        if ( !matcher.matches() )
        {
            return null;
        }

        return matcher.group( "name" );
    }

    private String normalizePath( final String value )
    {
        if ( value == null || "/".equals( value ) )
        {
            return "/";
        }

        final StringBuilder result = new StringBuilder();

        if ( !value.startsWith( "/" ) )
        {
            result.append( "/" );
        }

        if ( value.endsWith( "/" ) )
        {
            result.append( value, 0, value.length() - 1 );
        }
        else
        {
            result.append( value );
        }

        return result.toString();
    }
}

package com.enonic.xp.web.vhost.impl.config;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static com.google.common.base.Strings.isNullOrEmpty;

final class VirtualHostConfigMap
{
    private static final String DEFAULT_ID_PROVIDER_VALUE = "default";

    private static final String ENABLED_ID_PROVIDER_VALUE = "enabled";

    private static final Pattern MAPPING_NAME_PATTERN = Pattern.compile( "mapping\\.(?<name>[^.]+)\\..+" );

    // Documented endpoint names mapped to the internal connector names.
    private static final Map<String, String> ENDPOINTS =
        Map.of( "web", DispatchConstants.XP_CONNECTOR, "management", DispatchConstants.API_CONNECTOR, "statistics",
                DispatchConstants.STATUS_CONNECTOR );

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

        return new VirtualHostMapping( name, host, source, target, idProvidersMapping, order, context, connector, allowedPrincipals );
    }

    // Principals allowed to pass through the vhost. No list means no restriction; an invalid
    // principal key fails the configuration.
    private PrincipalKeys getAllowedPrincipals( final String mappingPrefix )
    {
        final String value = getString( mappingPrefix + "principals" );
        if ( value == null )
        {
            return PrincipalKeys.empty();
        }

        return Stream.of( value.split( "," ) ).map( String::trim ).map( PrincipalKey::from ).collect( PrincipalKeys.collector() );
    }

    // A mapping without an endpoint applies to the web endpoint; a mapping for another endpoint
    // must name it explicitly. Endpoints are named as documented (web, management, statistics),
    // not by their internal connector names.
    private String getConnector( final String mappingPrefix )
    {
        final String value = getString( mappingPrefix + "endpoint" );
        if ( value == null )
        {
            return DispatchConstants.XP_CONNECTOR;
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

        final VirtualHostIdProvidersMapping.Builder hostIdProvidersMapping = VirtualHostIdProvidersMapping.create();

        getIdProviders( idProviderPrefix ).forEach( ( idProviderName, idProviderStatus ) -> {

            final IdProviderKey idProviderKey = IdProviderKey.from( idProviderName );

            // Query-string style value: "default" and/or "enabled[=flow,flow...]".
            // The flow list (if any) comes from the "enabled" parameter; no list means the default flows.
            boolean isDefault = false;
            boolean isEnabled = false;
            Set<IdProviderFlow> flows = null;

            for ( final String token : idProviderStatus.split( "&" ) )
            {
                final String trimmed = token.trim();
                final int eq = trimmed.indexOf( '=' );
                final String name = eq < 0 ? trimmed : trimmed.substring( 0, eq );
                final String value = eq < 0 ? null : trimmed.substring( eq + 1 );

                if ( DEFAULT_ID_PROVIDER_VALUE.equals( name ) )
                {
                    isDefault = true;
                }
                else if ( ENABLED_ID_PROVIDER_VALUE.equals( name ) )
                {
                    isEnabled = true;
                    if ( value != null && !value.isBlank() )
                    {
                        flows = parseFlows( value );
                    }
                }
            }

            if ( isDefault || isEnabled )
            {
                hostIdProvidersMapping.addIdProvider( idProviderKey, flows );
                if ( isDefault )
                {
                    hostIdProvidersMapping.setDefaultIdProvider( idProviderKey );
                }
            }

        } );

        return hostIdProvidersMapping.build();
    }

    private static Set<IdProviderFlow> parseFlows( final String value )
    {
        final EnumSet<IdProviderFlow> flows = EnumSet.noneOf( IdProviderFlow.class );
        for ( final String token : value.split( "," ) )
        {
            IdProviderFlow.from( token.trim() ).ifPresent( flows::add );
        }
        return flows;
    }

    private Map<String, String> getVirtualHostContext( final String mappingPrefix )
    {
        final String configPrefix = mappingPrefix + "context" + ".";

        return this.map.entrySet()
            .stream()
            .filter( entry -> entry.getKey().startsWith( configPrefix ) )
            .collect( Collectors.toMap( entry -> entry.getKey().replace( configPrefix, "" ), Map.Entry::getValue ) );
    }

    private Map<String, String> getIdProviders( final String idProviderPrefix )
    {
        return this.map.entrySet()
            .stream()
            .filter( entry -> entry.getKey().startsWith( idProviderPrefix ) )
            .collect( Collectors.toMap( entry -> entry.getKey().replace( idProviderPrefix, "" ), Map.Entry::getValue ) );
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

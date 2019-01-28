package com.enonic.xp.web.vhost.impl.config;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMappings;

final class VirtualHostConfigMap
{
    private final static String DEFAULT_ID_PROVIDER_VALUE = "default";

    private final static String ENABLED_ID_PROVIDER_VALUE = "enabled";

    private final static Pattern MAPPING_NAME_PATTERN = Pattern.compile( "mapping\\.([^\\.]+)\\..+" );

    private final Map<String, String> map;

    public VirtualHostConfigMap( final Map<String, String> map )
    {
        this.map = map;
    }

    public boolean isEnabled()
    {
        return getBoolean( "enabled", false );
    }

    public VirtualHostMappings buildMappings()
    {
        final VirtualHostMappings mappings = new VirtualHostMappings();
        for ( final String name : findMappingNames() )
        {
            mappings.add( buildMapping( name ) );
        }

        return mappings;
    }

    private VirtualHostMapping buildMapping( final String name )
    {
        final VirtualHostMapping mapping = new VirtualHostMapping( name );

        final String prefix = "mapping." + name + ".";
        mapping.setHost( getString( prefix + "host" ) );
        mapping.setSource( getString( prefix + "source" ) );
        mapping.setTarget( getString( prefix + "target" ) );

        mapping.setVirtualHostIdProvidersMapping( getHostIdProvidersMapping( prefix ) );

        return mapping;
    }

    private VirtualHostIdProvidersMapping getHostIdProvidersMapping( final String mappingPrefix )
    {
        final String idProviderPrefix = mappingPrefix + "idProvider" + ".";

        final VirtualHostIdProvidersMapping.Builder hostIdProvidersMapping = VirtualHostIdProvidersMapping.create();

        getIdProviders( idProviderPrefix ).
            forEach( ( idProviderName, idProviderStatus ) -> {

                final IdProviderKey idProviderKey = IdProviderKey.from( idProviderName );

                if ( DEFAULT_ID_PROVIDER_VALUE.equals( idProviderStatus ) )
                {
                    hostIdProvidersMapping.setDefaultIdProvider( idProviderKey );
                }
                if ( ENABLED_ID_PROVIDER_VALUE.equals( idProviderStatus ) )
                {
                    hostIdProvidersMapping.addIdProviderKey( idProviderKey );
                }

            } );

        return hostIdProvidersMapping.build();
    }

    private Map<String, String> getIdProviders( final String idProviderPrefix )
    {
        return this.map.entrySet().stream().
            filter( entry -> entry.getKey().startsWith( idProviderPrefix ) ).
            collect( Collectors.toMap( entry -> entry.getKey().replace( idProviderPrefix, "" ), Map.Entry::getValue ) );
    }

    private String getString( final String name )
    {
        final String value = this.map.get( name );
        if ( Strings.isNullOrEmpty( value ) )
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

    private Iterable<String> findMappingNames()
    {
        final Set<String> result = Sets.newHashSet();

        for ( final String key : this.map.keySet() )
        {
            final String name = findMappingName( key );
            if ( name != null )
            {
                result.add( name );
            }
        }

        return result;
    }

    private String findMappingName( final String key )
    {
        final Matcher matcher = MAPPING_NAME_PATTERN.matcher( key );
        if ( !matcher.matches() )
        {
            return null;
        }

        return matcher.group( 1 );
    }
}

package com.enonic.xp.web.vhost.impl.config;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMappings;

final class VirtualHostConfigMap
{
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

        return mapping;
    }

    private String getString( final String name )
    {
        final String value = this.map.get( name );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return null;
        }

        return value;
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

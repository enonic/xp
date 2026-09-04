package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.enonic.xp.web.dispatch.ResourceMapping;

abstract class ResourceDefinitionImpl<T>
    implements ResourceDefinition<T>
{
    private final ResourceMapping<T> mapping;

    final T resource;

    private final Pattern pattern;

    ResourceDefinitionImpl( final ResourceMapping<T> mapping )
    {
        this.mapping = mapping;
        this.resource = this.mapping.getResource();
        this.pattern = compilePattern( this.mapping.getUrlPatterns() );
    }

    @Override
    public final int getOrder()
    {
        return this.mapping.getOrder();
    }

    @Override
    public final String getName()
    {
        return this.mapping.getName();
    }

    @Override
    public final List<String> getConnectors()
    {
        return this.mapping.getConnectors();
    }

    @Override
    public final Set<String> getUrlPatterns()
    {
        return this.mapping.getUrlPatterns();
    }

    @SuppressWarnings("removal")
    @Override
    public final Map<String, String> getInitParams()
    {
        return this.mapping.getInitParams();
    }

    @Override
    public final T getResource()
    {
        return this.resource;
    }

    final boolean matches( final String uri )
    {
        return uri != null && this.pattern.matcher( uri ).matches();
    }

    private static Pattern compilePattern( final Set<String> urlPatterns )
    {
        return Pattern.compile(
            urlPatterns.stream().map( glob -> glob.replace( "*", ".*" ) ).collect( Collectors.joining( "|", "(", ")" ) ) );
    }
}

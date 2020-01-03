package com.enonic.xp.web.dispatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class MappingBuilder
{
    private List<String> connectors;

    private static final class FilterMappingImpl
        extends AbstractMapping<Filter>
        implements FilterMapping
    {
        private FilterMappingImpl( final MappingBuilder builder, final Filter filter )
        {
            super( builder, filter );
        }
    }

    private static final class ServletMappingImpl
        extends AbstractMapping<Servlet>
        implements ServletMapping
    {
        private ServletMappingImpl( final MappingBuilder builder, final Servlet servlet )
        {
            super( builder, servlet );
        }
    }

    private int order;

    private String name;

    private MappingBuilder()
    {
        this.order = 0;
        this.initParams = new HashMap<>();
        this.urlPatterns = new TreeSet<>();
        this.connectors = new ArrayList<>();
    }

    private final Map<String, String> initParams;

    private final Set<String> urlPatterns;

    public MappingBuilder connector( final String connector )
    {
        this.connectors.add( connector );
        return this;
    }

    public MappingBuilder order( final int order )
    {
        this.order = order;
        return this;
    }

    public MappingBuilder name( final String name )
    {
        this.name = name;
        return this;
    }

    public MappingBuilder connectors( final List<String> connectors )
    {
        this.connectors.addAll( connectors );
        return this;
    }

    private abstract static class AbstractMapping<T>
        implements ResourceMapping<T>
    {
        private int order;

        private String name;

        private final ImmutableMap<String, String> initParams;

        private final ImmutableSet<String> urlPatterns;

        private final T resource;

        private final List<String> connectors;

        private AbstractMapping( final MappingBuilder builder, final T resource )
        {
            this.resource = resource;
            this.order = builder.order;
            this.name = isNullOrEmpty( builder.name ) ? this.resource.getClass().getSimpleName() : builder.name;
            this.connectors = builder.connectors;
            this.initParams = ImmutableMap.copyOf( builder.initParams );
            this.urlPatterns = ImmutableSet.copyOf( builder.urlPatterns );
        }

        @Override
        public final String getName()
        {
            return this.name;
        }

        @Override
        public List<String> getConnectors()
        {
            return connectors;
        }

        @Override
        public final int getOrder()
        {
            return this.order;
        }

        @Override
        public final Map<String, String> getInitParams()
        {
            return this.initParams;
        }

        @Override
        public final Set<String> getUrlPatterns()
        {
            return this.urlPatterns;
        }

        @Override
        public final T getResource()
        {
            return this.resource;
        }
    }

    public MappingBuilder initParam( final String key, final String value )
    {
        this.initParams.put( key, value );
        return this;
    }

    public MappingBuilder urlPatterns( final String... patterns )
    {
        this.urlPatterns.addAll( List.of( patterns ) );
        return this;
    }

    public FilterMapping filter( final Filter filter )
    {
        Preconditions.checkNotNull( filter, "Filter should not be null" );
        return new FilterMappingImpl( this, filter );
    }

    public ServletMapping servlet( final Servlet servlet )
    {
        Preconditions.checkNotNull( servlet, "Servlet should not be null" );
        return new ServletMappingImpl( this, servlet );
    }

    public static MappingBuilder newBuilder()
    {
        return new MappingBuilder();
    }
}

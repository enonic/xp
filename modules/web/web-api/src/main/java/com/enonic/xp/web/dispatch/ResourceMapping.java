package com.enonic.xp.web.dispatch;

import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class ResourceMapping
{
    private final String name;

    private final int order;

    private final Map<String, String> initParams;

    private final Set<String> urlPatterns;

    private final Filter filter;

    private final Servlet servlet;

    private ResourceMapping( final Builder builder )
    {
        this.name = builder.name;
        this.order = builder.order;
        this.initParams = ImmutableMap.copyOf( builder.initParams );
        this.urlPatterns = ImmutableSet.copyOf( builder.urlPatterns );
        this.filter = builder.filter;
        this.servlet = builder.servlet;
    }

    public String getName()
    {
        return this.name;
    }

    public int getOrder()
    {
        return this.order;
    }

    public Map<String, String> getInitParams()
    {
        return this.initParams;
    }

    public Set<String> getUrlPatterns()
    {
        return this.urlPatterns;
    }

    public Filter getFilter()
    {
        return this.filter;
    }

    public Servlet getServlet()
    {
        return this.servlet;
    }

    public boolean isFilter()
    {
        return this.filter != null;
    }

    public boolean isServlet()
    {
        return this.servlet != null;
    }

    public final static class Builder
    {
        private int order;

        private String name;

        private final Map<String, String> initParams;

        private final Set<String> urlPatterns;

        private Filter filter;

        private Servlet servlet;

        private Builder()
        {
            this.order = 0;
            this.initParams = Maps.newHashMap();
            this.urlPatterns = Sets.newHashSet();
        }

        public Builder order( final int order )
        {
            this.order = order;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder initParam( final String key, final String value )
        {
            this.initParams.put( key, value );
            return this;
        }

        public Builder initParams( final Map<String, String> initParams )
        {
            this.initParams.putAll( initParams );
            return this;
        }

        public Builder urlPatterns( final String... patterns )
        {
            this.urlPatterns.addAll( Lists.newArrayList( patterns ) );
            return this;
        }

        public ResourceMapping build( final Filter filter )
        {
            this.filter = filter;
            this.name = ( this.name != null ) ? this.name : this.filter.getClass().getSimpleName();
            return new ResourceMapping( this );
        }

        public ResourceMapping build( final Servlet servlet )
        {
            this.servlet = servlet;
            this.name = ( this.name != null ) ? this.name : this.servlet.getClass().getSimpleName();
            return new ResourceMapping( this );
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }
}

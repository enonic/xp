package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import com.enonic.xp.web.dispatch.ResourceMapping;

abstract class ResourceDefinitionImpl<T>
    implements ResourceDefinition<T>
{
    private final static Logger LOG = LoggerFactory.getLogger( ResourceDefinitionImpl.class );

    private final ResourceMapping<T> mapping;

    final T resource;

    private Pattern pattern;

    private boolean initialized;

    ResourceDefinitionImpl( final ResourceMapping<T> mapping )
    {
        this.mapping = mapping;
        this.resource = this.mapping.getResource();
    }

    @Override
    public final void init( final ServletContext context )
    {
        if ( this.initialized )
        {
            return;
        }

        initPattern();

        try
        {
            doInit( new ResourceConfig( this.mapping.getName(), context, this.mapping.getInitParams() ) );
        }
        catch ( final ServletException e )
        {
            LOG.error( "Failed to initialize " + this.resource.getClass().toString(), e );
        }
        finally
        {
            this.initialized = true;
        }
    }

    @Override
    public final void destroy()
    {
        if ( !this.initialized )
        {
            return;
        }

        try
        {
            doDestroy();
        }
        finally
        {
            this.initialized = false;
        }
    }

    abstract void doInit( final ResourceConfig config )
        throws ServletException;

    abstract void doDestroy();

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
    public final Set<String> getUrlPatterns()
    {
        return this.mapping.getUrlPatterns();
    }

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

    private void initPattern()
    {
        final List<String> list = getUrlPatterns().stream().map( this::toRegExp ).collect( Collectors.toList() );
        this.pattern = Pattern.compile( "(" + Joiner.on( '|' ).join( list ) + ")" );
    }

    private String toRegExp( final String glob )
    {
        return glob.replace( "*", ".*" );
    }

    final boolean matches( final String uri )
    {
        return uri != null && this.pattern != null && this.pattern.matcher( uri ).matches();
    }
}

package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import com.enonic.xp.web.dispatch.ResourceMapping;

abstract class ResourceDefinitionImpl<T>
    implements ResourceDefinition<T>
{
    private static final Logger LOG = LoggerFactory.getLogger( ResourceDefinitionImpl.class );

    private final ResourceMapping<T> mapping;

    final T resource;

    /**
     * Set as the very last step of {@link #init(ServletContext)} and cleared as the very first step of
     * {@link #destroy()}. Request threads read it without synchronization, so it doubles as the safe
     * publication of the resource state {@code doInit} sets up: a thread that sees a pattern here is
     * guaranteed to see an initialized resource.
     */
    private volatile Pattern pattern;

    /**
     * Guarded by {@code this}, so that concurrent {@code init}/{@code destroy} calls - the pipeline can
     * initialize a definition both when it is added and when the servlet context arrives - initialize and
     * destroy the resource exactly once.
     */
    private boolean initialized;

    ResourceDefinitionImpl( final ResourceMapping<T> mapping )
    {
        this.mapping = mapping;
        this.resource = this.mapping.getResource();
    }

    @Override
    public final synchronized void init( final ServletContext context )
    {
        if ( this.initialized )
        {
            return;
        }

        final Pattern pattern = compilePattern();

        try
        {
            doInit( new ResourceConfig( this.mapping.getName(), context, this.mapping.getInitParams() ) );
        }
        catch ( final ServletException e )
        {
            LOG.error( "Failed to initialize {}", this.resource.getClass(), e );
        }
        finally
        {
            this.initialized = true;
            this.pattern = pattern;
        }
    }

    @Override
    public final synchronized void destroy()
    {
        if ( !this.initialized )
        {
            return;
        }

        this.pattern = null;

        try
        {
            doDestroy();
        }
        finally
        {
            this.initialized = false;
        }
    }

    abstract void doInit( ResourceConfig config )
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
    public final List<String> getConnectors()
    {
        return this.mapping.getConnectors();
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

    private Pattern compilePattern()
    {
        final List<String> list = getUrlPatterns().stream().map( this::toRegExp ).collect( Collectors.toList() );
        return Pattern.compile( "(" + String.join( "|", list ) + ")" );
    }

    private String toRegExp( final String glob )
    {
        return glob.replace( "*", ".*" );
    }

    final boolean matches( final String uri )
    {
        final Pattern pattern = this.pattern;
        return uri != null && pattern != null && pattern.matcher( uri ).matches();
    }
}

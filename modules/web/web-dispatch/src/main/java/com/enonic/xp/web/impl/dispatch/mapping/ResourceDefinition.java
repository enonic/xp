package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class ResourceDefinition<T>
{
    private final static String SERVLET_RANKING = Constants.SERVICE_RANKING;

    private final static Logger LOG = LoggerFactory.getLogger( ResourceDefinition.class );

    private int ranking;

    private String name;

    private final Map<String, String> initParams;

    final T resource;

    private final Set<String> urlPatterns;

    private Pattern pattern;

    private boolean initialized;

    ResourceDefinition( final T resource )
    {
        this.resource = resource;
        this.initParams = Maps.newHashMap();
        this.urlPatterns = Sets.newTreeSet();
    }

    public void configure( final Map<String, Object> serviceProps )
    {
        setServiceRanking( serviceProps );
    }

    private void setServiceRanking( final Map<String, Object> serviceProps )
    {
        final Object value = serviceProps.get( SERVLET_RANKING );
        this.ranking = ( value instanceof Integer ) ? (Integer) value : 0;
    }

    private String[] getStringArray( final Map<String, Object> serviceProps, final String key )
    {
        final Object value = serviceProps.get( key );
        if ( value instanceof String[] )
        {
            return (String[]) value;
        }
        else if ( value instanceof String )
        {
            return new String[]{(String) value};
        }
        else
        {
            return new String[0];
        }
    }

    final void setName( final Map<String, Object> serviceProps, final String key )
    {
        final String[] values = getStringArray( serviceProps, key );
        if ( values.length == 0 )
        {
            this.name = this.resource.getClass().getSimpleName();
        }
        else
        {
            this.name = values[0];
        }
    }

    final void setInitParams( final Map<String, Object> serviceProps, final String keyPrefix )
    {
        // TODO: Implement
    }

    final void setUrlPatterns( final Map<String, Object> serviceProps, final String key )
    {
        this.urlPatterns.addAll( Lists.newArrayList( getStringArray( serviceProps, key ) ) );
    }

    public final void init( final ServletContext context )
    {
        if ( this.initialized )
        {
            return;
        }

        initPattern();

        try
        {
            doInit( new ResourceConfig( this.name, context, this.initParams ) );
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

    public final int getRanking()
    {
        return this.ranking;
    }

    public final String getName()
    {
        return this.name;
    }

    public final Set<String> getUrlPatterns()
    {
        return this.urlPatterns;
    }

    public final Map<String, String> getInitParams()
    {
        return this.initParams;
    }

    public final T getResource()
    {
        return this.resource;
    }

    public final boolean isValid()
    {
        return !this.urlPatterns.isEmpty();
    }

    private void initPattern()
    {
        final List<String> list = this.urlPatterns.stream().map( this::toRegExp ).collect( Collectors.toList() );
        this.pattern = Pattern.compile( "(" + Joiner.on( '|' ).join( list ) + ")" );
    }

    private String toRegExp( final String glob )
    {
        return glob.replace( "*", ".*" );
    }

    public final boolean matches( final String uri )
    {
        return uri != null && this.pattern != null && this.pattern.matcher( uri ).matches();
    }
}

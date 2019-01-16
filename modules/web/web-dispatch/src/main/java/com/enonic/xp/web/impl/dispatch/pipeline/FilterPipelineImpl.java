package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Lists;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;

@Component(factory = "pipeline", service = FilterPipeline.class)
public class FilterPipelineImpl
    extends ResourcePipelineImpl<FilterDefinition>
    implements FilterPipeline
{

    @Activate
    protected void activate( Map<String, Object> properties )
    {
        super.activate( properties );
    }

    @Override
    public void filter( final HttpServletRequest req, final HttpServletResponse res, final ServletPipeline servletPipeline )
        throws ServletException, IOException
    {
        final FilterChainImpl chain = new FilterChainImpl( this.list, servletPipeline );
        chain.doFilter( req, res );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addFilter( final Filter filter, final ServiceReference<Filter> filterServiceReference )
    {
        final Object connectorProperty = filterServiceReference.getProperty( DispatchConstants.CONNECTOR_PROPERTY );

        final List<String> connectors = connectorProperty == null
            ? Lists.newArrayList()
            : connectorProperty instanceof String[] ? List.of( (String[]) connectorProperty ) : List.of( (String) connectorProperty );

        add( ResourceDefinitionFactory.create( filter, connectors ) );
    }

    public void removeFilter( final Filter filter )
    {
        remove( filter );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addMapping( final FilterMapping mapping )
    {
        //////////////////////////////////////////////////////
        add( ResourceDefinitionFactory.create( mapping ) );
    }

    public void removeMapping( final FilterMapping mapping )
    {
        remove( mapping );
    }
}

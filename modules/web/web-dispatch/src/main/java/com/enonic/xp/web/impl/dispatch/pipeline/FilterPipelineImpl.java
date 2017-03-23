package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;

@Component(service = FilterPipeline.class)
public final class FilterPipelineImpl
    extends ResourcePipelineImpl<FilterDefinition>
    implements FilterPipeline
{
    @Override
    public void filter( final HttpServletRequest req, final HttpServletResponse res, final ServletPipeline servletPipeline )
        throws ServletException, IOException
    {
        final FilterChainImpl chain = new FilterChainImpl( this.list, servletPipeline );
        chain.doFilter( req, res );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addFilter( final Filter filter )
    {
        add( ResourceDefinitionFactory.create( filter ) );
    }

    public void removeFilter( final Filter filter )
    {
        remove( filter );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addMapping( final FilterMapping mapping )
    {
        add( ResourceDefinitionFactory.create( mapping ) );
    }

    public void removeMapping( final FilterMapping mapping )
    {
        remove( mapping );
    }
}

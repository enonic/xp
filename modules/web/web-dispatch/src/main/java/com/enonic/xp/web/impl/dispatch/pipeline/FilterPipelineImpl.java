package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

@Component(service = FilterPipeline.class)
public final class FilterPipelineImpl
    extends ResourcePipelineImpl<Filter, FilterDefinition>
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
    public void addFilter( final Filter filter, final Map<String, Object> serviceProps )
    {
        add( filter, FilterDefinition.create( filter ), serviceProps );
    }

    public void removeFilter( final Filter filter )
    {
        remove( filter );
    }
}

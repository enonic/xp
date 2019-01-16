package com.enonic.xp.web.impl.dispatch;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component(factory = "dispatchServlet", service = DispatchServlet.class)
public final class DispatchServletImpl
    extends HttpServlet
    implements DispatchServlet
{
    private FilterPipeline filterPipeline;

    private ServletPipeline servletPipeline;

    private String connector;

    @Activate
    void activate( Map<String, Object> properties )
    {
        connector = (String) properties.get( DispatchConstants.CONNECTOR_PROPERTY );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        ServletRequestHolder.setRequest( req );

        try
        {
            this.filterPipeline.filter( req, res, this.servletPipeline );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addFilterPipeline( final FilterPipeline filterPipeline, final ServiceReference<FilterPipeline> pipelineServiceReference )
    {
        if ( sameConnector( pipelineServiceReference ) )
        {
            this.filterPipeline = filterPipeline;
        }
    }

    public void removeFilterPipeline( final FilterPipeline filterPipeline )
    {
        if ( this.filterPipeline != null && this.filterPipeline.equals( filterPipeline ) )
        {
            this.filterPipeline = null;
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addServletPipeline( final ServletPipeline servletPipeline, final ServiceReference<ServletPipeline> servletServiceReference )
    {
        if ( sameConnector( servletServiceReference ) )
        {
            this.servletPipeline = servletPipeline;
        }
    }

    public void removeServletPipeline( final ServletPipeline servletPipeline )
    {
        if ( this.servletPipeline != null && this.servletPipeline.equals( servletPipeline ) )
        {
            this.servletPipeline = null;
        }
    }

    @Override
    public void init()
        throws ServletException
    {
        this.filterPipeline.init( getServletContext() );
        this.servletPipeline.init( getServletContext() );
    }

    @Override
    public void destroy()
    {
        this.servletPipeline.destroy();
        this.filterPipeline.destroy();
    }

    @Override
    public String getConnector()
    {
        return connector;
    }

    private boolean sameConnector( final ServiceReference reference )
    {
        final Object value = reference.getProperty( DispatchConstants.CONNECTOR_PROPERTY );

        if ( value == null )
        {
            return true;
        }

        return value.equals( this.connector );
    }
}

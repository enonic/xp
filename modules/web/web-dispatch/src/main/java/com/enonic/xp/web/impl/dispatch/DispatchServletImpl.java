package com.enonic.xp.web.impl.dispatch;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    private volatile FilterPipeline filterPipeline;

    private volatile ServletPipeline servletPipeline;

    private final String connector;

    @Activate
    public DispatchServletImpl( final Map<String, ?> properties )
    {
        final String connectorValue = (String) properties.get( DispatchConstants.CONNECTOR_PROPERTY );
        this.connector = Objects.requireNonNull( connectorValue, "Connector property must not be null" );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        req.setAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE, connector );

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
    public void addFilterPipeline( final FilterPipeline filterPipeline, final Map<String, ?> properties )
    {
        if ( sameConnector( properties ) )
        {
            this.filterPipeline = filterPipeline;
        }
    }

    public void removeFilterPipeline( final FilterPipeline filterPipeline )
    {
        final FilterPipeline currentFilterPipeline = this.filterPipeline;
        if ( currentFilterPipeline == filterPipeline )
        {
            this.filterPipeline = null;
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addServletPipeline( final ServletPipeline servletPipeline, final Map<String, ?> properties )
    {
        if ( sameConnector( properties ) )
        {
            this.servletPipeline = servletPipeline;
        }
    }

    public void removeServletPipeline( final ServletPipeline servletPipeline )
    {
        final ServletPipeline currentServletPipeline = this.servletPipeline;
        if ( currentServletPipeline == servletPipeline )
        {
            this.servletPipeline = null;
        }
    }

    @Override
    public void init()
        throws ServletException
    {
        final ServletContext servletContext = getServletContext();
        this.filterPipeline.init( servletContext );
        this.servletPipeline.init( servletContext );
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

    private boolean sameConnector( final Map<String, ?> properties )
    {
        return this.connector.equals( properties.get( DispatchConstants.CONNECTOR_PROPERTY ) );
    }
}

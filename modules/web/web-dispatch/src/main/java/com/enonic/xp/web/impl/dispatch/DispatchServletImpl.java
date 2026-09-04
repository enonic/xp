package com.enonic.xp.web.impl.dispatch;

import java.io.IOException;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;

import static java.util.Objects.requireNonNull;

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
        this.connector = requireNonNull( connectorValue, "Connector property must not be null" );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final FilterPipeline filterPipeline = this.filterPipeline;
        final ServletPipeline servletPipeline = this.servletPipeline;

        if ( filterPipeline == null || servletPipeline == null )
        {
            // both pipelines are bound dynamically, a request can arrive before they are in place
            res.sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
            return;
        }

        req.setAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE, connector );

        filterPipeline.filter( req, res, servletPipeline );
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
        if ( this.filterPipeline == filterPipeline )
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
        if ( this.servletPipeline == servletPipeline )
        {
            this.servletPipeline = null;
        }
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

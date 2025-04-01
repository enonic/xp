package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.web.dispatch.ServletMapping;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;
import com.enonic.xp.web.impl.dispatch.mapping.ServletDefinition;

@Component(factory = "servlet", service = ServletPipeline.class)
public final class ServletPipelineImpl
    extends ResourcePipelineImpl<ServletDefinition>
    implements ServletPipeline
{
    @Activate
    public ServletPipelineImpl( final Map<String, ?> properties )
    {
        super( properties );
    }

    @Override
    public void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        for ( final ServletDefinition def : this.list.snapshot() )
        {
            if ( def.service( req, res ) )
            {
                return;
            }
        }
        res.sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addServlet( final Servlet servlet, final Map<String, ?> props )
    {
        add( ResourceDefinitionFactory.create( servlet, getConnectorsFromProperty( props ) ) );
    }

    public void removeServlet( final Servlet servlet )
    {
        remove( servlet );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addMapping( final ServletMapping mapping )
    {
        add( ResourceDefinitionFactory.create( mapping ) );
    }

    public void removeMapping( final ServletMapping mapping )
    {
        remove( mapping.getResource() );
    }
}

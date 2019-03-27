package com.enonic.xp.web.impl.dispatch.status;

import javax.servlet.Servlet;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.dispatch.ServletMapping;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;
import com.enonic.xp.web.impl.dispatch.mapping.ServletDefinition;

@Component(immediate = true, service = StatusReporter.class)
public final class ServletStatusReporter
    extends ResourceStatusReporter<ServletDefinition>
{
    public ServletStatusReporter()
    {
        super( "http.servlet" );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addServlet( final Servlet servlet, final ServiceReference<Servlet> servletServiceReference )
    {
        add( ResourceDefinitionFactory.create( servlet, getConnectorsFromProperty( servletServiceReference ) ) );
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

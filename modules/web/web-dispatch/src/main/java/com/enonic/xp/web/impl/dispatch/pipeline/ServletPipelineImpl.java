package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.web.dispatch.ServletMapping;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;
import com.enonic.xp.web.impl.dispatch.mapping.ServletDefinition;

@Component(service = ServletPipeline.class)
public final class ServletPipelineImpl
    extends ResourcePipelineImpl<ServletDefinition>
    implements ServletPipeline
{
    @Override
    public void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        for ( final ServletDefinition def : this.list )
        {
            if ( def.service( req, res ) )
            {
                return;
            }
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addServlet( final Servlet servlet )
    {
        add( ResourceDefinitionFactory.create( servlet ) );
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
        remove( mapping );
    }
}

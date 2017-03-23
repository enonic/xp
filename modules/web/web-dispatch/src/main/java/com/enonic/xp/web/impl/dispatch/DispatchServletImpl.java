package com.enonic.xp.web.impl.dispatch;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component(immediate = true, service = DispatchServlet.class)
public final class DispatchServletImpl
    extends HttpServlet
    implements DispatchServlet
{
    private FilterPipeline filterPipeline;

    private ServletPipeline servletPipeline;

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

    @Reference
    public void setFilterPipeline( final FilterPipeline filterPipeline )
    {
        this.filterPipeline = filterPipeline;
    }

    @Reference
    public void setServletPipeline( final ServletPipeline servletPipeline )
    {
        this.servletPipeline = servletPipeline;
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
}

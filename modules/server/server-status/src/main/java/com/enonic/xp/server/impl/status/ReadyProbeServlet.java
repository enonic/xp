package com.enonic.xp.server.impl.status;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import jakarta.servlet.Servlet;
import jakarta.servlet.annotation.WebServlet;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.server.impl.status.check.OSGIStateCheck;
import com.enonic.xp.server.impl.status.check.OSGIStateChecks;

@Component(immediate = true, service = Servlet.class, property = {"connector=status"})
@Order(-200)
@WebServlet({"/ready"})
public final class ReadyProbeServlet
    extends ProbeServlet
{
    @Activate
    public ReadyProbeServlet( final BundleContext bundleContext )
    {
        super( new OSGIStateCheck( bundleContext, OSGIStateChecks.READY_SERVICE_NAMES ) );
    }

    @Deactivate
    public void deactivate()
    {
        super.deactivate();
    }
}

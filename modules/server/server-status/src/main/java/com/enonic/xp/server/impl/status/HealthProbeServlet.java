package com.enonic.xp.server.impl.status;

import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.server.impl.status.check.HealthOSGIStateCheck;

@Component(immediate = true, service = Servlet.class, property = {"connector=status"})
@Order(-200)
@WebServlet({"/ready"})
public final class HealthProbeServlet
    extends ProbeServlet
{
    @Activate
    public HealthProbeServlet( final BundleContext bundleContext )
    {
        super( new HealthOSGIStateCheck( bundleContext ) );
    }

    @Deactivate
    public void deactivate()
    {
        super.deactivate();
    }

    @Override
    String getSuccessMessage()
    {
        return "XP is healthy!";
    }

    @Override
    String getFailedMessage( final List<String> errorMessages )
    {
        return String.format( "XP is not healthy: [%s]", String.join( ", ", errorMessages ) );
    }
}

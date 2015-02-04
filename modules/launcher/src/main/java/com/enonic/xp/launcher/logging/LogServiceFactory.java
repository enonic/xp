package com.enonic.xp.launcher.logging;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

final class LogServiceFactory
    implements ServiceFactory<LogService>
{
    @Override
    public LogService getService( final Bundle bundle, final ServiceRegistration<LogService> reg )
    {
        return new LogServiceImpl( bundle );
    }

    @Override
    public void ungetService( final Bundle bundle, final ServiceRegistration<LogService> reg, final LogService service )
    {
        // Do nothing
    }
}

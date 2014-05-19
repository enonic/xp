package com.enonic.wem.admin;

import com.enonic.wem.core.config.ConfigProperties;
import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        install( new AdminModule() );

        // Import services
        service( ConfigProperties.class ).importSingle();

        // Export services
        service( ResourceServlet.class ).attribute( "alias", "/" ).export();
    }
}

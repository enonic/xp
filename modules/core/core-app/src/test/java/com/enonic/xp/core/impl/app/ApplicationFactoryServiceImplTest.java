package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.ApplicationKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApplicationFactoryServiceImplTest
    extends BundleBasedTest
{
    @Test
    void lifecycle()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext );
        service.activate();

        final String appName = "app1";

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        assertNull( service.getApplication( bundle ) );

        bundle.start();
        assertNotNull( service.getApplication( bundle ) );

        bundle.stop();
        assertNotNull( service.getApplication( bundle ) );

        service.deactivate();

        assertNull( service.getApplication( bundle ) );
    }

    @Test
    void findActiveApplication()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        assertThat( service.findActiveApplication( applicationKey ) ).isEmpty();

        bundle.start();
        assertThat( service.findActiveApplication( applicationKey ) ).isNotEmpty();

        bundle.stop();
        assertThat( service.findActiveApplication( applicationKey ) ).isEmpty();
    }
}

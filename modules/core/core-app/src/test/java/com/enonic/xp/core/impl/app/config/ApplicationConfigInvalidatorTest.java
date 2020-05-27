package com.enonic.xp.core.impl.app.config;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;

import com.enonic.xp.core.impl.app.ApplicationRegistry;
import com.enonic.xp.core.impl.app.BundleBasedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

class ApplicationConfigInvalidatorTest
    extends BundleBasedTest

{
    @Test
    void lifecycle()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();

        final ApplicationConfigInvalidator service =
            new ApplicationConfigInvalidator( bundleContext, mock( ApplicationRegistry.class, withSettings().stubOnly() ) );

        service.activate();

        final String appName = "app1";

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        assertIterableEquals( Collections.emptyList(), getManagedServiceReferences( bundleContext, appName ),
                              "Must be no ManagedService before bundle starts" );

        bundle.start();

        assertEquals( 1, getManagedServiceReferences( bundleContext, appName ).size(),
                      "Must be one ManagedService after bundle is started" );

        bundle.stop();

        assertIterableEquals( Collections.emptyList(), getManagedServiceReferences( bundleContext, appName ),
                              "Must be no ManagedService after bundle is stopped" );

        bundle.start();

        assertEquals( 1, getManagedServiceReferences( bundleContext, appName ).size(),
                      "Must be one ManagedService after bundle is started again" );

        service.deactivate();

        assertIterableEquals( Collections.emptyList(), getManagedServiceReferences( bundleContext, appName ),
                              "Must be no ManagedService after ApplicationConfigInvalidator is deactivated " );
    }

    @Test
    void lifecycle_non_application_skipped()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();

        final ApplicationConfigInvalidator service =
            new ApplicationConfigInvalidator( bundleContext, mock( ApplicationRegistry.class, withSettings().stubOnly() ) );

        service.activate();

        final String appName = "app1";

        final String nonAppName = "nonApp";

        final Bundle bundle1 = deploy( appName, newBundle( appName, true ) );
        final Bundle bundle2 = deploy( nonAppName, newBundle( nonAppName, false ) );

        bundle1.start();
        bundle2.start();

        assertEquals( 1, getManagedServiceReferences( bundleContext, appName ).size(), "Must be one ManagedService for app bundle" );

        assertIterableEquals( Collections.emptyList(), getManagedServiceReferences( bundleContext, nonAppName ),
                              "Must be no ManagedService fon nonApp bundle" );

        service.deactivate();
    }

    private static Collection<ServiceReference<ManagedService>> getManagedServiceReferences( final BundleContext bundleContext,
                                                                                             final String appName )
        throws InvalidSyntaxException
    {
        return bundleContext.getServiceReferences( ManagedService.class, "(" + Constants.SERVICE_PID + "=" + appName + ")" );
    }
}

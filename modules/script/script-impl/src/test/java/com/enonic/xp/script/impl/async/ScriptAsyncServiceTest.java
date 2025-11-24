package com.enonic.xp.script.impl.async;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.BundleBasedTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScriptAsyncServiceTest
    extends BundleBasedTest
{
    @Test
    void lifecycle()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ScriptAsyncService service = new ScriptAsyncService( bundleContext );

        final String appName = "app1";

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        service.activate();

        assertThrows( NoSuchElementException.class, () -> service.getAsyncExecutor( ApplicationKey.from( appName ) ),
                      "Not started bundle must not have executor" );

        bundle.start();

        assertNotNull( service.getAsyncExecutor( ApplicationKey.from( appName ) ), "Started bundle must have executor" );

        bundle.stop();

        assertThrows( NoSuchElementException.class, () -> service.getAsyncExecutor( ApplicationKey.from( appName ) ),
                      "Stopped bundle must not have executor" );

        bundle.start();

        service.deactivate();

        assertThrows( NoSuchElementException.class, () -> service.getAsyncExecutor( ApplicationKey.from( appName ) ),
                      "Stopped service must not have executors" );

    }
}

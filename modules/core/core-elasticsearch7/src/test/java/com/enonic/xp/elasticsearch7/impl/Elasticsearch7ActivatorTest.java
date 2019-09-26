package com.enonic.xp.elasticsearch7.impl;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import static org.junit.jupiter.api.Assertions.*;

class Elasticsearch7ActivatorTest
{

    @Test
    void startstop()
    throws Exception {
        BundleContext context = Mockito.mock( BundleContext.class );

        Elasticsearch7Activator activator = new Elasticsearch7Activator();

        activator.activate( context, Map.of() );
        activator.deactivate();
    }
}
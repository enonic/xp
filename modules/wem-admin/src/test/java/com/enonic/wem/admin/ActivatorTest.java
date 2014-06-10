package com.enonic.wem.admin;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

public class ActivatorTest
{
    @Test
    public void testCreateInjector()
    {
        Guice.createInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( BundleContext.class ).toInstance( Mockito.mock( BundleContext.class ) );
            }
        }, new Activator() );
    }
}

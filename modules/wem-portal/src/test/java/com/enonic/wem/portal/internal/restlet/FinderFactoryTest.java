package com.enonic.wem.portal.internal.restlet;

import org.junit.Before;
import org.junit.Test;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static org.junit.Assert.*;

public class FinderFactoryTest
{
    private FinderFactory factory;

    @Before
    public void setup()
    {
        final Injector injector = Guice.createInjector( new RestletModule() );
        this.factory = injector.getInstance( FinderFactory.class );
    }

    @Test
    public void testFinder()
    {
        final Finder finder = this.factory.finder( MyServerResource.class );
        assertNotNull( finder );
        assertTrue( finder instanceof ResourceKeyFinder );

        final ServerResource resource = finder.create( null, null );
        assertNotNull( resource );
        assertTrue( resource instanceof MyServerResource );
    }
}

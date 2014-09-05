package com.enonic.wem.mustache.internal;

import org.junit.Test;

import com.google.inject.Guice;

public class MustacheActivatorTest
{
    @Test
    public void testCreateInjector()
    {
        Guice.createInjector( new MustacheActivator() );
    }
}

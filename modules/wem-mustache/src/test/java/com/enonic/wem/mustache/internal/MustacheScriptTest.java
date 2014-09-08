package com.enonic.wem.mustache.internal;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.script.AbstractScriptTest;

public class MustacheScriptTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final MustacheScriptLibrary lib = new MustacheScriptLibrary();
        lib.processorFactory = new MustacheProcessorFactoryImpl();

        addLibrary( lib );
    }

    @Test
    public void renderTest()
    {
        runTestScript( "mustache-test.js" );
    }
}

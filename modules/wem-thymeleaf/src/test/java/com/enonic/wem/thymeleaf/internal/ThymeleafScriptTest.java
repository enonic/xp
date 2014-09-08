package com.enonic.wem.thymeleaf.internal;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.script.AbstractScriptTest;

public class ThymeleafScriptTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final ThymeleafScriptLibrary lib = new ThymeleafScriptLibrary( new ThymeleafProcessorFactoryImpl() );
        addLibrary( lib );
    }

    @Test
    public void renderTest()
    {
        runTestScript( "thymeleaf-test.js" );
    }
}

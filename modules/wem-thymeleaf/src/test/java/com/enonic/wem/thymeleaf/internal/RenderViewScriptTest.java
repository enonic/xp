package com.enonic.wem.thymeleaf.internal;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.script.AbstractScriptTest;

public class RenderViewScriptTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        addHandler( new RenderViewHandler( new ThymeleafProcessorFactoryImpl() ) );
    }

    @Test
    public void renderTest()
    {
        runTestScript( "thymeleaf-test.js" );
    }
}

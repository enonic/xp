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
        final RenderViewHandler handler = new RenderViewHandler();
        handler.setFactory( new ThymeleafProcessorFactoryImpl() );
        addHandler( handler );
    }

    @Test
    public void renderTest()
    {
        runTestScript( "thymeleaf-test.js" );
    }
}

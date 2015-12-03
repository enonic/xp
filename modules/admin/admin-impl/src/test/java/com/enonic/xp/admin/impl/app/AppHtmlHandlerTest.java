package com.enonic.xp.admin.impl.app;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AppHtmlHandlerTest
{
    private AppHtmlHandler appHtmlHandler;

    @Before
    public void setUp()
    {
        appHtmlHandler = new AppHtmlHandler();
    }

    @Test
    public void testRenderSystem()
    {
        final String html = appHtmlHandler.render( "launcher" );
        assertNotNull( html );
        assertFalse( html.contains( "{{" ) );
        assertTrue( html.contains( "launcher/js/_all.js" ) );
        assertFalse( html.contains( "function startApplication()" ) );
    }

    @Test
    public void testRenderApplication()
    {
        final String html = appHtmlHandler.render( "myapp:launcher" );
        assertNotNull( html );
        assertFalse( html.contains( "{{" ) );
        assertFalse( html.contains( "launcher/js/_all.js" ) );
        assertTrue( html.contains( "function startApplication()" ) );
    }
}

package com.enonic.xp.admin.impl.app;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppHtmlHandlerTest
{
    @Test
    public void testRender()
    {
        final AppHtmlHandler handler = new AppHtmlHandler();
        final String html = handler.render( "launcher" );

        assertNotNull( html );
        assertFalse( html.contains( "{{" ) );
    }
}

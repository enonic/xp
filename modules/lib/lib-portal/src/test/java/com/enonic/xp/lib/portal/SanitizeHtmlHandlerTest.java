package com.enonic.xp.lib.portal;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.enonic.xp.portal.owasp.HtmlSanitizer;
import com.enonic.xp.testing.ScriptTestSupport;

class SanitizeHtmlHandlerTest
    extends ScriptTestSupport
{
    private HtmlSanitizer htmlSanitizer;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.htmlSanitizer = Mockito.mock( HtmlSanitizer.class );
        addService( HtmlSanitizer.class, this.htmlSanitizer );
    }

    @Test
    void testSanitizeHtml()
    {
        final String cleanHtml = "<p><a href=\"http://example.com/\">Link</a></p>";
        Mockito.when( this.htmlSanitizer.sanitizeHtml( ArgumentMatchers.anyString() ) ).thenReturn( cleanHtml );
        runFunction( "/test/htmlSanitizer-test.js", "sanitizeHtml" );
    }

    @Test
    void testExample_SanitizeHtml()
    {
        final String cleanHtml = "<p><a href=\"http://example.com/\">Link</a></p>";
        Mockito.when( this.htmlSanitizer.sanitizeHtml( ArgumentMatchers.anyString() ) ).thenReturn( cleanHtml );

        runScript( "/lib/xp/examples/portal/sanitizeHtml.js" );
    }
}

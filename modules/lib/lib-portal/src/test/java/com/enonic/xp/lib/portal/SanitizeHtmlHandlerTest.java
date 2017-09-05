package com.enonic.xp.lib.portal;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.enonic.xp.portal.owasp.HtmlSanitizer;
import com.enonic.xp.testing.ScriptTestSupport;

public class SanitizeHtmlHandlerTest
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
    public void testSanitizeHtml()
        throws Exception
    {
        final String cleanHtml = "<p><a href=\"http://example.com/\">Link</a></p>";
        Mockito.when( this.htmlSanitizer.sanitizeHtml( Matchers.anyString() ) ).thenReturn( cleanHtml );
        runFunction( "/site/test/htmlSanitizer-test.js", "sanitizeHtml" );
    }

    @Test
    public void testExample_SanitizeHtml()
    {
        final String cleanHtml = "<p><a href=\"http://example.com/\">Link</a></p>";
        Mockito.when( this.htmlSanitizer.sanitizeHtml( Matchers.anyString() ) ).thenReturn( cleanHtml );

        runScript( "/site/lib/xp/examples/portal/sanitizeHtml.js" );
    }
}
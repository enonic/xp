package com.enonic.xp.lib.thymeleaf;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.junit.Assert.*;

public class ThymeleafLibTest
    extends ScriptTestSupport
{
    @Before
    public void setupViewFunctions()
    {
        final ViewFunctionService viewFunctions = Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer );
        addService( ViewFunctionService.class, viewFunctions );
    }

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        final ViewFunctionParams params = (ViewFunctionParams) invocation.getArguments()[0];
        return params.getName() + "(" + params.getArgs().toString() + ")";
    }

    @Test
    public void renderTest()
        throws Exception
    {
        final String expected = loadResource( "/site/view/test-result.html" ).readString();
        final String actual = runFunction( "/site/thymeleaf-test.js", "renderTest" ).getValue().toString();
        assertEquals( normalizeTest( expected ), normalizeTest( actual ) );
    }

    @Test
    public void functionsTest()
        throws Exception
    {
        final String expected = loadResource( "/site/view/functions-result.html" ).readString();
        final String actual = runFunction( "/site/thymeleaf-test.js", "functionsTest" ).getValue().toString();
        assertEquals( normalizeTest( expected ), normalizeTest( actual ) );
    }

    @Test
    public void inlineFragment()
        throws Exception
    {
        final String expected = loadResource( "/site/fragment/inline-fragment-result.html" ).readString();
        final String actual = runFunction( "/site/thymeleaf-test.js", "inlineFragmentTest" ).getValue().toString();
        assertEquals( normalizeTest( expected ), normalizeTest( actual ) );
    }

    @Test
    public void externalFragment()
        throws Exception
    {
        final String expected = loadResource( "/site/fragment/external-fragment-result.html" ).readString();
        final String actual = runFunction( "/site/thymeleaf-test.js", "externalFragmentTest" ).getValue().toString();
        assertEquals( normalizeTest( expected ), normalizeTest( actual ) );
    }

    @Test
    public void dateTest()
        throws Exception
    {
        final String expected = loadResource( "/site/view/date-result.html" ).readString();
        final String actual = runFunction( "/site/thymeleaf-test.js", "dateTest" ).getValue().toString();
        assertEquals( normalizeTest( expected ), normalizeTest( actual ) );
    }

    private String normalizeTest( final String text )
    {
        final Iterable<String> lines = Splitter.on( Pattern.compile( "(\r\n|\n|\r)" ) ).trimResults().split( text );
        return Joiner.on( "\n" ).join( lines );
    }
}

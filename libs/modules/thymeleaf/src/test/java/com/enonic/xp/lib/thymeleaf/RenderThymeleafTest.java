package com.enonic.xp.lib.thymeleaf;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.Resources;

import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.junit.Assert.*;

public class RenderThymeleafTest
    extends ScriptTestSupport
{
    @Before
    public void setUp()
    {
        setupRequest();
        addService( ViewFunctionService.class, Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer ) );
    }

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        final ViewFunctionParams params = (ViewFunctionParams) invocation.getArguments()[0];
        return params.getName() + "(" + params.getArgs().toString() + ")";
    }

    private Object execute( final String method )
        throws Exception
    {
        return runTestFunction( "test/thymeleaf-test.js", method ).getValue();
    }

    @Test
    public void renderTest()
        throws Exception
    {
        final String result = execute( "renderTest" ).toString();
        final String expected = Resources.toString( getClass().getResource( "/app/test/view/test-result.html" ), Charsets.UTF_8 );
        assertEquals( normalizeTest( expected ), normalizeTest( result ) );
    }

    @Test
    public void functionsTest()
        throws Exception
    {
        final String result = execute( "functionsTest" ).toString();
        final String expected = Resources.toString( getClass().getResource( "/app/test/view/functions-result.html" ), Charsets.UTF_8 );
        assertEquals( normalizeTest( expected ), normalizeTest( result ) );
    }

    @Test
    public void fragmentsTest()
        throws Exception
    {
        final String result = execute( "fragmentsTest" ).toString();
        final String expected = Resources.toString( getClass().getResource( "/app/test/fragment/fragment-result.html" ), Charsets.UTF_8 );
        assertEquals( normalizeTest( expected ), normalizeTest( result ) );
    }

    private String normalizeTest( final String text )
    {
        final Iterable<String> lines = Splitter.on( Pattern.compile( "(\r\n|\n|\r)" ) ).trimResults().split( text );
        return Joiner.on( "\n" ).join( lines );
    }
}

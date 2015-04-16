package com.enonic.xp.portal.impl.thymeleaf;

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

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.script.AbstractScriptTest;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.resource.ResourceProblemException;

import static org.junit.Assert.*;

public class RenderViewHandlerTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final PortalContext context = new PortalContext();
        context.setMode( RenderMode.LIVE );
        context.setBranch( Branch.from( "draft" ) );
        context.setModule( ModuleKey.from( "mymodule" ) );
        context.setBaseUri( "/portal" );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        context.setContent( content );
        PortalContextAccessor.set( context );

        final RenderViewHandler handler = new RenderViewHandler();
        handler.setViewFunctionService( Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer ) );

        addHandler( handler );
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
        final ScriptExports exports = runTestScript( "thymeleaf-test.js" );
        return exports.executeMethod( method ).getValue();
    }

    private void executeException( final String method, final String expectedMessage )
        throws Exception
    {
        try
        {
            execute( method );
            fail( "Expected to fail with exception" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( expectedMessage, e.getMessage() );
        }
        catch ( final Exception e )
        {
            fail( "Expected ResourceProblemException but got " + e.getClass().getName() );
        }
    }

    @Test
    public void renderNoView()
        throws Exception
    {
        executeException( "noViewTest", "Parameter [view] is required" );
    }

    @Test
    public void renderTest()
        throws Exception
    {
        final String result = execute( "renderTest" ).toString();
        final String expected = Resources.toString( getClass().getResource( "/modules/mymodule/view/test-result.html" ), Charsets.UTF_8 );
        assertEquals( normalizeTest( expected ), normalizeTest( result ) );
    }

    @Test
    public void functionsTest()
        throws Exception
    {
        final String result = execute( "functionsTest" ).toString();
        final String expected =
            Resources.toString( getClass().getResource( "/modules/mymodule/view/functions-result.html" ), Charsets.UTF_8 );
        assertEquals( normalizeTest( expected ), normalizeTest( result ) );
    }

    @Test
    public void fragmentsTest()
        throws Exception
    {
        final String result = execute( "fragmentsTest" ).toString();
        final String expected =
            Resources.toString( getClass().getResource( "/modules/mymodule/fragment/fragment-result.html" ), Charsets.UTF_8 );
        assertEquals( normalizeTest( expected ), normalizeTest( result ) );
    }

    private String normalizeTest( final String text )
    {
        final Iterable<String> lines = Splitter.on( Pattern.compile( "(\r\n|\n|\r)" ) ).trimResults().split( text );
        return Joiner.on( "\n" ).join( lines );
    }
}

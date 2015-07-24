package com.enonic.xp.lib.xslt;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.testing.script.ScriptTestSupport;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public class RenderXsltTest
    extends ScriptTestSupport
{
    @Before
    public void setUp()
        throws Exception
    {
        addService( ViewFunctionService.class, Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer ) );
        addService( ResourceService.class, this.resourceService );

        mockResource( "mymodule:/test/xslt-test.js" );
        mockResource( "mymodule:/site/lib/xp/xslt.js" );
        mockResource( "mymodule:/test/view/test.xsl" );
    }

    private Object execute( final String method )
        throws Exception
    {
        return runTestFunction( "/test/xslt-test.js", method ).getValue();
    }

    @Test
    public void renderTest()
        throws Exception
    {
        final String result = cleanupXml( execute( "render" ).toString() );
        final String expected =
            cleanupXml( Resources.toString( getClass().getResource( "/site/test/view/test-result.xml" ), Charsets.UTF_8 ) );
        assertEquals( expected, result );
    }

    private String cleanupXml( final String xml )
        throws Exception
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        final ViewFunctionParams params = (ViewFunctionParams) invocation.getArguments()[0];
        return params.getName() + "(" + params.getArgs().toString() + ")";
    }
}

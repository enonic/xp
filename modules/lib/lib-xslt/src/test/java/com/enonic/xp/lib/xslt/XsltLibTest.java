package com.enonic.xp.lib.xslt;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.testing.script.ScriptTestSupport;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public class XsltLibTest
    extends ScriptTestSupport
{
    @Before
    public void setupViewFunctions()
    {
        final ViewFunctionService viewFunctions = Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer );
        addService( ViewFunctionService.class, viewFunctions );
    }

    @Test
    public void testSimple()
    {
        final String expected = loadResource( "/site/view/simple-result.xml" ).readString();
        final String actual = runFunction( "/site/xslt-test.js", "simple" ).getValue().toString();

        assertEquals( cleanupXml( expected ), cleanupXml( actual ) );
    }

    @Test
    public void testUrlFunctions()
    {
        final String expected = loadResource( "/site/view/url-functions-result.xml" ).readString();
        final String actual = runFunction( "/site/xslt-test.js", "urlFunctions" ).getValue().toString();

        assertEquals( cleanupXml( expected ), cleanupXml( actual ) );
    }

    private String cleanupXml( final String xml )
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

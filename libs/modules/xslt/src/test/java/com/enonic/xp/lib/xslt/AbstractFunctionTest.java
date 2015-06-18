package com.enonic.xp.lib.xslt;

import java.net.URL;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.resource.ResourceUrlRegistry;
import com.enonic.xp.testing.resource.ResourceUrlTestHelper;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public abstract class AbstractFunctionTest
{
    private XsltProcessor processor;

    @Before
    public final void setup()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );

        final XsltService service = new XsltService();
        service.setViewFunctionService( Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer ) );

        this.processor = service.newProcessor();
    }

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        final ViewFunctionParams params = (ViewFunctionParams) invocation.getArguments()[0];
        return params.getName() + "(" + params.getArgs().toString() + ")";
    }

    protected final void processTemplate( final String baseName )
        throws Exception
    {
        final String name = "/test/url/" + baseName;

        this.processor.setView( ResourceKey.from( "mymodule:" + name + ".xsl" ) );
        this.processor.setModel( null );
        final String actual = cleanupXml( this.processor.process() );

        final URL actualUrl = getClass().getResource( "/app" + name + "-result.xml" );
        final String expected = cleanupXml( Resources.toString( actualUrl, Charsets.UTF_8 ) );

        assertEquals( expected, actual );
    }

    private String cleanupXml( final String xml )
        throws Exception
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }
}

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
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.ResourceUrlResolver;
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
        service.setViewFunctionService( () -> Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer ) );

        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL url = ResourceUrlResolver.resolve( resourceKey );
            return new Resource( resourceKey, url );
        } );

        this.processor = service.newProcessor();
        this.processor.setResourceService( resourceService );
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

        final URL actualUrl = getClass().getResource( "/site" + name + "-result.xml" );
        final String expected = cleanupXml( Resources.toString( actualUrl, Charsets.UTF_8 ) );

        assertEquals( expected, actual );
    }

    private String cleanupXml( final String xml )
        throws Exception
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }
}

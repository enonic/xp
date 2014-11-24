package com.enonic.wem.xslt.internal.function;

import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.xml.DomHelper;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.internal.XsltProcessorFactoryImpl;

import static org.junit.Assert.*;

public abstract class AbstractFunctionTest
{
    private XsltProcessor processor;

    @Before
    public final void setup()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );

        final XsltProcessorFactoryImpl factory = new XsltProcessorFactoryImpl();
        this.processor = factory.newProcessor();

        final PortalRequest portalRequest = Mockito.mock( PortalRequest.class );
        Mockito.when( portalRequest.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( portalRequest.getMode() ).thenReturn( RenderingMode.EDIT );
        Mockito.when( portalRequest.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );

        final PortalContext portalContext = Mockito.mock( PortalContext.class );
        Mockito.when( portalContext.getRequest() ).thenReturn( portalRequest );

        PortalContextAccessor.set( portalContext );
    }

    protected final void processTemplate( final String baseName )
        throws Exception
    {
        this.processor.view( ResourceKey.from( "mymodule:/view/function/" + baseName + ".xsl" ) );
        this.processor.inputSource( new StreamSource( new StringReader( "<dummy/>" ) ) );
        final String actual = cleanupXml( this.processor.process() );

        final URL actualUrl = getClass().getResource( "/view/function/" + baseName + "Result.xml" );
        final String expected = cleanupXml( Resources.toString( actualUrl, Charsets.UTF_8 ) );

        assertEquals( expected, actual );
    }

    private String cleanupXml( final String xml )
        throws Exception
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }
}

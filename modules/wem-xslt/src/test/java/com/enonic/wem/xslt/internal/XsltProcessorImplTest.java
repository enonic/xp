package com.enonic.wem.xslt.internal;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import junit.framework.Assert;

import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.XsltProcessorFactory;

public class XsltProcessorImplTest
{
    private XsltProcessorFactory processorFactory;

    @Before
    public void setup()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );

        this.processorFactory = new XsltProcessorFactoryImpl();

        final PortalRequest portalRequest = Mockito.mock( PortalRequest.class );
        Mockito.when( portalRequest.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( portalRequest.getMode() ).thenReturn( RenderingMode.EDIT );
        Mockito.when( portalRequest.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );

        final PortalContext portalContext = Mockito.mock( PortalContext.class );
        Mockito.when( portalContext.getRequest() ).thenReturn( portalRequest );

        PortalContextAccessor.set( portalContext );
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testResourceNotFound()
    {
        final XsltProcessor processor = this.processorFactory.newProcessor();
        processor.view( ResourceKey.from( "mymodule:/view/unknown.xsl" ) );
        processor.inputXml( "<input/>" );
        processor.process();
    }

    @Test
    public void testProcessResource()
    {
        final Map<String, Object> params = Maps.newHashMap();

        final XsltProcessor processor = this.processorFactory.newProcessor();
        processor.view( ResourceKey.from( "mymodule:/view/test.xsl" ) );
        processor.inputXml( "<input/>" );
        processor.parameters( params );

        final String result = processor.process();
        Assert.assertEquals( "<div>Hello</div>", result );
    }

    @Test(expected = ResourceProblemException.class)
    public void testViewError()
    {
        final XsltProcessor processor = this.processorFactory.newProcessor();
        processor.view( ResourceKey.from( "mymodule:/view/test-error.xsl" ) );
        processor.inputXml( "<input/>" );
        processor.process();
    }
}

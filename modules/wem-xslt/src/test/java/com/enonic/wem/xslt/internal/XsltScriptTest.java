package com.enonic.wem.xslt.internal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.script.AbstractScriptTest;

public class XsltScriptTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final PortalRequest portalRequest = Mockito.mock( PortalRequest.class );
        Mockito.when( portalRequest.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( portalRequest.getMode() ).thenReturn( RenderingMode.EDIT );
        Mockito.when( portalRequest.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );

        final PortalContext portalContext = Mockito.mock( PortalContext.class );
        Mockito.when( portalContext.getRequest() ).thenReturn( portalRequest );

        PortalContextAccessor.set( portalContext );

        final XsltScriptLibrary lib = new XsltScriptLibrary( new XsltProcessorFactoryImpl() );
        addLibrary( lib );
    }

    @Test
    public void renderTest()
    {
        runTestScript( "xslt-test.js" );
    }
}

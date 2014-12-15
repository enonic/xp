package com.enonic.wem.portal.url;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.RenderMode;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;

public abstract class AbstractUrlBuilderTest
{
    protected PortalUrlBuilders builders;

    @Before
    public void setup()
    {
        final PortalRequest request = Mockito.mock( PortalRequest.class );
        Mockito.when( request.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( request.getMode() ).thenReturn( RenderMode.LIVE );
        Mockito.when( request.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );

        final PortalContextImpl context = new PortalContextImpl();
        context.setRequest( request );
        context.setModule( ModuleKey.from( "mymodule" ) );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        context.setContent( content );

        this.builders = new PortalUrlBuilders( context );
    }
}


package com.enonic.xp.portal.url;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.RenderMode;

public abstract class AbstractUrlBuilderTest
{
    protected PortalUrlBuilders builders;

    @Before
    public void setup()
    {
        final PortalContext context = Mockito.mock( PortalContext.class );
        Mockito.when( context.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( context.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( context.getMode() ).thenReturn( RenderMode.LIVE );
        Mockito.when( context.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );
        Mockito.when( context.getModule() ).thenReturn( ModuleKey.from( "mymodule" ) );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        Mockito.when( context.getContent() ).thenReturn( content );

        this.builders = new PortalUrlBuilders( context );
    }
}


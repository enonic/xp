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
        final PortalContext context = new PortalContext();
        context.setWorkspace( Workspace.from( "stage" ) );
        context.setModule( ModuleKey.from( "mymodule" ) );
        context.setBaseUri( "/portal" );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        context.setContent( content );

        this.builders = new PortalUrlBuilders( context );
    }
}

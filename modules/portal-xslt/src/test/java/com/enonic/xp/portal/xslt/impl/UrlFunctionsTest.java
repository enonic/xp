package com.enonic.xp.portal.xslt.impl;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.RenderMode;

public class UrlFunctionsTest
    extends AbstractFunctionTest
{
    @Test
    public void testAll()
        throws Exception
    {
        final PortalContext context = Mockito.mock( PortalContext.class );
        Mockito.when( context.getMode() ).thenReturn( RenderMode.LIVE );
        Mockito.when( context.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );
        Mockito.when( context.getModule() ).thenReturn( ModuleKey.from( "mymodule" ) );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        Mockito.when( context.getContent() ).thenReturn( content );
        PortalContextAccessor.set( context );

        processTemplate( "all" );
    }
}

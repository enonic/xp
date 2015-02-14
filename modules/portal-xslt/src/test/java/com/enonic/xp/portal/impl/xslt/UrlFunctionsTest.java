package com.enonic.xp.portal.impl.xslt;

import org.junit.Test;

import com.enonic.xp.core.branch.Branch;
import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.module.ModuleKey;
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
        final PortalContext context = new PortalContext();
        context.setMode( RenderMode.LIVE );
        context.setBranch( Branch.from( "draft" ) );
        context.setModule( ModuleKey.from( "mymodule" ) );
        context.setBaseUri( "/portal" );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        context.setContent( content );
        PortalContextAccessor.set( context );

        processTemplate( "all" );
    }
}

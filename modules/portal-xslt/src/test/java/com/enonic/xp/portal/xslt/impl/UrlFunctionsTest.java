package com.enonic.xp.portal.xslt.impl;

import org.junit.Test;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.module.ModuleKey;
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

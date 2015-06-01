package com.enonic.xp.portal.impl.jslib.current;

import org.junit.Test;

import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.portal.impl.jslib.AbstractHandlerTest;
import com.enonic.xp.portal.impl.jslib.ContentFixtures;
import com.enonic.xp.portal.script.command.CommandHandler;

public class GetCurrentComponentHandlerTest
    extends AbstractHandlerTest
{

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        return new GetCurrentComponentHandler();
    }

    @Test
    public void getComponent()
        throws Exception
    {
        final Component component = ContentFixtures.newLayoutComponent();
        portalRequest.setComponent( component );

        execute( "getComponent" );
    }

    @Test
    public void getComponent_notFound()
        throws Exception
    {
        portalRequest.setComponent( null );
        execute( "getComponent_notFound" );
    }

}

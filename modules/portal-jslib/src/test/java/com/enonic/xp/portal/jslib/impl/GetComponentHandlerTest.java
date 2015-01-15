package com.enonic.xp.portal.jslib.impl;

import org.junit.Test;

import com.enonic.wem.api.content.page.region.Component;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.mapper.ContentFixtures;
import com.enonic.xp.portal.jslib.AbstractHandlerTest;

public class GetComponentHandlerTest
    extends AbstractHandlerTest
{

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        return new GetComponentHandler();
    }

    @Test
    public void getComponent()
        throws Exception
    {
        final Component component = ContentFixtures.newLayoutComponent();
        context.setComponent( component );

        execute( "getComponent" );
    }

    @Test
    public void getComponent_notFound()
        throws Exception
    {
        context.setComponent( null );
        execute( "getComponent_notFound" );
    }

}

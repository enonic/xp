package com.enonic.xp.portal.jslib.impl;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.mapper.ContentFixtures;

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
        final PageComponent component = ContentFixtures.newLayoutComponent();
        Mockito.when( context.getComponent() ).thenReturn( component );

        execute( "getComponent" );
    }

    @Test
    public void getComponent_notFound()
        throws Exception
    {
        Mockito.when( context.getComponent() ).thenReturn( null );

        execute( "getComponent_notFound" );
    }

}

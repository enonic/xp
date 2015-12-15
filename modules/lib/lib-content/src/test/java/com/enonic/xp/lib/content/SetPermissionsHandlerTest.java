package com.enonic.xp.lib.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.util.concurrent.Futures;

import com.enonic.xp.content.Content;

public class SetPermissionsHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        Mockito.when( this.contentService.applyPermissions( Mockito.any() ) ).thenReturn( Futures.immediateFuture( 1 ) );

        runScript( "/site/lib/xp/examples/content/setPermissions.js" );
    }
}

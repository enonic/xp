package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.UpdateContentParams;

class RemoveAttachmentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );
        runScript( "/lib/xp/examples/content/removeAttachment.js" );

        Mockito.verify( this.contentService, Mockito.times( 2 ) ).update( Mockito.any( UpdateContentParams.class ) );
    }

    @Test
    void removeAttachmentSingle()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runFunction( "/test/RemoveAttachmentHandlerTest.js", "removeAttachmentSingle" );
    }

    @Test
    void removeAttachmentMulti()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runFunction( "/test/RemoveAttachmentHandlerTest.js", "removeAttachmentMulti" );
    }

}

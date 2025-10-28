package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.UpdateContentParams;

public class AddAttachmentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );
        runScript( "/lib/xp/examples/content/addAttachment.js" );

        Mockito.verify( this.contentService, Mockito.times( 1 ) ).update( Mockito.any( UpdateContentParams.class ) );
    }

    @Test
    void addAttachmentById()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runFunction( "/test/AddAttachmentHandlerTest.js", "addAttachmentById" );
    }

    @Test
    void addAttachmentWithString()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runFunction( "/test/AddAttachmentHandlerTest.js", "addAttachmentWithString" );
    }

    @Test
    void addAttachmentWithObject()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runFunction( "/test/AddAttachmentHandlerTest.js", "addAttachmentWithObject" );
    }

    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}

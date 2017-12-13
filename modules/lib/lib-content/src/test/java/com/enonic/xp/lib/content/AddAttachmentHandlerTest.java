package com.enonic.xp.lib.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.UpdateContentParams;

public class AddAttachmentHandlerTest
    extends BaseContentHandlerTest

{

    @Test
    public void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );
        runScript( "/site/lib/xp/examples/content/addAttachment.js" );

        Mockito.verify( this.contentService, Mockito.times( 1 ) ).update( Mockito.any( UpdateContentParams.class ) );
    }

    @Test
    public void addAttachmentById()
        throws Exception
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runFunction( "/site/test/AddAttachmentHandlerTest.js", "addAttachmentById" );
    }

    @Test
    public void addAttachmentWithString()
        throws Exception
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runFunction( "/site/test/AddAttachmentHandlerTest.js", "addAttachmentWithString" );
    }

    @Test
    public void addAttachmentWithObject()
        throws Exception
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runFunction( "/site/test/AddAttachmentHandlerTest.js", "addAttachmentWithObject" );
    }

    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}
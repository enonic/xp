package com.enonic.xp.lib.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class DeleteContentHandlerTest
    extends ScriptTestSupport
{
    private ContentService contentService;

    @Before
    public void setup()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        addService( ContentService.class, this.contentService );

        mockResource( "mymodule:/test/DeleteContentHandlerTest.js" );
        mockResource( "mymodule:/site/lib/xp/content.js" );
    }

    @Test
    public void deleteById()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( Contents.from( content ) );

        runTestFunction( "/test/DeleteContentHandlerTest.js", "deleteById" );
    }

    @Test
    public void deleteByPath()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( Contents.from( content ) );

        runTestFunction( "/test/DeleteContentHandlerTest.js", "deleteByPath" );
    }

    @Test
    public void deleteById_notFound()
        throws Exception
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( Mockito.any() ) ).thenThrow( new ContentNotFoundException( id, null ) );

        runTestFunction( "/test/DeleteContentHandlerTest.js", "deleteById_notFound" );
    }

    @Test
    public void deleteByPath_notFound()
        throws Exception
    {
        final ContentPath path = ContentPath.from( "/a/b" );
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenThrow( new ContentNotFoundException( path, null ) );

        runTestFunction( "/test/DeleteContentHandlerTest.js", "deleteByPath_notFound" );
    }
}

package com.enonic.xp.lib.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetContentHandlerTest
    extends ScriptTestSupport
{
    private ContentService contentService;

    @Before
    public void setup()
    {
        this.contentService = Mockito.mock( ContentService.class );
        addService( ContentService.class, this.contentService );
    }

    @Test
    public void getById()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        runTestFunction( "/test/GetContentHandlerTest.js", "getById" );
    }

    @Test
    public void getByPath()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        runTestFunction( "/test/GetContentHandlerTest.js", "getByPath" );
    }

    @Test
    public void getById_notFound()
        throws Exception
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( id ) ).thenThrow( new ContentNotFoundException( id, null ) );

        runTestFunction( "/test/GetContentHandlerTest.js", "getById_notFound" );
    }

    @Test
    public void getByPath_notFound()
        throws Exception
    {
        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        Mockito.when( this.contentService.getByPath( path ) ).thenThrow( new ContentNotFoundException( path, null ) );

        runTestFunction( "/test/GetContentHandlerTest.js", "getByPath_notFound" );
    }
}

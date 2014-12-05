package com.enonic.wem.jsapi.internal.content;

import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.jsapi.internal.AbstractHandlerTest;
import com.enonic.wem.script.command.CommandHandler;

import static org.junit.Assert.*;

public class GetContentHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final GetContentHandler handler = new GetContentHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void testGetById_notFound()
        throws Exception
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( id ) ).thenThrow( new ContentNotFoundException( id, null ) );

        final Map<String, Object> params = Maps.newHashMap();
        params.put( "key", id.toString() );

        final Object result = execute( params );
        assertNull( result );
    }

    @Test
    public void testGetByPath_notFound()
        throws Exception
    {
        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        Mockito.when( this.contentService.getByPath( path ) ).thenThrow( new ContentNotFoundException( path, null ) );

        final Map<String, Object> params = Maps.newHashMap();
        params.put( "key", path.toString() );

        final Object result = execute( params );
        assertNull( result );
    }

    @Test
    public void testGetById()
        throws Exception
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final Map<String, Object> params = Maps.newHashMap();
        params.put( "key", content.getId().toString() );

        final Object result = execute( params );
        assertJson( null, result );
    }

    @Test
    public void testGetByPath()
        throws Exception
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        final Map<String, Object> params = Maps.newHashMap();
        params.put( "key", content.getPath().toString() );

        final Object result = execute( params );
        assertJson( null, result );
    }
}

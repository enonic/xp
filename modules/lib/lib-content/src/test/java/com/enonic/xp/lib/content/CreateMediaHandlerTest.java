package com.enonic.xp.lib.content;

import java.time.Instant;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class CreateMediaHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        Mockito.when( this.contentService.create( Mockito.any( CreateMediaParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateMediaParams) mock.getArguments()[0] ) );

        runScript( "/site/lib/xp/examples/content/createMedia.js" );
    }

    @Test
    public void createMedia()
        throws Exception
    {
        Mockito.when( this.contentService.create( Mockito.any( CreateMediaParams.class ) ) ).thenAnswer( mock -> createContent( (CreateMediaParams) mock.getArguments()[0] ) );

        runFunction( "/site/test/CreateMediaHandlerTest.js", "createMedia" );

        final ArgumentCaptor<CreateMediaParams> argumentCaptor = ArgumentCaptor.forClass( CreateMediaParams.class );
        Mockito.verify( this.contentService, Mockito.times( 1 ) ).create( argumentCaptor.capture() );

        assertEquals( 0.5, argumentCaptor.getValue().getFocalX(), 0 );
        assertEquals( 0.5, argumentCaptor.getValue().getFocalY(), 0 );
    }

    @Test
    public void createMediaWithFocalPoints()
        throws Exception
    {
        Mockito.when( this.contentService.create( Mockito.any( CreateMediaParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateMediaParams) mock.getArguments()[0] ) );

        runFunction( "/site/test/CreateMediaHandlerTest.js", "createMediaWithFocalPoints" );

        final ArgumentCaptor<CreateMediaParams> argumentCaptor = ArgumentCaptor.forClass( CreateMediaParams.class );
        Mockito.verify( this.contentService, Mockito.times( 1 ) ).create( argumentCaptor.capture() );

        assertEquals( 0.3, argumentCaptor.getValue().getFocalX(), 0 );
        assertEquals( 0.1, argumentCaptor.getValue().getFocalY(), 0 );
    }

    private Content createContent( final CreateMediaParams params )
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
        builder.name( params.getName() );
        builder.parentPath( params.getParent() );
        builder.valid( false );
        builder.creator( PrincipalKey.ofAnonymous() );
        builder.createdTime( Instant.parse( "1975-01-08T00:00:00Z" ) );
        return builder.build();
    }

    @Test
    public void createMediaAutoGenerateNameWithExistingName()
        throws Exception
    {
        final ContentAlreadyExistsException exception = new ContentAlreadyExistsException( ContentPath.from( "/a/b/my-content.jpg" ) );
        Mockito.when( this.contentService.create( Mockito.any( CreateMediaParams.class ) ) ).thenThrow( exception ).
            thenAnswer( mock -> createContent( (CreateMediaParams) mock.getArguments()[0] ) );

        Mockito.when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content.jpg" ) ) ) ).thenReturn( true );
        Mockito.when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content-1.jpg" ) ) ) ).thenReturn( true );
        Mockito.when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content-2.jpg" ) ) ) ).thenReturn( true );

        runFunction( "/site/test/CreateMediaHandlerTest.js", "createMediaAutoGenerateName" );
    }

    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}

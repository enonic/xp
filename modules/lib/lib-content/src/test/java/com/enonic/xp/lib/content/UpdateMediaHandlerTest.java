package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.security.PrincipalKey;

public class UpdateMediaHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        final Content content = Content.create()
            .id( ContentId.from( "123456" ) )
            .name( "myMedia" )
            .parentPath( ContentPath.from( "/a/b/" ) )
            .valid( false )
            .creator( PrincipalKey.ofAnonymous() )
            .createdTime( Instant.parse( "1975-01-08T00:00:00Z" ) )
            .data( new PropertyTree() )
            .build();

        Mockito.when( this.contentService.getByPath( Mockito.any( ContentPath.class ) ) ).thenReturn( content );

        Mockito.when( this.contentService.update( Mockito.any( UpdateMediaParams.class ) ) ).then( (Answer<Content>) invocation -> {
            final UpdateMediaParams params = invocation.getArgument( 0 );

            PropertyTree data = content.getData().copy();
            data.setString( "caption", params.getCaption() );
            data.setValues( "artist", params.getArtistList().stream().map( ValueFactory::newString ).collect( Collectors.toList() ) );
            data.setString( "copyright", params.getCopyright() );
            data.setString( "mimeType", params.getMimeType() );
            data.setValues( "tags", params.getTagList().stream().map( ValueFactory::newString ).collect( Collectors.toList() ) );

            return Content.create( content ).data( data ).workflowInfo( params.getWorkflowInfo() ).build();
        } );

        runScript( "/lib/xp/examples/content/updateMedia.js" );
    }

    @Test
    public void testUpdateMediaValidate()
    {
        runFunction( "/test/UpdateMediaHandlerTest.js", "updateMediaValidate" );
    }

    @Test
    public void testUpdateMediaContentNotFoundById()
    {
        Mockito.when( this.contentService.getById( Mockito.any( ContentId.class ) ) ).thenReturn( null );
        runFunction( "/test/UpdateMediaHandlerTest.js", "updateMediaContentNotFoundById" );
    }

    @Test
    public void testUpdateMediaContentNotFoundByPath()
    {
        Mockito.when( this.contentService.getByPath( Mockito.any( ContentPath.class ) ) ).thenReturn( null );
        runFunction( "/test/UpdateMediaHandlerTest.js", "updateMediaContentNotFoundByPath" );
    }

    @Test
    public void testUpdateMediaThrowContentNotFound()
    {
        Mockito.when( this.contentService.getByPath( Mockito.any( ContentPath.class ) ) )
            .thenThrow( ContentNotFoundException.class );
        runFunction( "/test/UpdateMediaHandlerTest.js", "updateMediaThrowContentNotFound" );
    }
}

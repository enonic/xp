package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.security.PrincipalKey;

public class ModifyMediaHandlerTest
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

        runScript( "/lib/xp/examples/content/modifyMedia.js" );
    }

    @Test
    public void testModifyMediaValidate()
    {
        runFunction( "/test/ModifyMediaHandlerTest.js", "modifyMediaValidate" );
    }

    @Test
    public void testModifyMediaContentNotFoundById()
    {
        Mockito.when( this.contentService.getById( Mockito.any( ContentId.class ) ) ).thenReturn( null );
        runFunction( "/test/ModifyMediaHandlerTest.js", "modifyMediaContentNotFoundById" );
    }

    @Test
    public void testModifyMediaContentNotFoundByPath()
    {
        Mockito.when( this.contentService.getByPath( Mockito.any( ContentPath.class ) ) ).thenReturn( null );
        runFunction( "/test/ModifyMediaHandlerTest.js", "modifyMediaContentNotFoundByPath" );
    }

    @Test
    public void testModifyMediaThrowContentNotFound()
    {
        Mockito.when( this.contentService.getByPath( Mockito.any( ContentPath.class ) ) )
            .thenThrow( new ContentNotFoundException( ContentPath.from( "path" ), Branch.from( "draft" ) ) );
        runFunction( "/test/ModifyMediaHandlerTest.js", "modifyMediaThrowContentNotFound" );
    }
}

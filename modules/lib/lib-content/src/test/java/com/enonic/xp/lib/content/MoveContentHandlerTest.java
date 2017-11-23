package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

import static org.mockito.Mockito.when;

@Ignore
public class MoveContentHandlerTest
    extends BaseContentHandlerTest
{

    @Test
    public void testExample()
    {
        // example 1
        final Content sourceContent = mockContent( "/my-site/my-content-name" );
        mockGetByPath( "/my-site/my-content-name", sourceContent );
        mockRename( sourceContent.getId(), "new-name", mockContent( "/my-site/new-name" ) );

        // example 2
        mockMove( sourceContent.getId(), "/my-site/folder/", mockContent( "/my-site/folder/my-content-name" ) );

        // example 3
        mockGetById( "8d933461-ede7-4dd5-80da-cb7de0cd7bba", sourceContent );

        final ContentPath cp = ContentPath.from( "/my-site/folder/existing-content" );
        when( contentService.contentExists( cp ) ).thenReturn( true );

        runScript( "/site/lib/xp/examples/content/move.js" );
    }

    @Test
    public void moveSameParentPath()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        final Content sourceContent = mockContent( "/my-site/my-content-name" );
        mockGetByPath( "/my-site/my-content-name", sourceContent );
        mockRename( sourceContent.getId(), "new-name", mockContent( "/my-site/new-name" ) );

        runFunction( "/site/test/MoveContentHandlerTest.js", "moveSameParentPath" );
    }

    private Content mockContent( final String contentPath )
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "8d933461-ede7-4dd5-80da-cb7de0cd7bba" ) );
        builder.name( StringUtils.substringAfterLast( contentPath, "/" ) );
        builder.displayName( contentPath );
        builder.parentPath( ContentPath.from( contentPath ).getParentPath() );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.language( Locale.ENGLISH );
        builder.data( new PropertyTree() );
        builder.valid( true );
        return builder.build();
    }

    private void mockGetByPath( final String contentPath, final Content contentResult )
    {
        when( this.contentService.getByPath( Mockito.eq( ContentPath.from( contentPath ) ) ) ).thenReturn( contentResult );
    }

    private void mockGetById( final String contentId, final Content contentResult )
    {
        when( this.contentService.getById( Mockito.eq( ContentId.from( contentId ) ) ) ).thenReturn( contentResult );
    }

    private void mockRename( final ContentId contentId, final String newName, final Content contentResult )
    {
        final RenameContentParams renameParams =
            RenameContentParams.create().contentId( contentId ).newName( ContentName.from( newName ) ).build();
        when( this.contentService.rename( Mockito.eq( renameParams ) ) ).thenReturn( contentResult );
    }

    private void mockMove( final ContentId contentId, final String parentPath, final Content contentResult )
    {
        final MoveContentParams moveParams = MoveContentParams.create().
            contentId( contentId ).
            parentContentPath( ContentPath.from( parentPath ) ).
            build();
        final MoveContentsResult result = MoveContentsResult.create().addMoved( contentResult.getId() ).build();
        when( this.contentService.move( Mockito.eq( moveParams ) ) ).thenReturn( result );
    }
}

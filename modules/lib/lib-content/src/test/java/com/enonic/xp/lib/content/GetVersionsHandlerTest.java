package com.enonic.xp.lib.content;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.security.PrincipalKey;

public class GetVersionsHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        final ContentVersion newContentVersion = ContentVersion.create().
            id( ContentVersionId.from( "newVersion" ) ).
            displayName( "My content" ).
            modified( Instant.ofEpochSecond( 1529280000 ) ).
            modifier( PrincipalKey.ofSuperUser() ).
            build();
        final ContentVersion oldContentVersion = ContentVersion.create().
            id( ContentVersionId.from( "olderVersion" ) ).
            displayName( "My content" ).
            modified( Instant.ofEpochSecond( 1529020800 ) ).
            modifier( PrincipalKey.ofSuperUser() ).
            build();
        final ContentVersions contentVersions = ContentVersions.create().
            add( newContentVersion ).
            add( oldContentVersion ).
            build();
        final FindContentVersionsResult result = FindContentVersionsResult.create().
            contentVersions( contentVersions ).
            from( 0 ).
            hits( 2L ).
            totalHits( 2L ).
            size( 10 ).
            build();
        Mockito.when( this.contentService.getVersions( Mockito.any() ) ).thenReturn( result );

        final Content content = Content.create().
            id( ContentId.from( "contentId" ) ).
            parentPath( ContentPath.from( "/path/to" ) ).
            name( "mycontent" ).
            build();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runScript( "/site/lib/xp/examples/content/getVersions.js" );
    }
}

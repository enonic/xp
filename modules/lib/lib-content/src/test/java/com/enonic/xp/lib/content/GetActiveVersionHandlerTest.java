package com.enonic.xp.lib.content;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.security.PrincipalKey;

public class GetActiveVersionHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        final ContentVersion draftContentVersion = ContentVersion.create().
            id( ContentVersionId.from( "1b5c7c8dc0db8a99287b288d965ac4002b22a560" ) ).
            displayName( "My content" ).
            modified( Instant.ofEpochSecond( 1529280000 ) ).
            modifier( PrincipalKey.ofSuperUser() ).
            build();

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create().
            add( ActiveContentVersionEntry.from( Branch.from( "draft" ), draftContentVersion ) ).
            build();
        Mockito.when( this.contentService.getActiveVersions( Mockito.any() ) ).thenReturn( result );        

        final Content content = Content.create().
            id( ContentId.from( "contentId" ) ).
            parentPath( ContentPath.from( "/path/to" ) ).
            name( "mycontent" ).
            build();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runScript( "/site/lib/xp/examples/content/getActiveVersion.js" );
    }
}

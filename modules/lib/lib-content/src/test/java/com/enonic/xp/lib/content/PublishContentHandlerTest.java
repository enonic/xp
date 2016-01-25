package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

public class PublishContentHandlerTest
    extends BaseContentHandlerTest
{
    private static String PUB_ID_1 = "d7ad428b-eae2-4ff1-9427-e8e8a8a3ab23";

    private static String PUB_ID_2 = "9f5b0db0-38f9-4e81-b92e-116f25476b1c";

    private static String PUB_ID_3 = "e1f57280-d672-4cd8-b674-98e26e5b69ae";

    private static String DEL_ID = "45d67001-7f2b-4093-99ae-639be9fdd1f6";

    private static String FAIL_ID = "79e21db0-5b43-45ce-b58c-6e1c420b22bd";

    @Test
    public void testExample()
    {
        final Content content = exampleContent( PUB_ID_1, "mycontent", "My Content", "/mysite/somepage", "myfield", "Hello World" );
        Mockito.when( this.contentService.getByPath( ContentPath.from( "/mysite/somepage" ) ) ).thenReturn( content );

        ContentIds ids = ContentIds.from( PUB_ID_1, FAIL_ID );
        PushContentParams pushParams =
            PushContentParams.create().contentIds( ids ).target( Branch.from( "master" ) ).includeChildren( true ).includeDependencies(
                false ).build();
        Mockito.when( this.contentService.push( pushParams ) ).thenReturn( exampleResult() );

        runScript( "/site/lib/xp/examples/content/publish.js" );
    }

    @Test
    public void publishById()
    {
        ContentIds ids = ContentIds.from( PUB_ID_2, DEL_ID, FAIL_ID );
        PushContentParams pushParams = PushContentParams.create().contentIds( ids ).target( Branch.from( "draft" ) ).build();
        Mockito.when( this.contentService.push( pushParams ) ).thenReturn( exampleResult() );

        runFunction( "/site/test/PublishContentHandlerTest.js", "publishById" );
    }

    @Test
    public void publishByPath()
    {
        final Content myContent = exampleContent( PUB_ID_2, "mycontent", "My Content", "/myfolder/mycontent", "myfield", "Hello World" );
        Mockito.when( this.contentService.getByPath( ContentPath.from( "/myfolder/mycontent" ) ) ).thenReturn( myContent );
        final Content yourContent =
            exampleContent( PUB_ID_3, "yourcontent", "Your Content", "/yourfolder/yourcontent", "yourfield", "Hello Universe!" );
        Mockito.when( this.contentService.getByPath( ContentPath.from( "/yourfolder/yourcontent" ) ) ).thenReturn( yourContent );

        ContentIds ids = ContentIds.from( PUB_ID_2, PUB_ID_3 );
        PushContentParams pushParams = PushContentParams.create().contentIds( ids ).target( Branch.from( "master" ) ).build();
        Mockito.when( this.contentService.push( pushParams ) ).thenReturn( exampleResult() );

        runFunction( "/site/test/PublishContentHandlerTest.js", "publishByPath" );
    }

    @Test
    public void publishWithoutChildrenOrDependencies()
    {
        Contents published =
            Contents.from( exampleContent( PUB_ID_3, "mycontent", "My Content", "/mysite/somepage", "myfield", "Hello World" ) );
        PushContentsResult exampleResult = PushContentsResult.create().setPushed( published ).build();
        ContentIds ids = ContentIds.from( PUB_ID_3 );
        PushContentParams pushParams =
            PushContentParams.create().contentIds( ids ).target( Branch.from( "master" ) ).includeChildren( false ).includeDependencies(
                false ).build();
        Mockito.when( this.contentService.push( pushParams ) ).thenReturn( exampleResult );

        runFunction( "/site/test/PublishContentHandlerTest.js", "publishWithoutChildrenOrDependencies" );
    }

    private static Content exampleContent( String id, String name, String displayName, String parentPath, String field, String value )
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( id ) );
        builder.name( name );
        builder.displayName( displayName );
        builder.parentPath( ContentPath.from( parentPath ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.language( Locale.ENGLISH );

        final PropertyTree tree = new PropertyTree();
        tree.setString( field, value );

        builder.data( tree );
        builder.valid( true );
        return builder.build();
    }

    private static PushContentsResult exampleResult()
    {
        Contents published =
            Contents.from( exampleContent( PUB_ID_1, "mycontent", "My Content", "/mysite/somepage", "myfield", "Hello World" ),
                           exampleContent( PUB_ID_2, "content2", "Content 2", "/mysite/page2", "myfield", "No. 2" ),
                           exampleContent( PUB_ID_3, "content3", "Content 3", "/mysite/page3", "myfield", "Hello x 3" ) );
        Contents deleted = Contents.from( exampleContent( DEL_ID, "nocontent", "No Content", "/mysite/leave", "myop", "Delete" ) );
        Contents failed = Contents.from( exampleContent( FAIL_ID, "badcontent", "Bad bad Content", "/mysite/fail", "myop", "Publish" ) );

        return PushContentsResult.create().setPushed( published ).setDeleted( deleted ).setFailed( failed ).build();
    }
}

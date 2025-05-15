package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PublishContentHandlerTest
    extends BaseContentHandlerTest
{
    private static final String PUB_ID_1 = "d7ad428b-eae2-4ff1-9427-e8e8a8a3ab23";

    private static final String PUB_ID_2 = "9f5b0db0-38f9-4e81-b92e-116f25476b1c";

    private static final String PUB_ID_3 = "e1f57280-d672-4cd8-b674-98e26e5b69ae";

    private static final String DEL_ID = "45d67001-7f2b-4093-99ae-639be9fdd1f6";

    private static final String FAIL_ID = "79e21db0-5b43-45ce-b58c-6e1c420b22bd";

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

    private static PublishContentResult exampleResult()
    {
        Contents published =
            Contents.from( exampleContent( PUB_ID_1, "mycontent", "My Content", "/mysite/somepage", "myfield", "Hello World" ),
                           exampleContent( PUB_ID_2, "content2", "Content 2", "/mysite/page2", "myfield", "No. 2" ),
                           exampleContent( PUB_ID_3, "content3", "Content 3", "/mysite/page3", "myfield", "Hello x 3" ) );
        Contents failed = Contents.from( exampleContent( FAIL_ID, "badcontent", "Bad bad Content", "/mysite/fail", "myop", "Publish" ) );

        return PublishContentResult.create().setPushed( published.getIds() ).
            setFailed( failed.getIds() ).
            build();
    }

    @Test
    public void testExample()
    {
        final Content content = exampleContent( PUB_ID_1, "mycontent", "My Content", "/mysite/somepage", "myfield", "Hello World" );
        when( this.contentService.getByPath( ContentPath.from( "/mysite/somepage" ) ) ).thenReturn( content );

        final ArgumentCaptor<PushContentParams> captor = ArgumentCaptor.forClass( PushContentParams.class );
        when( this.contentService.publish( captor.capture() ) ).thenReturn( exampleResult() );

        runScript( "/lib/xp/examples/content/publish.js" );

        assertThat( captor.getValue() ).extracting( "contentIds", "includeDependencies", "message" )
            .containsExactly( ContentIds.from( PUB_ID_1, FAIL_ID ), false, "My first publish" );
    }

    @Test
    public void publishById()
    {
        final ArgumentCaptor<PushContentParams> captor = ArgumentCaptor.forClass( PushContentParams.class );
        when( this.contentService.publish( captor.capture() ) ).thenReturn( exampleResult() );

        runFunction( "/test/PublishContentHandlerTest.js", "publishById" );

        assertThat( captor.getValue() ).returns( ContentIds.from( PUB_ID_2, DEL_ID, FAIL_ID ), PushContentParams::getContentIds );
    }

    @Test
    public void publishByPath()
    {
        final Content myContent = exampleContent( PUB_ID_2, "mycontent", "My Content", "/myfolder/mycontent", "myfield", "Hello World" );
        when( this.contentService.getByPath( ContentPath.from( "/myfolder/mycontent" ) ) ).thenReturn( myContent );
        final Content yourContent =
            exampleContent( PUB_ID_3, "yourcontent", "Your Content", "/yourfolder/yourcontent", "yourfield", "Hello Universe!" );
        when( this.contentService.getByPath( ContentPath.from( "/yourfolder/yourcontent" ) ) ).thenReturn( yourContent );

        final ArgumentCaptor<PushContentParams> captor = ArgumentCaptor.forClass( PushContentParams.class );
        when( this.contentService.publish( captor.capture() ) ).thenReturn( exampleResult() );

        runFunction( "/test/PublishContentHandlerTest.js", "publishByPath" );

        assertThat( captor.getValue() ).extracting( "contentIds" )
            .isEqualTo( ContentIds.from( PUB_ID_2, PUB_ID_3 ));
    }

    @Test
    public void publishWithoutChildrenOrDependencies()
    {
        Contents published =
            Contents.from( exampleContent( PUB_ID_3, "mycontent", "My Content", "/mysite/somepage", "myfield", "Hello World" ) );
        PublishContentResult exampleResult = PublishContentResult.create().
            setPushed( published.getIds() ).
            build();

        final ArgumentCaptor<PushContentParams> captor = ArgumentCaptor.forClass( PushContentParams.class );
        when( this.contentService.publish( captor.capture() ) ).thenReturn( exampleResult );

        runFunction( "/test/PublishContentHandlerTest.js", "publishWithoutChildrenOrDependencies" );

        assertThat( captor.getValue() ).extracting( "contentIds", "excludeChildrenIds", "includeDependencies" )
            .containsExactly( ContentIds.from( PUB_ID_3 ), ContentIds.from( PUB_ID_3 ), false );
    }

    @Test
    public void publishWithMessage()
    {
        final ArgumentCaptor<PushContentParams> captor = ArgumentCaptor.forClass( PushContentParams.class );

        when( this.contentService.publish( captor.capture() ) ).thenReturn( exampleResult() );

        runFunction( "/test/PublishContentHandlerTest.js", "publishWithMessage" );

        assertThat( captor.getValue() ).extracting( "contentIds", "message" )
            .containsExactly( ContentIds.from( PUB_ID_2, DEL_ID, FAIL_ID ), "My first publish" );
    }
}

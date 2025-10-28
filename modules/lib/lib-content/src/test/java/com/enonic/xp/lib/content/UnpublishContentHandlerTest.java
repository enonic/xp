package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UnpublishContentHandlerTest
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

    private static UnpublishContentsResult exampleResult()
    {
        ContentIds unpublished = ContentIds.from( PUB_ID_1, PUB_ID_2, PUB_ID_3 );
        return UnpublishContentsResult.create().addUnpublished( unpublished ).build();
    }

    @Test
    void testExample()
    {
        final Content content = exampleContent( PUB_ID_1, "mycontent", "My Content", "/mysite/somepage", "myfield", "Hello World" );
        when( this.contentService.getByPath( ContentPath.from( "/mysite/somepage" ) ) ).thenReturn( content );

        final ArgumentCaptor<UnpublishContentParams> captor = ArgumentCaptor.forClass( UnpublishContentParams.class );
        when( this.contentService.unpublish( captor.capture() ) ).thenReturn( exampleResult() );

        runScript( "/lib/xp/examples/content/unpublish.js" );

        assertThat( captor.getValue() ).extracting( "contentIds" )
            .isEqualTo( ContentIds.from( PUB_ID_1, FAIL_ID ) );
    }

    @Test
    void unpublishById()
    {
        final ArgumentCaptor<UnpublishContentParams> captor = ArgumentCaptor.forClass( UnpublishContentParams.class );
        when( this.contentService.unpublish( captor.capture() ) ).thenReturn( exampleResult() );

        runFunction( "/test/UnpublishContentHandlerTest.js", "unpublishById" );

        assertThat( captor.getValue() ).extracting( "contentIds" )
            .isEqualTo( ContentIds.from( PUB_ID_2, DEL_ID, FAIL_ID ) );
    }

    @Test
    void unpublishByPath()
    {
        final Content myContent = exampleContent( PUB_ID_2, "mycontent", "My Content", "/myfolder/mycontent", "myfield", "Hello World" );
        when( this.contentService.getByPath( ContentPath.from( "/myfolder/mycontent" ) ) ).thenReturn( myContent );
        final Content yourContent =
            exampleContent( PUB_ID_3, "yourcontent", "Your Content", "/yourfolder/yourcontent", "yourfield", "Hello Universe!" );
        when( this.contentService.getByPath( ContentPath.from( "/yourfolder/yourcontent" ) ) ).thenReturn( yourContent );

        final ArgumentCaptor<UnpublishContentParams> captor = ArgumentCaptor.forClass( UnpublishContentParams.class );
        when( this.contentService.unpublish( captor.capture() ) ).thenReturn( exampleResult() );

        runFunction( "/test/UnpublishContentHandlerTest.js", "unpublishByPath" );

        assertThat( captor.getValue() ).extracting( "contentIds" ).isEqualTo(  ContentIds.from( PUB_ID_2, PUB_ID_3 ) );
    }
}

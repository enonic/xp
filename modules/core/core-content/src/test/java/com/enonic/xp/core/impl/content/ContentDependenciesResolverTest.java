package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.Locale;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentDependencies;
import com.enonic.xp.content.ContentDependenciesAggregation;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentDependenciesResolverTest
{

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    private ContentDependenciesResolver resolver;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );

        resolver = new ContentDependenciesResolver( contentService );


    }

    private Content createContent( final String id, final PropertyTree data, final ContentTypeName contentTypeName )
    {
        return Content.create().
            id( ContentId.from( id ) ).
            data( data ).
            parentPath( ContentPath.ROOT ).
            name( id ).
            valid( true ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( contentTypeName ).
            build();
    }

    @Test
    public void resolve_inbound_dependencies()
    {
        final Content content = createContent( "folderRefContent1", new PropertyTree(), ContentTypeName.folder() );

        final FindContentIdsByQueryResult findContentByQueryResult =
            FindContentIdsByQueryResult.create().aggregations( Aggregations.from( BucketAggregation.bucketAggregation( "type" ).
                buckets( Buckets.create().
                    add( Bucket.create().key( "portal:site" ).docCount( 2 ).build() ).
                    add( Bucket.create().key( "base:folder" ).docCount( 1 ).build() ).
                    build() ).build() ) ).build();

        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( contentService.getOutboundDependencies( Mockito.any() ) ).thenReturn( ContentIds.from( "folderRefContent1" ) );
        Mockito.when( contentService.getByIds( Mockito.any() ) ).thenReturn( Contents.empty() );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findContentByQueryResult );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( "mycontenttype" ).icon( Icon.from( new byte[]{1}, "mime", Instant.now() ) ).setBuiltIn(
                true ).build() );

        final ContentDependencies result = resolver.resolve( content.getId() );

        assertEquals( 2, result.getInbound().size() );

        final ContentDependenciesAggregation siteAggregation = (ContentDependenciesAggregation) result.getInbound().toArray()[0];
        assertEquals( siteAggregation.getType(), ContentTypeName.site() );
        assertEquals( 2, siteAggregation.getCount() );

        final ContentDependenciesAggregation folderAggregation = (ContentDependenciesAggregation) result.getInbound().toArray()[1];
        assertEquals( folderAggregation.getType(), ContentTypeName.folder() );
        assertEquals( 1, folderAggregation.getCount() );

    }

    @Test
    public void resolve_outbound_dependencies()
    {
        final PropertyTree data = new PropertyTree();

        final Content folderRefContent1 = createContent( "folderRefContent1", data, ContentTypeName.folder() );

        final Content folderRefContent2 = createContent( "folderRefContent2", data, ContentTypeName.folder() );

        final Content siteRefContent1 = createContent( "siteRefContent1", data, ContentTypeName.site() );

        data.addReference( "myRef1", Reference.from( folderRefContent1.getId().toString() ) );
        data.addReference( "myRef2", Reference.from( folderRefContent2.getId().toString() ) );
        data.addReference( "myRef3", Reference.from( siteRefContent1.getId().toString() ) );
        data.addReference( "refToMyself", Reference.from( "contentId" ) );

        final Content content = createContent( "contentId", data, ContentTypeName.site() );

        final FindContentIdsByQueryResult findContentByQueryResult =
            FindContentIdsByQueryResult.create().aggregations( Aggregations.from( BucketAggregation.bucketAggregation( "type" ).
                buckets( Buckets.create().
                    build() ).build() ) ).build();

        Mockito.when( contentService.getOutboundDependencies( Mockito.any() ) ).thenReturn( ContentIds.from( "folderRefContent1", "folderRefContent2", "siteRefContent1" ) );
        Mockito.when( contentService.getByIds( Mockito.any() ) ).thenReturn(
            Contents.from( folderRefContent1, folderRefContent2, siteRefContent1 ) );
        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );

        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findContentByQueryResult );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( "mycontenttype" ).icon( Icon.from( new byte[]{1}, "mime", Instant.now() ) ).setBuiltIn(
                true ).build() );

        final ContentDependencies result = resolver.resolve( content.getId() );

        assertThat( result.getOutbound() ).extracting( ContentDependenciesAggregation::getType, ContentDependenciesAggregation::getCount )
            .containsExactlyInAnyOrder( Tuple.tuple( ContentTypeName.site(), 1L ), Tuple.tuple( ContentTypeName.folder(), 2L ) );
    }

    @Test
    public void resolve_outbound_with_missing_dependency()
    {
        final PropertyTree data = new PropertyTree();
        final Content folderRefContent1 = createContent( "folderRefContent1", data, ContentTypeName.folder() );
        final Content folderRefContent2 = createContent( "folderRefContent2", data, ContentTypeName.folder() );

        data.addReference( "myRef1", Reference.from( folderRefContent1.getId().toString() ) );
        data.addReference( "myRef2", Reference.from( folderRefContent2.getId().toString() ) );
        data.addReference( "myRef3", Reference.from( "some-id" ) );

        final Content content = createContent( "content", data, ContentTypeName.site() );

        final FindContentIdsByQueryResult findContentByQueryResult =
            FindContentIdsByQueryResult.create().aggregations( Aggregations.from( BucketAggregation.bucketAggregation( "type" ).
                buckets( Buckets.create().
                    build() ).build() ) ).build();

        Mockito.when( contentService.getOutboundDependencies( Mockito.any() ) ).thenReturn( ContentIds.from( "folderRefContent1", "folderRefContent2" ) );
        Mockito.when( contentService.getByIds( Mockito.any() ) ).thenReturn( Contents.from( folderRefContent1, folderRefContent2 ) );
        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findContentByQueryResult );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( "mycontenttype" ).icon( Icon.from( new byte[]{1}, "mime", Instant.now() ) ).setBuiltIn(
                true ).build() );

        final ContentDependencies result = resolver.resolve( content.getId() );

        assertEquals( 1, result.getOutbound().size() );

        final ContentDependenciesAggregation folderAggregation = (ContentDependenciesAggregation) result.getOutbound().toArray()[0];
        assertEquals( folderAggregation.getType(), ContentTypeName.folder() );
        assertEquals( 2, folderAggregation.getCount() );
    }
}

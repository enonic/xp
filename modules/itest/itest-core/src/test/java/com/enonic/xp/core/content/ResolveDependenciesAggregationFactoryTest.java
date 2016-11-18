package com.enonic.xp.core.content;

import java.time.Instant;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetDependenciesResult;
import com.enonic.xp.content.ResolveDependenciesAggregationResult;
import com.enonic.xp.core.impl.content.ResolveDependenciesAggregationFactory;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.Reference;


public class ResolveDependenciesAggregationFactoryTest
{

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    private ResolveDependenciesAggregationFactory factory;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );

        factory = new ResolveDependenciesAggregationFactory( contentService );


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
        throws Exception
    {
        final Content content = createContent( "folderRefContent1", new PropertyTree(), ContentTypeName.folder() );

        final FindContentIdsByQueryResult findContentByQueryResult =
            FindContentIdsByQueryResult.create().aggregations( Aggregations.from( BucketAggregation.bucketAggregation( "type" ).
                buckets( Buckets.create().
                    add( Bucket.create().key( "portal:site" ).docCount( 2 ).build() ).
                    add( Bucket.create().key( "base:folder" ).docCount( 1 ).build() ).
                    build() ).build() ) ).build();

        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( contentService.getByIds( Mockito.any() ) ).thenReturn( Contents.empty() );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findContentByQueryResult );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( "mycontenttype" ).icon( Icon.from( new byte[]{1}, "mime", Instant.now() ) ).setBuiltIn(
                true ).build() );

        final GetDependenciesResult result = factory.create( content.getId() );

        Assert.assertEquals( result.getInbound().size(), 2 );

        final ResolveDependenciesAggregationResult siteAggregation = (ResolveDependenciesAggregationResult) result.getInbound().toArray()[0];
        Assert.assertEquals( siteAggregation.getType(), ContentTypeName.site().toString() );
        Assert.assertEquals( siteAggregation.getCount(), 2 );

        final ResolveDependenciesAggregationResult folderAggregation = (ResolveDependenciesAggregationResult) result.getInbound().toArray()[1];
        Assert.assertEquals( folderAggregation.getType(), ContentTypeName.folder().toString() );
        Assert.assertEquals( folderAggregation.getCount(), 1 );

    }

    @Test
    public void resolve_outbound_dependencies()
        throws Exception
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

        Mockito.when( contentService.getByIds( Mockito.any() ) ).thenReturn(
            Contents.from( folderRefContent1, folderRefContent2, siteRefContent1 ) );
        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );

        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findContentByQueryResult );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( "mycontenttype" ).icon( Icon.from( new byte[]{1}, "mime", Instant.now() ) ).setBuiltIn(
                true ).build() );

        final GetDependenciesResult result = factory.create( content.getId() );

        Assert.assertEquals( result.getOutbound().size(), 2 );

        final ResolveDependenciesAggregationResult siteAggregation = (ResolveDependenciesAggregationResult) result.getOutbound().toArray()[0];
        Assert.assertEquals( siteAggregation.getType(), ContentTypeName.site().toString() );
        Assert.assertEquals( siteAggregation.getCount(), 1 );

        final ResolveDependenciesAggregationResult folderAggregation = (ResolveDependenciesAggregationResult) result.getOutbound().toArray()[1];
        Assert.assertEquals( folderAggregation.getType(), ContentTypeName.folder().toString() );
        Assert.assertEquals( folderAggregation.getCount(), 2 );
    }

    @Test
    public void resolve_outbound_with_missing_dependency()
        throws Exception
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

        Mockito.when( contentService.getByIds( Mockito.any() ) ).thenReturn( Contents.from( folderRefContent1, folderRefContent2 ) );
        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findContentByQueryResult );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( "mycontenttype" ).icon( Icon.from( new byte[]{1}, "mime", Instant.now() ) ).setBuiltIn(
                true ).build() );

        final GetDependenciesResult result = factory.create( content.getId() );

        Assert.assertEquals( result.getOutbound().size(), 1 );

        final ResolveDependenciesAggregationResult folderAggregation = (ResolveDependenciesAggregationResult) result.getOutbound().toArray()[0];
        Assert.assertEquals( folderAggregation.getType(), ContentTypeName.folder().toString() );
        Assert.assertEquals( folderAggregation.getCount(), 2 );
    }
}
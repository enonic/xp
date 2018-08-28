package com.enonic.xp.admin.impl.rest.resource.content;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.ContentSelectorQueryJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;

import static org.junit.Assert.*;

public class ContentSelectorQueryJsonToContentQueryConverterTest
{

    private ContentService contentService;

    private RelationshipTypeService relationshipTypeService;

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    @Before
    public void setUp()
    {
        contentService = Mockito.mock( ContentService.class );
        relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
    }

    @Test
    public void testSelectorQueryWithFewAllowPaths()
    {

        final Content content = createContent( "content-id", "my-content", ContentTypeName.folder() );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        final List<String> contentTypeNames = new ArrayList();
        contentTypeNames.add( "myApplication:comment" );

        final List<String> allowPaths = new ArrayList();
        allowPaths.add( "*" );
        allowPaths.add( "/path/to/parent" );

        ContentSelectorQueryJson contentQueryJson =
            new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName", contentTypeNames, allowPaths, null );
        ContentSelectorQueryJsonToContentQueryConverter processor = ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( contentQueryJson ).
            contentService( contentService ).
            relationshipTypeService( relationshipTypeService ).
            build();

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:comment" ), contentQuery.getContentTypes() );
        assertEquals( "(_path LIKE '/content/*' OR _path LIKE '/content/path/to/parent*')", contentQuery.getQueryExpr().toString() );
    }

    @Test
    public void testPathsWithSiteResolved()
    {
        final Content content = createContent( "content-id", "my-content", ContentTypeName.folder() );

        final Site site = createSite( "site-id", "my-site" );

        final List<String> allowPaths = new ArrayList();
        allowPaths.add( "${site}/path1" );
        allowPaths.add( "${site}/path2/path3" );
        allowPaths.add( "parent-path/child-path" );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        Mockito.when( contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( site );

        ContentSelectorQueryJson contentQueryJson =
            new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName", Collections.emptyList(), allowPaths, null );
        ContentSelectorQueryJsonToContentQueryConverter processor = ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( contentQueryJson ).
            contentService( contentService ).
            relationshipTypeService( relationshipTypeService ).
            build();

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals(
            "((_path LIKE '/content/my-site/path1*' OR _path LIKE '/content/my-site/path2/path3*') OR _path LIKE '/content/parent-path/child-path*')",
            contentQuery.getQueryExpr().toString() );
    }

    @Test
    public void testPathWithAllowTypePassedFromJson()
    {
        final Content content = createContent( "content-id", "my-content", ContentTypeName.shortcut() );

        final List<String> allowPaths = new ArrayList();
        allowPaths.add( "parent-path/child-path" );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        final List<String> allowTypesFromJson = Arrays.asList( "myApplication:type1", "myApplication:type2", "myApplication:type2" );

        ContentSelectorQueryJson contentQueryJson =
            new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName", allowTypesFromJson, allowPaths, null );
        ContentSelectorQueryJsonToContentQueryConverter processor = ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( contentQueryJson ).
            contentService( contentService ).
            relationshipTypeService( relationshipTypeService ).
            build();

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:type1", "myApplication:type2", "myApplication:type2" ),
                      contentQuery.getContentTypes() );
        assertEquals( "_path LIKE '/content/parent-path/child-path*'", contentQuery.getQueryExpr().toString() );
    }

    @Test(expected = RuntimeException.class)
    public void testNullSiteResolved()
    {
        final Content content = createContent( "content-id", "my-content", ContentTypeName.shortcut() );

        final List<String> allowPaths = new ArrayList();
        allowPaths.add( "${site}/*" );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        Mockito.when( contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( null );

        ContentSelectorQueryJson contentQueryJson =
            new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName", Collections.emptyList(), allowPaths, null );
        ContentSelectorQueryJsonToContentQueryConverter processor = ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( contentQueryJson ).
            contentService( contentService ).
            relationshipTypeService( relationshipTypeService ).
            build();

        processor.createQuery();
    }

    @Test
    public void testQueryWithSearchAndNoPaths()
    {
        final Content content = createContent( "content-id", "my-content", ContentTypeName.media() );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson(
            "(fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND')) " +
                "ORDER BY _modifiedTime DESC", 0, 100, "summary", "contentId", "inputName", Collections.emptyList(),
            Collections.emptyList(), null );
        ContentSelectorQueryJsonToContentQueryConverter processor = getProcessor( contentQueryJson );

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals(
            "(fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND')) ORDER BY _modifiedtime DESC",
            contentQuery.getQueryExpr().toString() );
    }

    private ContentSelectorQueryJsonToContentQueryConverter getProcessor( final ContentSelectorQueryJson json )
    {
        return ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( json ).
            contentService( contentService ).
            relationshipTypeService( relationshipTypeService ).
            build();
    }

    @Test
    public void testQueryWithSearch()
    {
        final Content content = createContent( "content-id", "my-content", ContentTypeName.shortcut() );

        final List<String> allowPaths = new ArrayList();
        allowPaths.add( "/*" );

        final List<String> contentTypeNames = new ArrayList();
        contentTypeNames.add( "myApplication:comment" );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson(
            "(fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND')) " +
                "ORDER BY _modifiedTime DESC", 0, 100, "summary", "contentId", "inputName", contentTypeNames, allowPaths, null );
        ContentSelectorQueryJsonToContentQueryConverter processor = getProcessor( contentQueryJson );

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:comment" ), contentQuery.getContentTypes() );
        assertEquals( "(_path LIKE '/content/*' AND (fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') " +
                          "OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND'))) ORDER BY _modifiedtime DESC",
                      contentQuery.getQueryExpr().toString() );
    }

    @Test
    public void testRelationshipTypesFetched()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "/*" ).build() ).
            build();

        final RelationshipType relationshipType = RelationshipType.create().
            name( "myrelationshiptype" ).
            addAllowedToType( ContentTypeName.media() ).
            addAllowedToType( ContentTypeName.dataMedia() ).
            build();

        Mockito.when( this.relationshipTypeService.getByName( Mockito.any() ) ).thenReturn( relationshipType );

        final Content content = createContent( "content-id", "my-content", ContentTypeName.shortcut() );

        final List<String> allowPaths = new ArrayList<>();
        allowPaths.add( "/*" );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson(
            "(fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND')) " +
                "ORDER BY _modifiedTime DESC", 0, 100, "summary", "contentId", "inputName", Collections.emptyList(), allowPaths,
            "relationship-type" );
        ContentSelectorQueryJsonToContentQueryConverter processor = getProcessor( contentQueryJson );

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( relationshipType.getAllowedToTypes(), contentQuery.getContentTypes() );
        assertEquals( "(_path LIKE '/content/*' AND (fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') " +
                          "OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND'))) ORDER BY _modifiedtime DESC",
                      contentQuery.getQueryExpr().toString() );
    }

    private Content createContent( final String id, final String name, final ContentTypeName contentTypeName )
    {
        final PropertyTree metadata = new PropertyTree();

        final Content parent1 = Content.create().
            id( ContentId.from( id ) ).
            parentPath( ContentPath.ROOT ).
            name( "parent-content-1" ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( contentTypeName ).
            addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) ).
            build();

        final Content parent2 = Content.create().
            id( ContentId.from( id ) ).
            parentPath( parent1.getPath() ).
            name( "parent-content-2" ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( contentTypeName ).
            addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) ).
            build();

        return Content.create().
            id( ContentId.from( id ) ).
            parentPath( parent2.getPath() ).
            name( name ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( contentTypeName ).
            addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) ).
            build();
    }

    private Site createSite( final String id, final String name )
    {
        return Site.create().
            id( ContentId.from( id ) ).
            parentPath( ContentPath.ROOT ).
            name( name ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.site() ).
            build();
    }
}

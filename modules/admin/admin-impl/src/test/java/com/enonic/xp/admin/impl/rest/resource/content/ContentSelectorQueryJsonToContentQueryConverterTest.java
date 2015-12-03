package com.enonic.xp.admin.impl.rest.resource.content;

import java.time.Instant;
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
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;

import static org.junit.Assert.*;

public class ContentSelectorQueryJsonToContentQueryConverterTest
{

    private ContentService contentService;

    private RelationshipTypeService relationshipTypeService;

    private ContentTypeService contentTypeService;

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    @Before
    public void setUp()
    {
        contentTypeService = Mockito.mock( ContentTypeService.class );
        contentService = Mockito.mock( ContentService.class );
        relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
    }

    @Test
    public void testSelectorQueryWithFewAllowPaths()
        throws Exception
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "*" ).build() ).
            property( InputTypeProperty.create( "allowPath", "/path/to/parent" ).build() ).
            build();

        final ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        final Content content = createContent( "content-id", "my-content", contentType.getName() );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName" );
        ContentSelectorQueryJsonToContentQueryConverter processor = ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( contentQueryJson ).
            contentService( contentService ).
            contentTypeService( contentTypeService ).
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
        throws Exception
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "${site}/path1" ).build() ).
            property( InputTypeProperty.create( "allowPath", "${site}/path2/path3" ).build() ).
            property( InputTypeProperty.create( "allowPath", "parent-path/child-path" ).build() ).
            build();

        final ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        final Content content = createContent( "content-id", "my-content", contentType.getName() );

        final Site site = createSite( "site-id", "my-site" );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        Mockito.when( contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( site );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName" );
        ContentSelectorQueryJsonToContentQueryConverter processor = ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( contentQueryJson ).
            contentService( contentService ).
            contentTypeService( contentTypeService ).
            relationshipTypeService( relationshipTypeService ).
            build();

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:comment" ), contentQuery.getContentTypes() );
        assertEquals(
            "((_path LIKE '/content/my-site/path1*' OR _path LIKE '/content/my-site/path2/path3*') OR _path LIKE '/content/parent-path/child-path*')",
            contentQuery.getQueryExpr().toString() );
    }

    @Test(expected = Exception.class)
    public void testSingleNullSitePathResolvedToDefault()
        throws Exception
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "${site}/*" ).build() ).
            build();

        final ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        final Content content = createContent( "content-id", "my-content", contentType.getName() );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        Mockito.when( contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( null );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName" );
        ContentSelectorQueryJsonToContentQueryConverter processor = ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( contentQueryJson ).
            contentService( contentService ).
            contentTypeService( contentTypeService ).
            relationshipTypeService( relationshipTypeService ).
            build();

        processor.createQuery();
    }

    @Test
    public void testQueryWithSearchAndNoPaths()
        throws Exception
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            build();

        final ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        final Content content = createContent( "content-id", "my-content", contentType.getName() );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson(
            "(fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND')) " +
                "ORDER BY _modifiedTime DESC", 0, 100, "summary", "contentId", "inputName" );
        ContentSelectorQueryJsonToContentQueryConverter processor = getProcessor( contentQueryJson );

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:comment" ), contentQuery.getContentTypes() );
        assertEquals(
            "(fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND')) ORDER BY _modifiedtime DESC",
            contentQuery.getQueryExpr().toString() );
    }

    @Test
    public void testQueryWithSearch()
        throws Exception
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "/*" ).build() ).
            build();

        final ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        final Content content = createContent( "content-id", "my-content", contentType.getName() );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson(
            "(fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND')) " +
                "ORDER BY _modifiedTime DESC", 0, 100, "summary", "contentId", "inputName" );
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
    public void testQueryForImageSelectorInput()
        throws Exception
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "/*" ).build() ).
            build();

        final ContentType contentType = createContentTypeWithImageSelectorInput( "inputName", config );

        final Content content = createContent( "content-id", "my-content", contentType.getName() );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson(
            "(fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND')) " +
                "ORDER BY _modifiedTime DESC", 0, 100, "summary", "contentId", "inputName" );
        ContentSelectorQueryJsonToContentQueryConverter processor = getProcessor( contentQueryJson );

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:comment" ), contentQuery.getContentTypes() );
        assertEquals( "(_path LIKE '/content/*' AND (fulltext('displayName^5,_name^3,_alltext', 'check', 'AND') " +
                          "OR ngram('displayName^5,_name^3,_alltext', 'check', 'AND'))) ORDER BY _modifiedtime DESC",
                      contentQuery.getQueryExpr().toString() );
    }

    private ContentSelectorQueryJsonToContentQueryConverter getProcessor( final ContentSelectorQueryJson json )
    {
        return ContentSelectorQueryJsonToContentQueryConverter.create().
            contentQueryJson( json ).
            contentService( contentService ).
            contentTypeService( contentTypeService ).
            relationshipTypeService( relationshipTypeService ).
            build();
    }

    private ContentType createContentTypeWithSelectorInput( final String inputName, final InputTypeConfig inputTypeConfig,
                                                            boolean addBasicFieldSet, boolean useImageSelectorInput )
    {

        final FormItem formItem =
            createBasicFieldSetWithSelectorInput( inputName, inputTypeConfig, addBasicFieldSet, useImageSelectorInput );

        return ContentType.create().
            superType( ContentTypeName.structured() ).
            displayName( "My type" ).
            name( "myApplication:my-content-type" ).
            icon( Icon.from( new byte[]{123}, "image/gif", Instant.now() ) ).
            addFormItem( formItem ).
            build();
    }

    private ContentType createContentTypeWithSelectorInput( final String inputName, final InputTypeConfig inputTypeConfig )
    {
        return this.createContentTypeWithSelectorInput( inputName, inputTypeConfig, true, false );
    }

    private ContentType createContentTypeWithImageSelectorInput( final String inputName, final InputTypeConfig inputTypeConfig )
    {
        return this.createContentTypeWithSelectorInput( inputName, inputTypeConfig, false, true );
    }

    private FormItem createBasicFieldSetWithSelectorInput( final String inputName, final InputTypeConfig inputTypeConfig,
                                                           boolean addBasicFieldSet, boolean useImageSelectorInput )
    {

        final FormItem inputFormItem = Input.create().
            name( inputName ).
            label( "input" ).
            inputType( useImageSelectorInput ? InputTypeName.IMAGE_SELECTOR : InputTypeName.CONTENT_SELECTOR ).
            inputTypeConfig( inputTypeConfig ).
            build();

        if ( addBasicFieldSet )
        {
            return FieldSet.create().
                label( "basic fieldSet" ).
                name( "basic" ).
                addFormItem( inputFormItem ).
                build();
        }

        return inputFormItem;
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
            addExtraData( new ExtraData( MixinName.from( "myApplication:myField" ), metadata ) ).
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
            addExtraData( new ExtraData( MixinName.from( "myApplication:myField" ), metadata ) ).
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
            addExtraData( new ExtraData( MixinName.from( "myApplication:myField" ), metadata ) ).
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

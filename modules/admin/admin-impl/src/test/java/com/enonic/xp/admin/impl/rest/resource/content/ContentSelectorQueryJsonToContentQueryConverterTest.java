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
    public void testSelectorQuery()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "*" ).build() ).
            property( InputTypeProperty.create( "allowPath", "/path/to/parent" ).build() ).
            build();

        ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        Content content = createContent( "content-id", "my-content", contentType.getName() );

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
    public void testQueryWithSearchInQuery()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "/*" ).build() ).
            build();

        ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        Content content = createContent( "content-id", "my-content", contentType.getName() );

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
    public void testAllowChildrenPathInQuery()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "./*" ).build() ).
            build();

        ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        Content content = createContent( "content-id", "my-content", contentType.getName() );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName" );
        ContentSelectorQueryJsonToContentQueryConverter processor = getProcessor( contentQueryJson );

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:comment" ), contentQuery.getContentTypes() );
        assertEquals( "_path LIKE '/content/parent-content-1/parent-content-2/my-content/*'", contentQuery.getQueryExpr().toString() );
    }

    @Test
    public void testAllowChildrenAndSiblingsPathInQuery()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "../*" ).build() ).
            build();

        ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        Content content = createContent( "content-id", "my-content", contentType.getName() );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName" );
        ContentSelectorQueryJsonToContentQueryConverter processor = getProcessor( contentQueryJson );

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:comment" ), contentQuery.getContentTypes() );
        assertEquals( "_path LIKE '/content/parent-content-1/parent-content-2/*'", contentQuery.getQueryExpr().toString() );
    }

    @Test
    public void testOneLevelUpPathInQuery()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "../images" ).build() ).
            build();

        ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        Content content = createContent( "content-id", "my-content", contentType.getName() );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName" );
        ContentSelectorQueryJsonToContentQueryConverter processor = getProcessor( contentQueryJson );

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:comment" ), contentQuery.getContentTypes() );
        assertEquals( "_path LIKE '/content/parent-content-1/parent-content-2/images*'", contentQuery.getQueryExpr().toString() );
    }

    @Test
    public void testTwoLevelsUpPathInQuery()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( InputTypeProperty.create( "relationship", "system:reference" ).build() ).
            property( InputTypeProperty.create( "allowContentType", "myApplication:comment" ).build() ).
            property( InputTypeProperty.create( "allowPath", "../../images" ).build() ).
            build();

        ContentType contentType = createContentTypeWithSelectorInput( "inputName", config );

        Content content = createContent( "content-id", "my-content", contentType.getName() );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( contentType );

        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).
            thenReturn( content );

        ContentSelectorQueryJson contentQueryJson = new ContentSelectorQueryJson( "", 0, 100, "summary", "contentId", "inputName" );
        ContentSelectorQueryJsonToContentQueryConverter processor = getProcessor( contentQueryJson );

        final ContentQuery contentQuery = processor.createQuery();

        assertEquals( 0, contentQuery.getFrom() );
        assertEquals( 100, contentQuery.getSize() );
        assertEquals( ContentTypeNames.from( "myApplication:comment" ), contentQuery.getContentTypes() );
        assertEquals( "_path LIKE '/content/parent-content-1/images*'", contentQuery.getQueryExpr().toString() );
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

    private ContentType createContentTypeWithSelectorInput( final String inputName, final InputTypeConfig inputTypeConfig )
    {
        return ContentType.create().
            superType( ContentTypeName.structured() ).
            displayName( "My type" ).
            name( "myApplication:my-content-type" ).
            icon( Icon.from( new byte[]{123}, "image/gif", Instant.now() ) ).
            addFormItem( createBasicFieldSetWithSelectorInput( inputName, inputTypeConfig ) ).
            build();
    }

    private FieldSet createBasicFieldSetWithSelectorInput( final String inputName, final InputTypeConfig inputTypeConfig )
    {
        return FieldSet.create().
            label( "basic fieldSet" ).
            name( "basic" ).
            addFormItem( Input.create().
                name( inputName ).
                label( "input" ).
                inputType( InputTypeName.CONTENT_SELECTOR ).
                inputTypeConfig( inputTypeConfig ).
                build() ).
            build();
    }

    private Content createContent( final String id, final String name, final ContentTypeName contentTypeName )
    {
        final PropertyTree metadata = new PropertyTree();

        Content parent1 = Content.create().
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

        Content parent2 = Content.create().
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
}

package com.enonic.xp.admin.impl.rest.resource.schema.content;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ContentTypeResourceTest
    extends AdminResourceTestSupport
{
    private static final Instant SOME_DATE = LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );

    private static final ContentTypeName MY_CTY_QUALIFIED_NAME = ContentTypeName.from( "myapplication:my_cty" );

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private LocaleService localeService;

    private ContentTypeResource resource;

    public ContentTypeResourceTest()
    {
        super();
    }

    @Override
    protected Object getResourceInstance()
    {
        this.resource = new ContentTypeResource();
        contentTypeService = Mockito.mock( ContentTypeService.class );
        contentService = Mockito.mock( ContentService.class );
        localeService = Mockito.mock( LocaleService.class );

        this.resource.setContentTypeService( contentTypeService );
        this.resource.setContentService( contentService );
        this.resource.setLocaleService( localeService );

        return this.resource;
    }

    @Test
    public void get_contentType_with_only_one_input()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.create().
            name( MY_CTY_QUALIFIED_NAME ).
            createdTime( SOME_DATE ).
            superType( ContentTypeName.unstructured() ).
            displayName( "My ContentType" ).
            description( "My description" ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            addFormItem( Input.create().
                name( "myTextLine" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        // execute
        String jsonString =
            request().path( "schema/content" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).queryParam( "inlineMixinsToFormItems",
                                                                                                                  "false" ).get().getAsString();

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_with_only_one_input-result.json", jsonString );
    }

    @Test
    public void get_contentType_i18n_fieldSet()
        throws Exception
    {
        final ContentType contentType = ContentType.create().
            name( MY_CTY_QUALIFIED_NAME ).
            createdTime( SOME_DATE ).
            superType( ContentTypeName.unstructured() ).
            displayName( "My ContentType" ).
            description( "My description" ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            addFormItem( FieldSet.create().
                name( "myFieldSet" ).
                label( "My field set" ).
                labelI18nKey( "key.label" ).
                addFormItem( Input.create().
                    name( "myTextLine" ).
                    inputType( InputTypeName.TEXT_LINE ).
                    label( "My text line" ).
                    labelI18nKey( "key.label" ).
                    helpTextI18nKey( "key.help-text" ).
                    required( true ).
                    build() ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.label" ) ).thenReturn( "translated.label" );
        Mockito.when( messageBundle.localize( "key.help-text" ) ).thenReturn( "translated.helpText" );

        Mockito.when( this.localeService.getBundle( Mockito.any(), Mockito.any() ) ).thenReturn( messageBundle );

        // execute
        String jsonString =
            request().path( "schema/content" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).queryParam( "inlineMixinsToFormItems",
                                                                                                                  "false" ).get().getAsString();

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_i18n_fieldSet.json", jsonString );
    }

    @Test
    public void get_contentType_i18n_itemSet()
        throws Exception
    {
        final ContentType contentType = ContentType.create().
            name( MY_CTY_QUALIFIED_NAME ).
            createdTime( SOME_DATE ).
            superType( ContentTypeName.unstructured() ).
            displayName( "My ContentType" ).
            description( "My description" ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            addFormItem( FormItemSet.create().
                name( "myFormItemSet" ).
                label( "My Form Item Set" ).
                labelI18nKey( "key.label" ).
                addFormItem( Input.create().
                    name( "myTextLine" ).
                    inputType( InputTypeName.TEXT_LINE ).
                    label( "My text line" ).
                    labelI18nKey( "key.label" ).
                    helpTextI18nKey( "key.help-text" ).
                    required( true ).
                    build() ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.label" ) ).thenReturn( "translated.label" );
        Mockito.when( messageBundle.localize( "key.help-text" ) ).thenReturn( "translated.helpText" );

        Mockito.when( this.localeService.getBundle( Mockito.any(), Mockito.any() ) ).thenReturn( messageBundle );

        // execute
        String jsonString =
            request().path( "schema/content" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).queryParam( "inlineMixinsToFormItems",
                                                                                                                  "false" ).get().getAsString();

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_i18n_itemSet.json", jsonString );
    }

    @Test
    public void get_contentType_i18n_optionSet()
        throws Exception
    {
        final ContentType contentType = ContentType.create().
            name( MY_CTY_QUALIFIED_NAME ).
            createdTime( SOME_DATE ).
            superType( ContentTypeName.unstructured() ).
            displayName( "My ContentType" ).
            description( "My description" ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            addFormItem( FormOptionSet.create().
                name( "myOptionSet" ).
                label( "My Option Set" ).
                labelI18nKey( "key.label" ).
                helpTextI18nKey( "key.help-text" ).
                addOptionSetOption( FormOptionSetOption.create().
                    name( "option" ).
                    label( "My option" ).
                    helpText( "Option help text" ).
                    labelI18nKey( "key.label" ).
                    helpTextI18nKey( "key.help-text" ).
                    addFormItem( Input.create().
                        name( "myTextLine" ).
                        inputType( InputTypeName.TEXT_LINE ).
                        label( "My text line" ).
                        labelI18nKey( "key.label" ).
                        helpTextI18nKey( "key.help-text" ).
                        required( true ).
                        build() ).
                    build() ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.label" ) ).thenReturn( "translated.label" );
        Mockito.when( messageBundle.localize( "key.help-text" ) ).thenReturn( "translated.helpText" );

        Mockito.when( this.localeService.getBundle( Mockito.any(), Mockito.any() ) ).thenReturn( messageBundle );

        // execute
        String jsonString =
            request().path( "schema/content" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).queryParam( "inlineMixinsToFormItems",
                                                                                                                  "false" ).get().getAsString();

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_i18n_optionSet.json", jsonString );
    }

    @Test
    public void get_contentType_i18n()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.create().
            name( MY_CTY_QUALIFIED_NAME ).
            createdTime( SOME_DATE ).
            superType( ContentTypeName.unstructured() ).
            displayName( "My ContentType" ).
            displayNameI18nKey( "key.display-name" ).
            description( "My description" ).
            descriptionI18nKey( "key.description" ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            addFormItem( Input.create().
                name( "myTextLine" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                labelI18nKey( "key.label" ).
                helpTextI18nKey( "key.help-text" ).
                required( true ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.label" ) ).thenReturn( "translated.label" );
        Mockito.when( messageBundle.localize( "key.help-text" ) ).thenReturn( "translated.helpText" );
        Mockito.when( messageBundle.localize( "key.display-name" ) ).thenReturn( "translated.displayName" );
        Mockito.when( messageBundle.localize( "key.description" ) ).thenReturn( "translated.description" );

        Mockito.when( this.localeService.getBundle( Mockito.any(), Mockito.any() ) ).thenReturn( messageBundle );

        // execute
        String jsonString =
            request().path( "schema/content" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).queryParam( "inlineMixinsToFormItems",
                                                                                                                  "false" ).get().getAsString();

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_i18n.json", jsonString );
    }

    @Test
    public void get_contentType_with_all_formItem_types()
        throws Exception
    {
        // setup

        Input myTextLine = Input.create().
            name( "myTextLine" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "My text line" ).
            required( true ).
            build();

        Input myCustomInput = Input.create().
            name( "myCheckbox" ).
            inputType( InputTypeName.CHECK_BOX ).
            label( "My checkbox input" ).
            required( false ).
            build();

        FieldSet myFieldSet = FieldSet.create().
            name( "myFieldSet" ).
            label( "My field set" ).
            addFormItem( Input.create().
                name( "myTextLine2" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        FormItemSet myFormItemSet = FormItemSet.create().
            name( "myFormItemSet" ).
            label( "My form item set" ).
            addFormItem( Input.create().
                name( "myTextLine" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        final FormOptionSet formOptionSet = FormOptionSet.create().
            name( "myOptionSet" ).
            label( "My option set" ).
            helpText( "Option set help text" ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "myTextLine1" ).label( "myTextLine1" ).inputType(
                        InputTypeName.TEXT_LINE ).build() ).build() ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "myTextLine2" ).label( "myTextLine2" ).inputType(
                        InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        InlineMixin myInline = InlineMixin.create().
            mixin( "myapplication:mymixin" ).
            build();

        ContentType contentType = ContentType.create().
            createdTime( SOME_DATE ).
            name( MY_CTY_QUALIFIED_NAME ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( myTextLine ).
            addFormItem( myCustomInput ).
            addFormItem( myFieldSet ).
            addFormItem( myFormItemSet ).
            addFormItem( myInline ).
            addFormItem( formOptionSet ).
            build();

        Mockito.when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        // execute
        String jsonString =
            request().path( "schema/content" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).queryParam( "inlineMixinsToFormItems",
                                                                                                                  "false" ).get().getAsString();

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_with_all_formItem_types-result.json", jsonString );
    }

    @Test
    public void list_one_contentType_with_only_one_input()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.create().
            createdTime( SOME_DATE ).
            name( MY_CTY_QUALIFIED_NAME ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( Input.create().
                name( "myTextLine" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( contentTypeService.getAll( isA( GetAllContentTypesParams.class ) ) ).thenReturn( ContentTypes.from( contentType ) );

        // execute
        String jsonString = request().
            path( "schema/content/all" ).
            get().getAsString();

        // verify
        assertJson( "ContentTypeResourceTest-list_one_contentType_with_only_one_input-result.json", jsonString );
    }

    @Test
    public void getTypesByContentContext()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.create().
            createdTime( SOME_DATE ).
            name( MY_CTY_QUALIFIED_NAME ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( Input.create().
                name( "myTextLine" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByApplication( isA( ApplicationKey.class ) ) ).thenReturn( ContentTypes.from( contentType ) );
        final Site site = newSite();
        Mockito.when( contentService.getNearestSite( eq( ContentId.from( "1004242" ) ) ) ).thenReturn( site );

        // execute
        String jsonString = request().
            path( "schema/content/byContent" ).
            queryParam( "contentId", "1004242" ).
            get().getAsString();

        // verify
        assertJson( "ContentTypeResourceTest-list_one_contentType_with_only_one_input-result.json", jsonString );
    }

    @Test
    public void testContentTypeIcon()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        Icon schemaIcon = Icon.from( data, "image/png", Instant.now() );

        final ContentType contentType = ContentType.create().
            name( "myapplication:my_content_type" ).
            displayName( "My content type" ).
            superType( ContentTypeName.from( "myapplication:unstructured" ) ).
            icon( schemaIcon ).
            build();
        setupContentType( contentType );

        // exercise
        final Response response = this.resource.getIcon( "myapplication:my_content_type", 20, null );
        final BufferedImage contentTypeIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( contentTypeIcon, 20 );
    }

    @Test
    public void testContentTypeIconSvg()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "archive.svg" ) );
        Icon schemaIcon = Icon.from( data, "image/svg+xml", Instant.now() );

        final ContentType contentType = ContentType.create().
            name( "myapplication:icon_svg_test" ).
            displayName( "My content type" ).
            superType( ContentTypeName.from( "myapplication:unstructured" ) ).
            icon( schemaIcon ).
            build();
        setupContentType( contentType );

        // exercise
        final Response response = this.resource.getIcon( "myapplication:icon_svg_test", 20, "fed8beb6054fd1eed2916e4c1f43109c" );

        assertNotNull( response.getEntity() );
        assertEquals( schemaIcon.getMimeType(), response.getMediaType().toString() );
        org.junit.Assert.assertArrayEquals( data, (byte[]) response.getEntity() );
    }

    @Test
    public void testContentTypeIcon_fromSuperType()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "contenttypeicon.png" ) );
        Icon schemaIcon = Icon.from( data, "image/png", Instant.now() );

        final ContentType systemContentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:unstructured" ).
            displayName( "Unstructured" ).
            icon( schemaIcon ).
            build();
        setupContentType( systemContentType );

        final ContentType contentType = ContentType.create().
            name( "myapplication:my_content_type" ).
            displayName( "My content type" ).
            superType( systemContentType.getName() ).
            build();
        setupContentType( contentType );

        // exercise
        final Response response = this.resource.getIcon( "myapplication:my_content_type", 20, null );
        final BufferedImage contentTypeIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( contentTypeIcon, 20 );
    }

    @Test(expected = javax.ws.rs.WebApplicationException.class)
    public void testContentTypeIcon_notFound()
        throws Exception
    {
        Mockito.when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( null );

        try
        {
            // exercise
            this.resource.getIcon( "myapplication:my_content_type", 10, null );
        }
        catch ( WebApplicationException e )
        {
            // verify
            assertEquals( 404, e.getResponse().getStatus() ); // HTTP Not Found
            throw e;
        }
    }

    @Test
    public void getMimeTypes()
    {
        final Set<String> mimeTypes = Sets.newHashSet();
        mimeTypes.add( "mimeType1" );
        mimeTypes.add( "mimeType2" );

        final ContentTypeNames contentTypeNames = ContentTypeNames.from( ContentTypeName.documentMedia(), ContentTypeName.audioMedia() );

        Mockito.when( contentTypeService.getMimeTypes( contentTypeNames ) ).thenReturn( mimeTypes );

        final Collection<String> result =
            this.resource.getMimeTypes( ContentTypeName.documentMedia().toString() + "," + ContentTypeName.audioMedia().toString() );

        assertEquals( mimeTypes.size(), result.size() );
        assertTrue( result.contains( "mimeType1" ) );
        assertTrue( result.contains( "mimeType2" ) );
    }

    private void setupContentType( final ContentType contentType )
    {
        final List<ContentType> list = Lists.newArrayList();
        list.add( contentType );
        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentType.getName() );
        Mockito.when( contentTypeService.getByName( params ) ).thenReturn( contentType );
    }

    public static Site newSite()
    {
        final PropertyTree siteConfigConfig = new PropertyTree();
        final SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapp" ) ).
            config( siteConfigConfig ).
            build();

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "1004242" ) );
        site.siteConfigs( SiteConfigs.from( siteConfig ) );
        site.name( "my-content" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }
}

package com.enonic.xp.admin.impl.rest.resource.schema.content;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static org.junit.Assert.*;

public class ContentTypeResourceTest
    extends AdminResourceTestSupport
{
    private static final Instant SOME_DATE = LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );

    private static final ContentTypeName MY_CTY_QUALIFIED_NAME = ContentTypeName.from( "myapplication:my_cty" );

    private ContentTypeService contentTypeService;

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
        this.resource.setContentTypeService( contentTypeService );
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

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        // execute
        String jsonString =
            request().path( "schema/content" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).queryParam( "inlineMixinsToFormItems",
                                                                                                                  "false" ).get().getAsString();

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_with_only_one_input-result.json", jsonString );
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
                name( "myTextLine" ).
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
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

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

        Mockito.when( contentTypeService.getAll( Mockito.isA( GetAllContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( contentType ) );

        // execute
        String jsonString = request().
            path( "schema/content/all" ).
            queryParam( "names", MY_CTY_QUALIFIED_NAME.toString() ).
            queryParam( "format", "JSON" ).
            queryParam( "inlineMixinsToFormItems", "false" ).
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
        final Response response = this.resource.getIcon( "myapplication:icon_svg_test", 20, null );

        assertNotNull( response.getEntity() );
        assertEquals( schemaIcon.getMimeType(), response.getMediaType().toString() );
        org.junit.Assert.assertArrayEquals( data, ( byte[] )response.getEntity() );
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
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( null );

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

    private void setupContentType( final ContentType contentType )
    {
        final List<ContentType> list = Lists.newArrayList();
        list.add( contentType );
        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentType.getName() );
        Mockito.when( contentTypeService.getByName( params ) ).thenReturn( contentType );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }
}

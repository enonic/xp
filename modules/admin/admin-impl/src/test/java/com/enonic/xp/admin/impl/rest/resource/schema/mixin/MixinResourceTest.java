package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertNotNull;

public class MixinResourceTest
    extends AdminResourceTestSupport
{
    private final static MixinName MY_MIXIN_QUALIFIED_NAME_1 = MixinName.from( "myapplication:input_text_1" );

    private final static String MY_MIXIN_INPUT_NAME_1 = "input_text_1";

    private final static MixinName MY_MIXIN_QUALIFIED_NAME_2 = MixinName.from( "myapplication:text_area_2" );

    private final static String MY_MIXIN_INPUT_NAME_2 = "text_area_2";

    private MixinService mixinService;

    private MixinResource resource;

    @Override
    protected Object getResourceInstance()
    {
        mixinService = Mockito.mock( MixinService.class );

        resource = new MixinResource();
        resource.setMixinService( mixinService );

        return resource;
    }

    @Test
    public final void test_get_mixin()
        throws Exception
    {
        Mixin mixin = Mixin.create().
            createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).
            name( MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_1 ).inputType( InputTypeName.TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

        String response = request().path( "schema/mixin" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get().getAsString();

        assertJson( "get_mixin.json", response );
    }

    @Test
    public final void test_get_mixin_not_found()
        throws Exception
    {
        Mockito.when( mixinService.getByName( Mockito.any( MixinName.class ) ) ).thenReturn( null );

        final MockRestResponse response = request().path( "schema/mixin" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get();
        Assert.assertEquals( 404, response.getStatus() );
    }

    @Test
    public final void test_list_mixins()
        throws Exception
    {
        Mixin mixin1 = Mixin.create().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_1 ).inputType( InputTypeName.TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mixin mixin2 = Mixin.create().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_MIXIN_QUALIFIED_NAME_2.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_2 ).inputType( InputTypeName.TEXT_AREA ).label( "Text Area" ).required(
                true ).helpText( "Help text area" ).required( true ).build() ).build();

        Mockito.when( mixinService.getAll() ).thenReturn( Mixins.from( mixin1, mixin2 ) );

        String result = request().path( "schema/mixin/list" ).get().getAsString();

        assertJson( "list_mixins.json", result );
    }


    @Test
    public void testMixinIcon()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "mixinicon.png" ) );
        final Icon icon = Icon.from( data, "image/png", Instant.now() );

        Mixin mixin = Mixin.create().
            name( "myapplication:postal_code" ).
            displayName( "My content type" ).
            icon( icon ).
            addFormItem( Input.create().name( "postal_code" ).label( "Postal code" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        setupMixin( mixin );

        // exercise
        final Response response = this.resource.getIcon( "myapplication:postal_code", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testMixinIcon_default_image()
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( "mixin.svg" );
        final Response response = this.resource.getIcon( "myapplication:icon_svg_test", 20, null );

        assertNotNull( response.getEntity() );
        org.junit.Assert.assertArrayEquals( ByteStreams.toByteArray( in ), ( byte[] )response.getEntity() );
    }

    @Test
    public void getIconIsSvg()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "icon-black.svg" ) );
        final Icon icon = Icon.from( data, "image/svg+xml", Instant.now() );

        Mixin mixin = Mixin.create().
                name( "myapplication:icon_svg_test" ).
                displayName( "My content type" ).
                icon( icon ).
                addFormItem( Input.create().name( "icon_svg_test" ).label( "SVG icon test" ).inputType( InputTypeName.TEXT_LINE ).build() ).
                build();
        setupMixin( mixin );

        final Response response = this.resource.getIcon( "myapplication:icon_svg_test", 20, null );

        assertNotNull( response.getEntity() );
        assertEquals( icon.getMimeType(), response.getMediaType().toString() );
        org.junit.Assert.assertArrayEquals( data, ( byte[] )response.getEntity() );
    }

    private void setupMixin( final Mixin mixin )
    {
        Mockito.when( mixinService.getByName( mixin.getName() ) ).thenReturn( mixin );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }

}

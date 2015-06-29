package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Resources;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.admin.impl.rest.resource.MockRestResponse;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.form.inputtype.NullConfig;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;

import static com.enonic.xp.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.xp.form.inputtype.InputTypes.TEXT_LINE;
import static com.enonic.xp.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class MixinResourceTest
    extends AbstractResourceTest
{
    private final static MixinName MY_MIXIN_QUALIFIED_NAME_1 = MixinName.from( "mymodule:input_text_1" );

    private final static String MY_MIXIN_INPUT_NAME_1 = "input_text_1";

    private final static MixinName MY_MIXIN_QUALIFIED_NAME_2 = MixinName.from( "mymodule:text_area_2" );

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
        Mixin mixin = Mixin.newMixin().
            createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).
            name( MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_1 ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build() ).build();

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
        Mixin mixin1 = Mixin.newMixin().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_1 ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build() ).build();

        Mixin mixin2 = Mixin.newMixin().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_MIXIN_QUALIFIED_NAME_2.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_2 ).inputType( TEXT_AREA ).inputTypeConfig( NullConfig.create() ).label(
                "Text Area" ).required( true ).helpText( "Help text area" ).required( true ).build() ).build();

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

        Mixin mixin = newMixin().
            name( "mymodule:postal_code" ).
            displayName( "My content type" ).
            icon( icon ).
            addFormItem( Input.create().name( "postal_code" ).label( "Postal code" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        setupMixin( mixin );

        // exercise
        final Response response = this.resource.getIcon( "mymodule:postal_code", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testMixinIcon_default_image()
        throws Exception
    {
        Mixin mixin = newMixin().
            name( "mymodule:postal_code" ).
            displayName( "My content type" ).
            addFormItem( Input.create().name( "postal_code" ).label( "Postal code" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        setupMixin( mixin );

        // exercise
        final Response response = this.resource.getIcon( "mymodule:postal_code", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
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

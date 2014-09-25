package com.enonic.wem.admin.rest.resource.schema.mixin;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.resource.MockRestResponse;
import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;

public class MixinResourceTest
    extends AbstractResourceTest
{
    private final static MixinName MY_MIXIN_QUALIFIED_NAME_1 = MixinName.from( "mymodule:input_text_1" );

    private final static String MY_MIXIN_INPUT_NAME_1 = "input_text_1";

    private final static MixinName MY_MIXIN_QUALIFIED_NAME_2 = MixinName.from( "mymodule:text_area_2" );

    private final static String MY_MIXIN_INPUT_NAME_2 = "text_area_2";

    private MixinService mixinService;

    @Override
    protected Object getResourceInstance()
    {
        mixinService = Mockito.mock( MixinService.class );

        final MixinResource resource = new MixinResource();
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
            newInput().name( MY_MIXIN_INPUT_NAME_1 ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( mixinService.getByName( Mockito.isA( GetMixinParams.class ) ) ).thenReturn( mixin );

        String response = request().path( "schema/mixin" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get().getAsString();

        assertJson( "get_mixin.json", response );
    }

    @Test
    public final void test_get_mixin_not_found()
        throws Exception
    {
        Mockito.when( mixinService.getByName( Mockito.any( GetMixinParams.class ) ) ).thenReturn( null );

        final MockRestResponse response = request().path( "schema/mixin" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get();
        Assert.assertEquals( 404, response.getStatus() );
        Assert.assertEquals( "Mixin [mymodule:input_text_1] was not found.", response.getAsString() );
    }

    @Test
    public final void test_list_mixins()
        throws Exception
    {
        Mixin mixin1 = Mixin.newMixin().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            newInput().name( MY_MIXIN_INPUT_NAME_1 ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build() ).build();

        Mixin mixin2 = Mixin.newMixin().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_MIXIN_QUALIFIED_NAME_2.toString() ).addFormItem(
            newInput().name( MY_MIXIN_INPUT_NAME_2 ).inputType( TEXT_AREA ).inputTypeConfig( TEXT_AREA.getDefaultConfig() ).label(
                "Text Area" ).required( true ).helpText( "Help text area" ).required( true ).build() ).build();

        Mockito.when( mixinService.getAll() ).thenReturn( Mixins.from( mixin1, mixin2 ) );

        String result = request().path( "schema/mixin/list" ).get().getAsString();

        assertJson( "list_mixins.json", result );
    }
}

package com.enonic.wem.admin.rest.resource.schema.mixin;

import java.io.ByteArrayInputStream;

import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.UniformInterfaceException;

import junit.framework.Assert;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.SchemaId;
import com.enonic.wem.api.schema.mixin.CreateMixinParams;
import com.enonic.wem.api.schema.mixin.DeleteMixinParams;
import com.enonic.wem.api.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.GetMixinsParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinAlreadyExistException;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.UnableToDeleteMixinException;
import com.enonic.wem.api.schema.mixin.UpdateMixinParams;
import com.enonic.wem.api.schema.mixin.UpdateMixinResult;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MixinResourceTest
    extends AbstractResourceTest
{
    private static MixinName MY_MIXIN_QUALIFIED_NAME_1 = MixinName.from( "input_text_1" );

    private static MixinName MY_MIXIN_QUALIFIED_NAME_2 = MixinName.from( "text_area_1" );

    private MixinService mixinService;

    private BlobService blobService;

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Override
    protected Object getResourceInstance()
    {
        mixinService = Mockito.mock( MixinService.class );
        blobService = Mockito.mock( BlobService.class );

        final MixinResource resource = new MixinResource();
        resource.setMixinService( mixinService );
        resource.setBlobService( blobService );

        return resource;
    }

    @Test
    public final void test_get_mixin()
        throws Exception
    {
        Mixin mixin = Mixin.newMixin().
            createdTime( new DateTime( 2013, 1, 1, 12, 0, 0, DateTimeZone.UTC ).toInstant() ).
            name( MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            newInput().name( MY_MIXIN_QUALIFIED_NAME_1.toString() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( mixinService.getByName( Mockito.isA( GetMixinParams.class ) ) ).thenReturn( mixin );

        String response = resource().path( "schema/mixin" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get( String.class );

        assertJson( "get_mixin.json", response );
    }

    @Test
    public final void test_get_mixin_config()
        throws Exception
    {
        Mixin mixin = Mixin.newMixin().createdTime( new DateTime( 2013, 1, 1, 12, 0, 0, DateTimeZone.UTC ).toInstant() ).name(
            MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            newInput().name( MY_MIXIN_QUALIFIED_NAME_1.toString() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( mixinService.getByName( Mockito.isA( GetMixinParams.class ) ) ).thenReturn( mixin );

        String result =
            resource().path( "schema/mixin/config" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get( String.class );

        assertJson( "get_mixin_config.json", result );
    }

    @Test
    public final void test_get_mixin_not_found()
        throws Exception
    {
        Mockito.when( mixinService.getByName( Mockito.any( GetMixinParams.class ) ) ).thenReturn( null );
        try
        {
            resource().path( "schema/mixin" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get( String.class );

            Assert.assertFalse( "Exception should've been thrown already", true );
        }
        catch ( UniformInterfaceException e )
        {
            Assert.assertEquals( 404, e.getResponse().getStatus() );
            Assert.assertEquals( "Mixin [input_text_1] was not found.", e.getResponse().getEntity( String.class ) );
        }
    }

    @Test
    public final void test_get_mixin_config_not_found()
    {
        Mockito.when( mixinService.getByName( Mockito.any( GetMixinParams.class ) ) ).thenReturn( null );
        try
        {
            resource().path( "schema/mixin/config" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get( String.class );

            Assert.assertFalse( "Exception should've been thrown already", true );
        }
        catch ( UniformInterfaceException e )
        {
            Assert.assertEquals( 404, e.getResponse().getStatus() );
            Assert.assertEquals( "Mixin [input_text_1] was not found.", e.getResponse().getEntity( String.class ) );
        }
    }

    @Test
    public final void test_list_mixins()
        throws Exception
    {
        Mixin mixin1 = Mixin.newMixin().createdTime( new DateTime( 2013, 1, 1, 12, 0, 0, DateTimeZone.UTC ).toInstant() ).name(
            MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            newInput().name( MY_MIXIN_QUALIFIED_NAME_1.toString() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build() ).build();

        Mixin mixin2 = Mixin.newMixin().createdTime( new DateTime( 2013, 1, 1, 12, 0, 0, DateTimeZone.UTC ).toInstant() ).name(
            MY_MIXIN_QUALIFIED_NAME_2.toString() ).addFormItem(
            newInput().name( MY_MIXIN_QUALIFIED_NAME_2.toString() ).inputType( TEXT_AREA ).inputTypeConfig(
                TEXT_AREA.getDefaultConfig() ).label( "Text Area" ).required( true ).helpText( "Help text area" ).required(
                true ).build() ).build();

        Mockito.when( mixinService.getAll() ).thenReturn( Mixins.from( mixin1, mixin2 ) );

        String result = resource().path( "schema/mixin/list" ).get( String.class );

        assertJson( "list_mixins.json", result );
    }

    @Test
    public void test_create_mixin()
        throws Exception
    {
        // setup
        Mockito.when( mixinService.getByNames( isA( GetMixinsParams.class ) ) ).thenReturn( Mixins.empty() );
        Mockito.when( mixinService.create( isA( CreateMixinParams.class ) ) ).thenReturn( Mixin.newMixin().
            id( new SchemaId( "abc" ) ).
            name( "my_set" ).
            description( "description" ).
            build() );

        String result = resource().path( "schema/mixin/create" ).entity( readFromFile( "create_mixin_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "create_mixin.json", result );
        verify( mixinService, times( 1 ) ).create( isA( CreateMixinParams.class ) );
    }

    @Test
    public void test_create_mixin_already_exists()
        throws Exception
    {
        Mixin mixin = newMixin().createdTime( new DateTime( 2013, 1, 1, 12, 0, 0 ).toInstant() ).name( "some_input" ).addFormItem(
            newInput().name( "some_input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        Mockito.when( mixinService.create( isA( CreateMixinParams.class ) ) ).thenThrow(
            new MixinAlreadyExistException( MixinName.from( "my_set" ) ) );

        String result = resource().path( "schema/mixin/create" ).entity( readFromFile( "create_mixin_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "create_mixin_already_exists.json", result );
    }

    @Test
    public void test_create_mixin_with_icon()
        throws Exception
    {
        // setup
        Mockito.when( mixinService.getByNames( isA( GetMixinsParams.class ) ) ).thenReturn( Mixins.empty() );
        Mockito.when( mixinService.create( isA( CreateMixinParams.class ) ) ).thenReturn( Mixin.newMixin().
            id( new SchemaId( "abc" ) ).
            name( "my_set" ).
            description( "description" ).
            build() );
        final Blob iconBlob = Mockito.mock( Blob.class );
        Mockito.when( iconBlob.getStream() ).thenReturn( new ByteArrayInputStream( "icondata".getBytes() ) );
        Mockito.when( blobService.get( isA( BlobKey.class ) ) ).thenReturn( iconBlob );

        String result = resource().path( "schema/mixin/create" ).entity( readFromFile( "create_mixin_with_icon_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );

        assertJson( "create_mixin.json", result );
        verify( mixinService, times( 1 ) ).create( isA( CreateMixinParams.class ) );
    }

    @Test
    public void test_update_mixin()
        throws Exception
    {
        Mixin mixin = newMixin().name( "some_input" ).addFormItem(
            newInput().name( "some_input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        Mockito.when( mixinService.update( isA( UpdateMixinParams.class ) ) ).thenReturn( new UpdateMixinResult( mixin ) );
        final Blob iconBlob = Mockito.mock( Blob.class );
        Mockito.when( iconBlob.getStream() ).thenReturn( new ByteArrayInputStream( "icondata".getBytes() ) );
        Mockito.when( blobService.get( isA( BlobKey.class ) ) ).thenReturn( iconBlob );

        String result = resource().path( "schema/mixin/update" ).entity( readFromFile( "update_mixin_with_icon_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "update_mixin.json", result );
        verify( mixinService, times( 1 ) ).update( isA( UpdateMixinParams.class ) );
    }

    @Test
    public void test_update_mixin_not_found()
        throws Exception
    {
        Mockito.when( mixinService.update( isA( UpdateMixinParams.class ) ) ).thenThrow(
            new MixinNotFoundException( MixinName.from( "my_set" ) ) );
        String result = resource().path( "schema/mixin/update" ).entity( readFromFile( "create_mixin_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "update_mixin_not_found.json", result );
    }

    @Test
    public void test_delete_single_mixin()
        throws Exception
    {
        Mockito.when( mixinService.delete( Mockito.isA( DeleteMixinParams.class ) ) ).thenReturn(
            new DeleteMixinResult( Mixin.newMixin().name( "existing_mixin" ).build() ) );

        String result = resource().path( "schema/mixin/delete" ).entity( readFromFile( "delete_single_mixin_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "delete_single_mixin.json", result );
    }

    @Test
    public void test_delete_multiple_mixins()
        throws Exception
    {
        Mockito.when( mixinService.delete( Mockito.eq( new DeleteMixinParams().name( MixinName.from( "existing_mixin" ) ) ) ) ).thenReturn(
            new DeleteMixinResult( Mixin.newMixin().name( MixinName.from( "existing_mixin" ) ).build() ) );

        Mockito.when( mixinService.delete( Mockito.eq( new DeleteMixinParams().name( MixinName.from( "being_used_mixin" ) ) ) ) ).thenThrow(
            new UnableToDeleteMixinException( MixinName.from( "not_existing_mixin" ), "Being used" ) );

        Mockito.when(
            mixinService.delete( Mockito.eq( new DeleteMixinParams().name( MixinName.from( "not_existing_mixin" ) ) ) ) ).thenThrow(
            new MixinNotFoundException( MixinName.from( "being_used_mixin" ) ) );

        String result = resource().path( "schema/mixin/delete" ).entity( readFromFile( "delete_multiple_mixins_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "delete_multiple_mixins.json", result );
    }

}

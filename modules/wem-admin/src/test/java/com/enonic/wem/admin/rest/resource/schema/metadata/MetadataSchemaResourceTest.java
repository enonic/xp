package com.enonic.wem.admin.rest.resource.schema.metadata;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.resource.MockRestResponse;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaService;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;

public class MetadataSchemaResourceTest
    extends AbstractResourceTest
{
    private final static MetadataSchemaName MY_METADATA_SCHEMA_QUALIFIED_NAME_1 = MetadataSchemaName.from( "mymodule:input_text_1" );

    private final static String MY_METADATA_SCHEMA_INPUT_NAME_1 = "input_text_1";

    private MetadataSchemaService metadataSchemaService;

    private MetadataSchemaResource resource;

    @Override
    protected Object getResourceInstance()
    {
        metadataSchemaService = Mockito.mock( MetadataSchemaService.class );

        resource = new MetadataSchemaResource();
        resource.setMetadataSchemaService( metadataSchemaService );

        return resource;
    }

    @Test
    public final void test_get_metadata_schema()
        throws Exception
    {
        MetadataSchema metadataSchema = MetadataSchema.newMetadataSchema().
            createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).
            name( MY_METADATA_SCHEMA_QUALIFIED_NAME_1.toString() ).form( Form.newForm().addFormItem(
            newInput().name( MY_METADATA_SCHEMA_INPUT_NAME_1 ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build() ).build() ).build();

        Mockito.when( metadataSchemaService.getByName( Mockito.isA( MetadataSchemaName.class ) ) ).thenReturn( metadataSchema );

        String response =
            request().path( "schema/metadata" ).queryParam( "name", MY_METADATA_SCHEMA_QUALIFIED_NAME_1.toString() ).get().getAsString();

        assertJson( "get_metadata_schema.json", response );
    }

    @Test
    public final void test_get_metadata_schema_not_found()
        throws Exception
    {
        Mockito.when( metadataSchemaService.getByName( Mockito.any( MetadataSchemaName.class ) ) ).thenReturn( null );

        final MockRestResponse response =
            request().path( "schema/metadata" ).queryParam( "name", MY_METADATA_SCHEMA_QUALIFIED_NAME_1.toString() ).get();
        Assert.assertEquals( 404, response.getStatus() );
        Assert.assertEquals( "MetadataSchema [mymodule:input_text_1] was not found.", response.getAsString() );
    }


   /* @Test
    public void testMetadataSchemaIcon()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "metadataicon.png" ) );
        final Icon icon = Icon.from( data, "image/png", Instant.now() );

        MetadataSchema metadataSchema = MetadataSchema.newMetadataSchema().
            name( "mymodule:postal_code" ).
            displayName( "My metadata" ).
            icon( icon ).
            form( Form.newForm().addFormItem( newInput().name( "postal_code" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).
            build();
        setupMetadataSchema( metadataSchema );

        // exercise
        final Response response = this.resource.getIcon( "mymodule:postal_code", 20, null );
        final BufferedImage metadataSchemaIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( metadataSchemaIcon, 20 );
    }

    @Test
    public void testMetadataSchemaIcon_default_image()
        throws Exception
    {
        MetadataSchema metadataSchema = MetadataSchema.newMetadataSchema().
            name( "mymodule:postal_code" ).
            displayName( "My content type" ).
            form( Form.newForm().addFormItem( newInput().name( "postal_code" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).
            build();
        setupMetadataSchema( metadataSchema );

        // exercise
        final Response response = this.resource.getIcon( "mymodule:postal_code", 20, null );
        final BufferedImage metadataSchemaIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( metadataSchemaIcon, 20 );
    }

    private void setupMetadataSchema( final MetadataSchema metadataSchema )
    {
        final GetMetadataSchemaParams params = new GetMetadataSchemaParams( metadataSchema.getName() );
        Mockito.when( metadataSchemaService.getByName( params ) ).thenReturn( metadataSchema );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }*/

}

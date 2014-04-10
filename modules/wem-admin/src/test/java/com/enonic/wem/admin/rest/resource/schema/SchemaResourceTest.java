package com.enonic.wem.admin.rest.resource.schema;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.UniformInterfaceException;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.SchemaService;
import com.enonic.wem.api.schema.SchemaTypesParams;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.relationship.RelationshipType;

import static org.junit.Assert.*;

public class SchemaResourceTest
    extends AbstractResourceTest
{
    private SchemaService schemaService;

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    private Mixin createMixin( String displayName )
    {
        return Mixin.newMixin().name( displayName.toLowerCase() ).displayName( displayName ).description( "M description" ).createdTime(
            DateTime.parse( currentTime ) ).modifiedTime( DateTime.parse( currentTime ) ).addFormItem(
            Input.newInput().name( displayName.toLowerCase() ).inputType( InputTypes.TEXT_AREA ).inputTypeConfig(
                InputTypes.TEXT_AREA.getDefaultConfig() ).build()
        ).build();
    }

    private ContentType createContentType( String name )
    {
        return ContentType.newContentType().name( name ).createdTime( DateTime.parse( currentTime ) ).modifiedTime(
            DateTime.parse( currentTime ) ).description( "CT description" ).build();
    }

    private RelationshipType createRelationshipType( String name )
    {
        return RelationshipType.newRelationshipType().name( name ).createdTime( DateTime.parse( currentTime ) ).modifiedTime(
            DateTime.parse( currentTime ) ).description( "RT description" ).build();
    }

    private Schemas createSchemaList()
    {
        ContentType contentType = createContentType( "contenttype" );
        Mixin mixin = createMixin( "mixin" );
        RelationshipType relationshipType = createRelationshipType( "relationship" );
        return Schemas.from( contentType, mixin, relationshipType );
    }

    @Test
    public void searchSchemaByQuery()
        throws Exception
    {
        Schemas schemas = createSchemaList();
        Mockito.when( this.schemaService.getTypes( Mockito.isA( SchemaTypesParams.class ) ) ).thenReturn( schemas );

        String json = resource().path( "schema/find" ).queryParam( "search", "" ).get( String.class );
        assertJson( "schema_by_empty_query.json", json );

        json = resource().path( "schema/find" ).queryParam( "search", "mixin" ).get( String.class );
        assertJson( "schema_by_query.json", json );
    }

    @Test
    public void searchSchemaByWrongType()
    {
        try
        {
            resource().path( "schema/find" ).queryParam( "search", "" ).queryParam( "types", "SomeType" ).queryParam( "types",
                                                                                                                      "AnotherType" ).get(
                String.class );
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 406 );
            assertEquals( e.getResponse().getEntity( String.class ), "Invalid parameter 'types': [SomeType, AnotherType]" );
        }
    }

    @Test
    public void searchSchemaByTypes()
        throws Exception
    {
        ContentType contentType = createContentType( "contenttype" );
        Mixin mixin = createMixin( "mixin" );
        Schemas schemas = Schemas.from( contentType, mixin );
        Mockito.when( this.schemaService.getTypes( Mockito.isA( SchemaTypesParams.class ) ) ).thenReturn( schemas );

        String json = resource().path( "schema/find" ).queryParam( "search", "" ).queryParam( "types", "mixin" ).queryParam( "types",
                                                                                                                             "content_type" ).get(
            String.class );

        assertJson( "schema_by_types.json", json );
    }

    @Test
    public void listRootSchemas()
        throws Exception
    {
        Schemas schemas = createSchemaList();
        Mockito.when( this.schemaService.getRoot() ).thenReturn( schemas );

        String json = resource().path( "schema/list" ).get( String.class );

        assertJson( "schema_by_root.json", json );
    }

    @Test
    public void listChildSchemas()
        throws Exception
    {
        Schemas schemas = Schemas.from( createContentType( "contenttype" ) );
        Mockito.when( this.schemaService.getChildren( Mockito.isA( SchemaKey.class ) ) ).thenReturn( schemas );

        String json = resource().path( "schema/list" ).queryParam( "parentKey", "ContentType:parent" ).get( String.class );

        assertJson( "schema_by_parent.json", json );
    }

    @Test
    public void listEmptyChildSchemas()
        throws Exception
    {
        Mockito.when( this.schemaService.getChildren( Mockito.isA( SchemaKey.class ) ) ).thenReturn( Schemas.empty() );

        String json = resource().path( "schema/list" ).queryParam( "parentKey", "ContentType:parent" ).get( String.class );
        assertEquals( "[]", json );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.schemaService = Mockito.mock( SchemaService.class );

        final SchemaResource resource = new SchemaResource();
        resource.schemaService = this.schemaService;
        return resource;
    }
}

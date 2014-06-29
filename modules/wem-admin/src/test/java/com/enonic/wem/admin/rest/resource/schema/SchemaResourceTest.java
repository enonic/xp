package com.enonic.wem.admin.rest.resource.schema;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.resource.MockRestResponse;
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

    private Mixin createMixin( String displayName )
    {
        return Mixin.newMixin().name( displayName.toLowerCase() ).displayName( displayName ).description( "M description" ).createdTime(
            Instant.parse( currentTime ) ).modifiedTime( Instant.parse( currentTime ) ).addFormItem(
            Input.newInput().name( displayName.toLowerCase() ).inputType( InputTypes.TEXT_AREA ).inputTypeConfig(
                InputTypes.TEXT_AREA.getDefaultConfig() ).build() ).build();
    }

    private ContentType createContentType( String name )
    {
        return ContentType.newContentType().name( name ).createdTime( Instant.parse( currentTime ) ).modifiedTime(
            Instant.parse( currentTime ) ).description( "CT description" ).build();
    }

    private RelationshipType createRelationshipType( String name )
    {
        return RelationshipType.newRelationshipType().name( name ).createdTime( Instant.parse( currentTime ) ).modifiedTime(
            Instant.parse( currentTime ) ).description( "RT description" ).build();
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

        String json = request().path( "schema/find" ).queryParam( "search", "" ).get().getAsString();
        assertJson( "schema_by_empty_query.json", json );

        json = request().path( "schema/find" ).queryParam( "search", "mixin" ).get().getAsString();
        assertJson( "schema_by_query.json", json );
    }

    @Test
    public void searchSchemaByWrongType()
        throws Exception
    {
        final MockRestResponse response = request().path( "schema/find" ).
            queryParam( "search", "" ).
            queryParam( "types", "SomeType" ).
            queryParam( "types", "AnotherType" ).
            get();

        assertEquals( response.getStatus(), 406 );
        assertEquals( response.getAsString(), "Invalid parameter 'types': [SomeType, AnotherType]" );
    }

    @Test
    public void searchSchemaByTypes()
        throws Exception
    {
        ContentType contentType = createContentType( "contenttype" );
        Mixin mixin = createMixin( "mixin" );
        Schemas schemas = Schemas.from( contentType, mixin );
        Mockito.when( this.schemaService.getTypes( Mockito.isA( SchemaTypesParams.class ) ) ).thenReturn( schemas );

        String json = request().
            path( "schema/find" ).
            queryParam( "search", "" ).
            queryParam( "types", "mixin" ).
            queryParam( "types", "content_type" ).
            get().getAsString();

        assertJson( "schema_by_types.json", json );
    }

    @Test
    public void listRootSchemas()
        throws Exception
    {
        Schemas schemas = createSchemaList();
        Mockito.when( this.schemaService.getRoot() ).thenReturn( schemas );

        String json = request().path( "schema/list" ).get().getAsString();

        assertJson( "schema_by_root.json", json );
    }

    @Test
    public void listChildSchemas()
        throws Exception
    {
        Schemas schemas = Schemas.from( createContentType( "contenttype" ) );
        Mockito.when( this.schemaService.getChildren( Mockito.isA( SchemaKey.class ) ) ).thenReturn( schemas );

        String json = request().path( "schema/list" ).queryParam( "parentKey", "ContentType:parent" ).get().getAsString();

        assertJson( "schema_by_parent.json", json );
    }

    @Test
    public void listEmptyChildSchemas()
        throws Exception
    {
        Mockito.when( this.schemaService.getChildren( Mockito.isA( SchemaKey.class ) ) ).thenReturn( Schemas.empty() );

        String json = request().path( "schema/list" ).queryParam( "parentKey", "ContentType:parent" ).get().getAsString();
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

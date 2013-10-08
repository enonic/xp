package com.enonic.wem.admin.rest.resource.schema;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.UniformInterfaceException;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.GetSchemaTree;
import com.enonic.wem.api.command.schema.SchemaTypes;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.form.inputtype.TextAreaConfig;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.support.tree.Tree;

import static org.junit.Assert.*;

public class SchemaResourceTest
    extends AbstractResourceTest
{

    private Client client;

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    private Mixin createMixin( String displayName )
    {
        return Mixin.newMixin().name( displayName.toLowerCase() ).displayName( displayName ).createdTime( DateTime.parse( currentTime ) ).modifiedTime(
            DateTime.parse( currentTime ) ).addFormItem(
            Input.newInput().name( displayName.toLowerCase() ).inputType( InputTypes.TEXT_AREA ).inputTypeConfig(
                TextAreaConfig.newTextAreaConfig().rows( 10 ).columns( 10 ).build() ).build() ).build();
    }

    private ContentType createContentType( String name )
    {
        return ContentType.newContentType().name( name ).createdTime( DateTime.parse( currentTime ) ).modifiedTime(
            DateTime.parse( currentTime ) ).build();
    }

    private RelationshipType createRelationshipType( String name )
    {
        return RelationshipType.newRelationshipType().name( name ).createdTime( DateTime.parse( currentTime ) ).modifiedTime(
            DateTime.parse( currentTime ) ).build();
    }

    private Schemas createSchemaList()
    {
        ContentType contentType = createContentType( "contenttype" );
        Mixin mixin = createMixin( "mixin" );
        RelationshipType relationshipType = createRelationshipType( "relationship" );
        return Schemas.from( contentType, mixin, relationshipType );
    }

    private Tree<Schema> createSchemaTree()
    {
        Tree<Schema> tree = new Tree<>();
        ContentType rootContentType = createContentType( "rootcontenttype" );
        ContentType childContentType = createContentType( "childcontenttype" );
        tree.createNode( rootContentType ).addChild( childContentType );
        Mixin rootMixin = createMixin( "rootmixin" );
        Mixin childMixin = createMixin( "childmixin" );
        tree.createNode( rootMixin ).addChild( childMixin );
        RelationshipType rootRelationshipType = createRelationshipType( "rootrelationshiptype" );
        RelationshipType childRelationshipType = createRelationshipType( "childrelationshiptype" );
        tree.createNode( rootRelationshipType ).addChild( childRelationshipType );
        return tree;
    }


    @Test
    public void searchSchemaByQuery()
        throws Exception
    {
        Schemas schemas = createSchemaList();
        Mockito.when( client.execute( Mockito.isA( SchemaTypes.class ) ) ).thenReturn( schemas );

        String json = resource().path( "schema/list" ).queryParam( "search", "" ).get( String.class );
        assertJson( "schema_by_empty_query.json", json );

        json = resource().path( "schema/list" ).queryParam( "search", "mixin" ).get( String.class );
        assertJson( "schema_by_query.json", json );

        json = resource().path( "schema/list" ).queryParam( "search", "mixin" ).queryParam( "modules", "module" ).get( String.class );
        assertJson( "schema_by_query_and_modules.json", json );


    }

    @Test
    public void searchSchemaByWrongType()
    {
        try
        {
            resource().path( "schema/list" ).queryParam( "search", "" ).queryParam( "types", "SomeType" ).queryParam( "types",
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
    public void searchSchemaByModules()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( SchemaTypes.class ) ) ).thenReturn( createSchemaList() );

        String json = resource().path( "schema/list" ).queryParam( "search", "" ).queryParam( "modules", "module" ).get( String.class );

        assertJson( "schema_by_modules.json", json );
    }

    @Test
    public void searchSchemaByTypes()
        throws Exception
    {
        final SchemaResource schemaResource = new SchemaResource();
        schemaResource.setClient( client );
        ContentType contentType = createContentType( "contenttype" );
        Mixin mixin = createMixin( "mixin" );
        Schemas schemas = Schemas.from( contentType, mixin );
        Mockito.when( client.execute( Mockito.isA( SchemaTypes.class ) ) ).thenReturn( schemas );

        String json = resource().path( "schema/list" ).queryParam( "search", "" ).queryParam( "types", "mixin" ).queryParam( "types",
                                                                                                                             "content_type" ).get(
            String.class );

        assertJson( "schema_by_types.json", json );
    }

    @Test
    public void getSchemaTree()
        throws Exception
    {
        Tree<Schema> schemaTree = createSchemaTree();
        Mockito.when( client.execute( Mockito.isA( GetSchemaTree.class ) ) ).thenReturn( schemaTree );

        String json = resource().path( "schema/tree" ).get( String.class );

        assertJson( "schema_tree.json", json );
    }

    @Test
    public void getSchemaTreeWithTypesParam()
        throws Exception
    {
        Tree<Schema> schemaTree = createSchemaTree();
        Mockito.when( client.execute( Mockito.isA( GetSchemaTree.class ) ) ).thenReturn( schemaTree );

        String json = resource().path( "schema/tree" ).queryParam( "types", "content_type" ).
            queryParam( "types", "mixin" ).queryParam( "types", "relationship_type" ).get( String.class );

        assertJson( "schema_tree.json", json );
    }

    @Test
    public void getSchemaTreeWithWrongTypesParam()
    {
        try
        {
            resource().path( "schema/tree" ).queryParam( "types", "wrong_type" ).get( String.class );
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 406 );
            assertEquals( e.getResponse().getEntity( String.class ), "Invalid parameter 'types': [wrong_type]" );
        }
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final SchemaResource resource = new SchemaResource();
        resource.setClient( client );
        return resource;
    }
}

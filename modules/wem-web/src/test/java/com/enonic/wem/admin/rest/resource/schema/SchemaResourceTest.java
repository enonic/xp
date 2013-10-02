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
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
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

    private Mixin createMixin( String name, ModuleName module )
    {
        return Mixin.newMixin().displayName( name ).name( name ).createdTime( DateTime.parse( currentTime ) ).modifiedTime(
            DateTime.parse( currentTime ) ).module( module ).addFormItem(
            Input.newInput().name( name.toLowerCase() ).inputType( InputTypes.TEXT_AREA ).build() ).build();
    }

    private ContentType createContentType( String name, ModuleName module )
    {
        return ContentType.newContentType().name( name ).createdTime( DateTime.parse( currentTime ) ).modifiedTime(
            DateTime.parse( currentTime ) ).module( module ).build();
    }

    private RelationshipType createRelationshipType( String name, ModuleName module )
    {
        return RelationshipType.newRelationshipType().createdTime( DateTime.parse( currentTime ) ).modifiedTime(
            DateTime.parse( currentTime ) ).name( name ).module( module ).build();
    }

    private ModuleName createModuleName( String name )
    {
        return ModuleName.from( name );
    }

    private Schemas createSchemaList()
    {
        ModuleName moduleName = createModuleName( "module" );
        ContentType contentType = createContentType( "contenttype", moduleName );
        Mixin mixin = createMixin( "mixin", moduleName );
        RelationshipType relationshipType = createRelationshipType( "relationship", moduleName );
        return Schemas.from( contentType, mixin, relationshipType );
    }

    private Tree<Schema> createSchemaTree()
    {
        Tree<Schema> tree = new Tree<>();
        ModuleName moduleName = createModuleName( "tree" );
        ContentType rootContentType = createContentType( "rootcontenttype", moduleName );
        ContentType childContentType = createContentType( "childcontenttype", moduleName );
        tree.createNode( rootContentType ).addChild( childContentType );
        Mixin rootMixin = createMixin( "rootmixin", moduleName );
        Mixin childMixin = createMixin( "childmixin", moduleName );
        tree.createNode( rootMixin ).addChild( childMixin );
        RelationshipType rootRelationshipType = createRelationshipType( "rootrelationshiptype", moduleName );
        RelationshipType childRelationshipType = createRelationshipType( "childrelationshiptype", moduleName );
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
        ModuleName moduleName = createModuleName( "module" );
        ContentType contentType = createContentType( "contenttype", moduleName );
        Mixin mixin = createMixin( "mixin", moduleName );
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

package com.enonic.wem.admin.rest.resource.schema;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.admin.rest.resource.schema.exception.InvalidSchemaTypeException;
import com.enonic.wem.admin.rest.resource.schema.model.SchemaJson;
import com.enonic.wem.admin.rest.resource.schema.model.SchemaTreeJson;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.GetSchemaTree;
import com.enonic.wem.api.command.schema.SchemaTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.TextArea;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class SchemaResourceTest
{

    private Client client;

    @Before
    public void setup()
    {
        HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        Mockito.when( request.getScheme() ).thenReturn( "http" );
        ServletRequestHolder.setRequest( request );
        this.client = Mockito.mock( Client.class );
    }

    private Mixin createMixin( String displayName, ModuleName module )
    {
        return Mixin.newMixin().displayName( displayName ).module( module ).formItem(
            Input.newInput().name( displayName.toLowerCase() ).inputType( new TextArea() ).build() ).build();
    }

    private ContentType createContentType( String name, ModuleName module )
    {
        return ContentType.newContentType().name( name ).module( module ).build();
    }

    private RelationshipType createRelationshipType( String name, ModuleName module )
    {
        return RelationshipType.newRelationshipType().name( name ).module( module ).build();
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
    {
        final SchemaResource schemaResource = new SchemaResource();
        schemaResource.setClient( client );
        Schemas schemas = createSchemaList();
        Mockito.when( client.execute( Mockito.isA( SchemaTypes.class ) ) ).thenReturn( schemas );
        List<SchemaJson> result = schemaResource.list( "", new HashSet<String>(), new ArrayList<String>() );
        assertEquals( result.size(), 3 );
        List<String> names = Lists.asList( "mixin", "relationship", new String[]{"contenttype"} );
        for ( SchemaJson schemaJson : result )
        {
            assertTrue( names.contains( schemaJson.getName() ) );
        }

        result = schemaResource.list( "mixin", new HashSet<String>(), new ArrayList<String>() );
        assertEquals( result.size(), 1 );
        assertEquals( result.get( 0 ).getName(), "mixin" );

        result = schemaResource.list( "", Sets.newHashSet( "module" ), new ArrayList<String>() );
        assertEquals( result.size(), 3 );
        for ( SchemaJson schemaJson : result )
        {
            assertTrue( names.contains( schemaJson.getName() ) );
        }
        //TODO: add json assertions when their form is decided

    }

    @Test(expected = InvalidSchemaTypeException.class)
    public void searchSchemaByWrongType()
    {
        final SchemaResource schemaResource = new SchemaResource();
        schemaResource.setClient( client );
        schemaResource.list( "", new HashSet<String>(), Lists.asList( "SomeType", new String[]{"AnotherType"} ) );
    }

    @Test
    public void searchSchemaByModules()
    {
        final SchemaResource schemaResource = new SchemaResource();
        schemaResource.setClient( client );
        Schemas schemas = createSchemaList();
        Mockito.when( client.execute( Mockito.isA( SchemaTypes.class ) ) ).thenReturn( schemas );

        List<SchemaJson> result = schemaResource.list( "", Sets.newHashSet( "module" ), new ArrayList<String>() );
        List<String> names = Lists.asList( "mixin", "relationship", new String[]{"contenttype"} );
        assertEquals( result.size(), 3 );
        for ( SchemaJson schemaJson : result )
        {
            assertTrue( names.contains( schemaJson.getName() ) );
        }
        //TODO: add json assertions when their form is decided
    }

    @Test
    public void searchSchemaByTypes()
    {
        final SchemaResource schemaResource = new SchemaResource();
        schemaResource.setClient( client );
        ModuleName moduleName = createModuleName( "module" );
        ContentType contentType = createContentType( "contenttype", moduleName );
        Mixin mixin = createMixin( "mixin", moduleName );
        Schemas schemas = Schemas.from( contentType, mixin );
        Mockito.when( client.execute( Mockito.isA( SchemaTypes.class ) ) ).thenReturn( schemas );

        List<SchemaJson> result = schemaResource.list( "", new HashSet<String>(), Lists.asList( "mixin", new String[]{"content_type"} ) );
        List<String> names = Lists.asList( "mixin", new String[]{"contenttype"} );
        assertEquals( result.size(), 2 );
        for ( SchemaJson schemaJson : result )
        {
            assertTrue( names.contains( schemaJson.getName() ) );
        }
        //TODO: add json assertions when their form is decided
    }

    @Test
    public void getSchemaTree()
    {
        final SchemaResource schemaResource = new SchemaResource();
        schemaResource.setClient( client );
        Tree<Schema> schemaTree = createSchemaTree();
        Mockito.when( client.execute( Mockito.isA( GetSchemaTree.class ) ) ).thenReturn( schemaTree );
        SchemaTreeJson jsonTree = schemaResource.tree( new ArrayList<String>() );
        assertEquals( jsonTree.getTotal(), 6 );
        //TODO: add json assertions when their form is decided
    }

    @Test
    public void getSchemaTreeWithTypesParam()
    {
        final SchemaResource schemaResource = new SchemaResource();
        schemaResource.setClient( client );
        Tree<Schema> schemaTree = createSchemaTree();
        Mockito.when( client.execute( Mockito.isA( GetSchemaTree.class ) ) ).thenReturn( schemaTree );
        SchemaTreeJson jsonTree = schemaResource.tree( Lists.newArrayList( "content_type", "mixin", "relationship_type" ) );
        assertEquals( jsonTree.getTotal(), 6 );
        //TODO: add json assertions when their form is decided
    }

    @Test(expected = InvalidSchemaTypeException.class)
    public void getSchemaTreeWithWrongTypesParam()
    {
        final SchemaResource schemaResource = new SchemaResource();
        schemaResource.setClient( client );
        schemaResource.tree( Lists.asList( "wrong_type", new String[]{"content_type"} ) );
    }

}

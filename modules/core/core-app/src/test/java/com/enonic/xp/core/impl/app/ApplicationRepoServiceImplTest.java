package com.enonic.xp.core.impl.app;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.schema.SchemaNodePropertyNames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationRepoServiceImplTest
{
    private final NodeService nodeService = Mockito.mock( NodeService.class );

    private ApplicationRepoServiceImpl service;

    @BeforeEach
    void setUp()
    {
        this.service = new ApplicationRepoServiceImpl( this.nodeService );
    }

    @Test
    void create_node()
    {
        final AppInfo app = createApp();

        this.service.upsertApplicationNode( app, ByteSource.wrap( "myBinary".getBytes() ) );

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).create( Mockito.isA( CreateNodeParams.class ) );
    }

    @Test
    void update_node()
    {
        final AppInfo app = createApp();

        Mockito.when(
                this.nodeService.getByPath( new NodePath( ApplicationRepoServiceImpl.APPLICATION_PATH, NodeName.from( "myBundle" ) ) ) )
            .thenReturn(
                Node.create().id( new NodeId() ).name( "myBundle" ).parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).build() );

        this.service.upsertApplicationNode( app, ByteSource.wrap( "myBinary".getBytes() ) );

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).update( Mockito.isA( UpdateNodeParams.class ) );
    }

    @Test
    void delete_node()
    {
        final AppInfo app = createApp();

        Mockito.when( this.nodeService.getByPath( new NodePath( ApplicationRepoServiceImpl.APPLICATION_PATH, NodeName.from( "myBundle" ) ) ) )
            .thenReturn(
                Node.create().id( new NodeId() ).name( "myBundle" ).parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).build() );

        this.service.deleteApplicationNode( ApplicationKey.from( app.name ) );

        ArgumentCaptor<DeleteNodeParams> argCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).delete( argCaptor.capture() );
        assertEquals( new NodePath( ApplicationRepoServiceImpl.APPLICATION_PATH, NodeName.from( "myBundle" ) ),
                      argCaptor.getValue().getNodePath() );
    }

    @Test
    void persist_schema_creates_cms_tree()
    {
        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "enonic.yaml", ByteSource.wrap( "app-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/cms.yaml", ByteSource.wrap( "cms-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/content-types/mytype/mytype.yaml", ByteSource.wrap( "content-type".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/i18n/phrases/phrases_en.properties", ByteSource.wrap( "phrases".getBytes( StandardCharsets.UTF_8 ) ) );

        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        Mockito.verify( this.nodeService, Mockito.never() ).delete( Mockito.any( DeleteNodeParams.class ) );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 9 ) ).create( captor.capture() );

        final List<CreateNodeParams> created = captor.getAllValues();
        assertEquals( List.of( "/applications/myBundle/enonic.yaml", "/applications/myBundle/cms", "/applications/myBundle/cms/cms.yaml",
                               "/applications/myBundle/cms/content-types", "/applications/myBundle/cms/content-types/mytype",
                               "/applications/myBundle/cms/content-types/mytype/mytype.yaml", "/applications/myBundle/cms/i18n",
                               "/applications/myBundle/cms/i18n/phrases", "/applications/myBundle/cms/i18n/phrases/phrases_en.properties" ),
                      created.stream().map( params -> new NodePath( params.getParent(), params.getName() ).toString() ).toList() );

        assertEquals( "app-descriptor", created.stream()
            .filter( params -> "enonic.yaml".equals( params.getName().toString() ) )
            .findFirst()
            .orElseThrow()
            .getData()
            .getString( SchemaNodePropertyNames.RESOURCE ) );
        assertEquals( "content-type", created.stream()
            .filter( params -> "mytype.yaml".equals( params.getName().toString() ) )
            .findFirst()
            .orElseThrow()
            .getData()
            .getString( SchemaNodePropertyNames.RESOURCE ) );
        assertEquals( "phrases", created.stream()
            .filter( params -> "phrases_en.properties".equals( params.getName().toString() ) )
            .findFirst()
            .orElseThrow()
            .getData()
            .getString( SchemaNodePropertyNames.RESOURCE ) );

        Mockito.verify( this.nodeService ).refresh( RefreshMode.ALL );
    }

    @Test
    void persist_schema_stores_schema_icons_as_binaries()
    {
        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "cms/cms.yaml", ByteSource.wrap( "cms-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/content-types/mytype/mytype.svg", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );

        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.atLeastOnce() ).create( captor.capture() );

        final CreateNodeParams iconParams = created( captor, "mytype.svg" );
        assertEquals( SchemaResourcePaths.SVG_MIME_TYPE, iconParams.getData().getString( SchemaNodePropertyNames.MIME_TYPE ) );
        assertEquals( SchemaResourceNames.ICON_BINARY_REFERENCE, iconParams.getData().getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertNull( iconParams.getData().getString( SchemaNodePropertyNames.RESOURCE ) );
        assertNotNull( iconParams.getBinaryAttachments().get( SchemaResourceNames.ICON_BINARY_REFERENCE ) );
    }

    @Test
    void persist_schema_attaches_the_application_icon_to_the_descriptor()
    {
        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "enonic.yaml", ByteSource.wrap( "app-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "enonic.svg", ByteSource.wrap( "<svg>app</svg>".getBytes( StandardCharsets.UTF_8 ) ) );

        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService ).create( captor.capture() );

        // the descriptor is the only node written: no icon node, no cms marker for an application without a schema
        final CreateNodeParams descriptorParams = captor.getValue();
        assertEquals( "enonic.yaml", descriptorParams.getName().toString() );
        assertEquals( new NodePath( "/applications/myBundle" ), descriptorParams.getParent() );
        assertEquals( "app-descriptor", descriptorParams.getData().getString( SchemaNodePropertyNames.RESOURCE ) );
        assertEquals( SchemaResourcePaths.SVG_MIME_TYPE, descriptorParams.getData().getString( SchemaNodePropertyNames.MIME_TYPE ) );
        assertEquals( SchemaResourceNames.APP_ICON_BINARY_REFERENCE,
                      descriptorParams.getData().getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertNotNull( descriptorParams.getBinaryAttachments().get( SchemaResourceNames.APP_ICON_BINARY_REFERENCE ) );
    }

    @Test
    void persist_schema_descriptor_without_icon()
    {
        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ),
                                               Map.of( "enonic.yaml", ByteSource.wrap( "app-descriptor".getBytes( StandardCharsets.UTF_8 ) ) ) );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService ).create( captor.capture() );

        assertEquals( "enonic.yaml", captor.getValue().getName().toString() );
        assertNull( captor.getValue().getData().getString( SchemaNodePropertyNames.MIME_TYPE ) );
        assertNull( captor.getValue().getData().getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertEquals( 0, captor.getValue().getBinaryAttachments().getSize() );
    }

    @Test
    void persist_schema_icon_without_descriptor_creates_a_minimal_descriptor()
    {
        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ),
                                               Map.of( "enonic.svg", ByteSource.wrap( "<svg>app</svg>".getBytes( StandardCharsets.UTF_8 ) ) ) );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService ).create( captor.capture() );

        assertEquals( "enonic.yaml", captor.getValue().getName().toString() );
        assertEquals( "kind: \"Application\"\nname: \"myBundle\"\n", captor.getValue().getData().getString( SchemaNodePropertyNames.RESOURCE ) );
        assertNotNull( captor.getValue().getBinaryAttachments().get( SchemaResourceNames.APP_ICON_BINARY_REFERENCE ) );
    }

    @Test
    void persist_schema_replaces_existing_schema()
    {
        final NodePath cmsPath = new NodePath( "/applications/myBundle/cms" );
        final NodePath descriptorPath = new NodePath( "/applications/myBundle/enonic.yaml" );
        Mockito.when( this.nodeService.nodeExists( cmsPath ) ).thenReturn( true );
        Mockito.when( this.nodeService.nodeExists( descriptorPath ) ).thenReturn( true );

        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ),
                                               Map.of( "cms/cms.yaml", ByteSource.wrap( "cms".getBytes( StandardCharsets.UTF_8 ) ) ) );

        // the old schema (cms subtree and application descriptor) is removed before the new one is written
        final InOrder inOrder = Mockito.inOrder( this.nodeService );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        inOrder.verify( this.nodeService, Mockito.times( 2 ) ).delete( deleteCaptor.capture() );
        assertEquals( List.of( cmsPath, descriptorPath ), deleteCaptor.getAllValues().stream().map( DeleteNodeParams::getNodePath ).toList() );

        final ArgumentCaptor<CreateNodeParams> createCaptor = ArgumentCaptor.forClass( CreateNodeParams.class );
        inOrder.verify( this.nodeService, Mockito.times( 2 ) ).create( createCaptor.capture() );
        assertEquals( SchemaResourceNames.CMS_ROOT_NAME, createCaptor.getAllValues().get( 0 ).getName().toString() );
        assertEquals( new NodePath( "/applications/myBundle" ), createCaptor.getAllValues().get( 0 ).getParent() );
        assertEquals( "cms.yaml", createCaptor.getAllValues().get( 1 ).getName().toString() );
    }

    @Test
    void persist_schema_without_resources_removes_the_persisted_schema()
    {
        final NodePath cmsPath = new NodePath( "/applications/myBundle/cms" );
        Mockito.when( this.nodeService.nodeExists( cmsPath ) ).thenReturn( true );

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), Map.of() );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService ).delete( deleteCaptor.capture() );
        assertEquals( cmsPath, deleteCaptor.getValue().getNodePath() );
        Mockito.verify( this.nodeService, Mockito.never() ).create( Mockito.any( CreateNodeParams.class ) );
        Mockito.verify( this.nodeService ).refresh( RefreshMode.ALL );
    }

    @Test
    void create_application_node()
    {
        stubCreate();

        this.service.createApplicationNode( ApplicationKey.from( "myapp" ) );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService ).create( captor.capture() );

        assertEquals( "myapp", captor.getValue().getName().toString() );
        assertEquals( ApplicationRepoServiceImpl.APPLICATION_PATH, captor.getValue().getParent() );
        assertNotNull( captor.getValue().getData().getInstant( "modifiedTime" ) );
        assertNull( captor.getValue().getData().getBinaryReference( ApplicationNodeTransformer.APPLICATION_BINARY_REF ) );
        assertEquals( 0, captor.getValue().getBinaryAttachments().getSize() );
    }

    @Test
    void get_application_descriptor_node()
    {
        final Node node = Node.create().id( new NodeId() ).name( "enonic.yaml" ).parentPath( new NodePath( "/applications/myapp" ) ).build();
        Mockito.when( this.nodeService.getByPath( new NodePath( "/applications/myapp/enonic.yaml" ) ) ).thenReturn( node );

        assertEquals( node, this.service.getApplicationDescriptorNode( ApplicationKey.from( "myapp" ) ) );
        assertNull( this.service.getApplicationDescriptorNode( ApplicationKey.from( "other" ) ) );
    }

    @Test
    void upsert_descriptor_creates_the_node_with_icon()
    {
        stubCreate();

        this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "yaml", ApplicationIconUpdate.replace(
            ByteSource.wrap( new byte[]{1, 2, 3} ), SchemaResourcePaths.PNG_MIME_TYPE ) );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService ).create( captor.capture() );
        Mockito.verify( this.nodeService, Mockito.never() ).update( Mockito.any( UpdateNodeParams.class ) );

        assertEquals( "enonic.yaml", captor.getValue().getName().toString() );
        assertEquals( new NodePath( "/applications/myapp" ), captor.getValue().getParent() );
        assertEquals( "yaml", captor.getValue().getData().getString( SchemaNodePropertyNames.RESOURCE ) );
        assertEquals( SchemaResourcePaths.PNG_MIME_TYPE, captor.getValue().getData().getString( SchemaNodePropertyNames.MIME_TYPE ) );
        assertEquals( SchemaResourceNames.APP_ICON_BINARY_REFERENCE, captor.getValue().getData().getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertNotNull( captor.getValue().getBinaryAttachments().get( SchemaResourceNames.APP_ICON_BINARY_REFERENCE ) );
        Mockito.verify( this.nodeService ).refresh( RefreshMode.ALL );
    }

    @Test
    void upsert_descriptor_creates_the_node_without_icon()
    {
        stubCreate();

        this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "yaml", ApplicationIconUpdate.KEEP );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService ).create( captor.capture() );

        assertEquals( "yaml", captor.getValue().getData().getString( SchemaNodePropertyNames.RESOURCE ) );
        assertNull( captor.getValue().getData().getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertEquals( 0, captor.getValue().getBinaryAttachments().getSize() );
    }

    @Test
    void upsert_descriptor_updates_keeping_the_icon()
    {
        final PropertyTree edited = stubUpdate( descriptorData( "old", SchemaResourcePaths.SVG_MIME_TYPE ) );

        this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "new", ApplicationIconUpdate.KEEP );

        final ArgumentCaptor<UpdateNodeParams> captor = ArgumentCaptor.forClass( UpdateNodeParams.class );
        Mockito.verify( this.nodeService ).update( captor.capture() );
        Mockito.verify( this.nodeService, Mockito.never() ).create( Mockito.any( CreateNodeParams.class ) );

        assertEquals( new NodePath( "/applications/myapp/enonic.yaml" ), captor.getValue().getPath() );
        assertEquals( "new", edited.getString( SchemaNodePropertyNames.RESOURCE ) );
        assertEquals( SchemaResourcePaths.SVG_MIME_TYPE, edited.getString( SchemaNodePropertyNames.MIME_TYPE ) );
        assertEquals( SchemaResourceNames.APP_ICON_BINARY_REFERENCE, edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertEquals( 0, captor.getValue().getBinaryAttachments().getSize() );
    }

    @Test
    void upsert_descriptor_updates_removing_the_icon()
    {
        final PropertyTree edited = stubUpdate( descriptorData( "old", SchemaResourcePaths.SVG_MIME_TYPE ) );

        this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "new", ApplicationIconUpdate.REMOVE );

        final ArgumentCaptor<UpdateNodeParams> captor = ArgumentCaptor.forClass( UpdateNodeParams.class );
        Mockito.verify( this.nodeService ).update( captor.capture() );

        assertEquals( "new", edited.getString( SchemaNodePropertyNames.RESOURCE ) );
        assertNull( edited.getString( SchemaNodePropertyNames.MIME_TYPE ) );
        assertNull( edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertEquals( 0, captor.getValue().getBinaryAttachments().getSize() );
    }

    @Test
    void upsert_descriptor_updates_replacing_the_icon()
    {
        final PropertyTree edited = stubUpdate( descriptorData( "old", null ) );

        this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "new", ApplicationIconUpdate.replace(
            ByteSource.wrap( new byte[]{1, 2, 3} ), SchemaResourcePaths.PNG_MIME_TYPE ) );

        final ArgumentCaptor<UpdateNodeParams> captor = ArgumentCaptor.forClass( UpdateNodeParams.class );
        Mockito.verify( this.nodeService ).update( captor.capture() );

        assertEquals( "new", edited.getString( SchemaNodePropertyNames.RESOURCE ) );
        assertEquals( SchemaResourcePaths.PNG_MIME_TYPE, edited.getString( SchemaNodePropertyNames.MIME_TYPE ) );
        assertEquals( SchemaResourceNames.APP_ICON_BINARY_REFERENCE, edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertNotNull( captor.getValue().getBinaryAttachments().get( SchemaResourceNames.APP_ICON_BINARY_REFERENCE ) );
    }

    private static CreateNodeParams created( final ArgumentCaptor<CreateNodeParams> captor, final String name )
    {
        return captor.getAllValues().stream().filter( params -> name.equals( params.getName().toString() ) ).findFirst().orElseThrow();
    }

    private static PropertyTree descriptorData( final String resource, final String iconMimeType )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.RESOURCE, resource );
        if ( iconMimeType != null )
        {
            data.setString( SchemaNodePropertyNames.MIME_TYPE, iconMimeType );
            data.setBinaryReference( SchemaNodePropertyNames.ICON, SchemaResourceNames.APP_ICON_BINARY_REFERENCE );
        }
        return data;
    }

    /**
     * Stubs the descriptor node as existing and applies the editor of the update to a node holding the given data.
     *
     * @return the data as edited by the update
     */
    private PropertyTree stubUpdate( final PropertyTree data )
    {
        final NodePath descriptorPath = new NodePath( "/applications/myapp/enonic.yaml" );
        Mockito.when( this.nodeService.nodeExists( descriptorPath ) ).thenReturn( true );

        final Node node = Node.create().id( new NodeId() ).name( "enonic.yaml" ).parentPath( descriptorPath.getParentPath() ).data( data ).build();
        final EditableNode editable = new EditableNode( node );
        Mockito.when( this.nodeService.update( Mockito.any( UpdateNodeParams.class ) ) ).thenAnswer( invocation -> {
            invocation.getArgument( 0, UpdateNodeParams.class ).getEditor().edit( editable );
            return node;
        } );
        return editable.data;
    }

    @Test
    void persist_schema_failure_leaves_no_schema()
    {
        final NodePath cmsPath = new NodePath( "/applications/myBundle/cms" );
        final NodePath descriptorPath = new NodePath( "/applications/myBundle/enonic.yaml" );
        // the old schema exists before the call; the freshly created cms and enonic.yaml nodes exist at cleanup time
        Mockito.when( this.nodeService.nodeExists( cmsPath ) ).thenReturn( true );
        Mockito.when( this.nodeService.nodeExists( descriptorPath ) ).thenReturn( true );

        final Node cmsNode = cmsNode();
        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenAnswer( invocation -> {
            final CreateNodeParams params = invocation.getArgument( 0 );
            if ( "mytype.yaml".equals( params.getName().toString() ) )
            {
                throw new RuntimeException( "node layer failure" );
            }
            return cmsNode;
        } );

        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "enonic.yaml", ByteSource.wrap( "app-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/content-types/mytype/mytype.yaml", ByteSource.wrap( "content-type".getBytes( StandardCharsets.UTF_8 ) ) );

        assertThrows( RuntimeException.class,
                      () -> this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources ) );

        // the old schema is removed up front and the half-written new one is removed on failure: no schema is left behind
        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 4 ) ).delete( deleteCaptor.capture() );
        assertEquals( List.of( cmsPath, descriptorPath, cmsPath, descriptorPath ),
                      deleteCaptor.getAllValues().stream().map( DeleteNodeParams::getNodePath ).toList() );
        Mockito.verify( this.nodeService, Mockito.never() ).refresh( Mockito.any() );
    }

    @Test
    void delete_schema_removes_persisted_nodes()
    {
        final NodePath cmsPath = new NodePath( "/applications/myBundle/cms" );
        final NodePath descriptorPath = new NodePath( "/applications/myBundle/enonic.yaml" );
        Mockito.when( this.nodeService.nodeExists( cmsPath ) ).thenReturn( true );
        Mockito.when( this.nodeService.nodeExists( descriptorPath ) ).thenReturn( true );

        this.service.deleteApplicationSchema( ApplicationKey.from( "myBundle" ) );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 2 ) ).delete( deleteCaptor.capture() );
        assertEquals( List.of( cmsPath, descriptorPath ), deleteCaptor.getAllValues().stream().map( DeleteNodeParams::getNodePath ).toList() );
        Mockito.verify( this.nodeService, Mockito.never() ).create( Mockito.any( CreateNodeParams.class ) );
    }

    @Test
    void delete_schema_without_persisted_schema_is_noop()
    {
        this.service.deleteApplicationSchema( ApplicationKey.from( "myBundle" ) );

        Mockito.verify( this.nodeService, Mockito.never() ).delete( Mockito.any( DeleteNodeParams.class ) );
    }

    private Node stubCreate()
    {
        final Node cmsNode = cmsNode();
        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenReturn( cmsNode );
        return cmsNode;
    }

    private static Node cmsNode()
    {
        return Node.create()
            .id( new NodeId() )
            .name( SchemaResourceNames.CMS_ROOT_NAME )
            .parentPath( new NodePath( "/applications/myBundle" ) )
            .build();
    }

    private AppInfo createApp()
    {
        var app =  new AppInfo();
        app.name = "myBundle";
        return app;
    }
}

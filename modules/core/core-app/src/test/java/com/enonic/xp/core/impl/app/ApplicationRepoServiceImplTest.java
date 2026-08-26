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
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationRepoServiceImplTest
{
    // the application icon is attached to the application node under its file name, by mime type
    private static final BinaryReference SVG_ICON = BinaryReference.from( "enonic.svg" );

    private static final BinaryReference PNG_ICON = BinaryReference.from( "enonic.png" );

    private static final NodePath APP_PATH = new NodePath( "/myBundle" );

    private static final NodePath CMS_PATH = new NodePath( "/myBundle/cms" );

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

        Mockito.when( this.nodeService.getByPath( APP_PATH ) ).thenReturn( applicationNode( new PropertyTree() ) );

        this.service.upsertApplicationNode( app, ByteSource.wrap( "myBinary".getBytes() ) );

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).update( Mockito.isA( UpdateNodeParams.class ) );
    }

    @Test
    void delete_node()
    {
        final AppInfo app = createApp();

        Mockito.when( this.nodeService.getByPath( APP_PATH ) ).thenReturn( applicationNode( new PropertyTree() ) );

        this.service.deleteApplicationNode( ApplicationKey.from( app.name ) );

        ArgumentCaptor<DeleteNodeParams> argCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).delete( argCaptor.capture() );
        assertEquals( APP_PATH, argCaptor.getValue().getNodePath() );
    }

    // the descriptor goes on the application node, the schema as one node per descriptor below cms, nothing else on the way
    @Test
    void persist_schema_creates_cms_tree()
    {
        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "enonic.yaml", ByteSource.wrap( "app-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/cms.yaml", ByteSource.wrap( "cms-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/content-types/mytype.yaml", ByteSource.wrap( "content-type".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/i18n/phrases/phrases_en.properties", ByteSource.wrap( "phrases".getBytes( StandardCharsets.UTF_8 ) ) );

        final PropertyTree edited = stubApplicationNode( new PropertyTree() );
        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        Mockito.verify( this.nodeService, Mockito.never() ).delete( Mockito.any( DeleteNodeParams.class ) );

        assertEquals( "app-descriptor", edited.getString( SchemaNodePropertyNames.DESCRIPTOR ) );
        assertNull( edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 4 ) ).create( captor.capture() );

        final List<CreateNodeParams> created = captor.getAllValues();
        assertEquals( List.of( "/myBundle/cms", "/myBundle/cms/cms", "/myBundle/cms/contenttype:mytype", "/myBundle/cms/phrases:phrases_en" ),
                      created.stream().map( params -> new NodePath( params.getParent(), params.getName() ).toString() ).toList() );

        assertEquals( "cms-descriptor", created( captor, "/myBundle/cms/cms" ).getData().getString( SchemaNodePropertyNames.RESOURCE ) );
        assertEquals( "content-type", created( captor, "/myBundle/cms/contenttype:mytype" ).getData().getString( SchemaNodePropertyNames.RESOURCE ) );
        assertEquals( "phrases", created( captor, "/myBundle/cms/phrases:phrases_en" ).getData().getString( SchemaNodePropertyNames.RESOURCE ) );

        Mockito.verify( this.nodeService ).refresh( RefreshMode.ALL );
    }

    @Test
    void persist_schema_attaches_schema_icons_to_their_node()
    {
        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "cms/cms.yaml", ByteSource.wrap( "cms-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/content-types/mytype.svg", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/content-types/mytype.yaml", ByteSource.wrap( "content-type".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/parts/mypart.png", ByteSource.wrap( new byte[]{1, 2, 3} ) );
        resources.put( "cms/parts/mypart.yaml", ByteSource.wrap( "part".getBytes( StandardCharsets.UTF_8 ) ) );

        stubApplicationNode( new PropertyTree() );
        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 4 ) ).create( captor.capture() );

        final CreateNodeParams contentType = created( captor, "/myBundle/cms/contenttype:mytype" );
        assertEquals( "content-type", contentType.getData().getString( SchemaNodePropertyNames.RESOURCE ) );
        assertEquals( BinaryReference.from( "icon.svg" ), contentType.getData().getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertNotNull( contentType.getBinaryAttachments().get( BinaryReference.from( "icon.svg" ) ) );

        final CreateNodeParams part = created( captor, "/myBundle/cms/part:mypart" );
        assertEquals( BinaryReference.from( "icon.png" ), part.getData().getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertNotNull( part.getBinaryAttachments().get( BinaryReference.from( "icon.png" ) ) );
    }

    // an icon without a descriptor is not a schema
    @Test
    void persist_schema_drops_an_icon_without_descriptor()
    {
        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "cms/cms.yaml", ByteSource.wrap( "cms-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/content-types/mytype.svg", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );

        stubApplicationNode( new PropertyTree() );
        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 2 ) ).create( captor.capture() );
        assertEquals( List.of( "cms", "cms" ), captor.getAllValues().stream().map( params -> params.getName().toString() ).toList() );
    }

    @Test
    void persist_schema_attaches_the_application_icon_to_the_application_node()
    {
        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "enonic.yaml", ByteSource.wrap( "app-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "enonic.svg", ByteSource.wrap( "<svg>app</svg>".getBytes( StandardCharsets.UTF_8 ) ) );

        final PropertyTree edited = stubApplicationNode( new PropertyTree() );

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        // the application node is the only node written: no cms marker for an application without a schema
        Mockito.verify( this.nodeService, Mockito.never() ).create( Mockito.any( CreateNodeParams.class ) );

        final ArgumentCaptor<UpdateNodeParams> captor = ArgumentCaptor.forClass( UpdateNodeParams.class );
        Mockito.verify( this.nodeService ).update( captor.capture() );
        assertEquals( APP_PATH, captor.getValue().getPath() );
        assertEquals( "app-descriptor", edited.getString( SchemaNodePropertyNames.DESCRIPTOR ) );
        assertEquals( SVG_ICON, edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertEquals( 1, captor.getValue().getBinaryAttachments().getSize() );
        assertNotNull( captor.getValue().getBinaryAttachments().get( SVG_ICON ) );
    }

    @Test
    void persist_schema_icon_without_descriptor_writes_a_minimal_descriptor()
    {
        final PropertyTree edited = stubApplicationNode( new PropertyTree() );

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ),
                                               Map.of( "enonic.svg", ByteSource.wrap( "<svg>app</svg>".getBytes( StandardCharsets.UTF_8 ) ) ) );

        assertEquals( "kind: \"Application\"\nname: \"myBundle\"\n", edited.getString( SchemaNodePropertyNames.DESCRIPTOR ) );
        assertEquals( SVG_ICON, edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
    }

    @Test
    void persist_schema_rejects_a_resource_that_is_not_persisted()
    {
        stubApplicationNode( new PropertyTree() );

        final Map<String, ByteSource> resources = Map.of( "assets/app.js", ByteSource.wrap( "js".getBytes( StandardCharsets.UTF_8 ) ) );

        assertThrows( IllegalArgumentException.class, () -> this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources ) );
        Mockito.verify( this.nodeService, Mockito.never() ).create( Mockito.any( CreateNodeParams.class ) );
    }

    @Test
    void persist_schema_replaces_existing_schema()
    {
        Mockito.when( this.nodeService.nodeExists( CMS_PATH ) ).thenReturn( true );
        final PropertyTree edited = stubApplicationNode( descriptorData( "old", SchemaResourcePaths.SVG_MIME_TYPE ) );
        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ),
                                               Map.of( "cms/cms.yaml", ByteSource.wrap( "cms".getBytes( StandardCharsets.UTF_8 ) ) ) );

        // the old schema (cms subtree, descriptor and icon on the application node) is removed before the new one is written
        final InOrder inOrder = Mockito.inOrder( this.nodeService );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        inOrder.verify( this.nodeService ).delete( deleteCaptor.capture() );
        assertEquals( CMS_PATH, deleteCaptor.getValue().getNodePath() );
        inOrder.verify( this.nodeService ).update( Mockito.any( UpdateNodeParams.class ) );
        assertNull( edited.getString( SchemaNodePropertyNames.DESCRIPTOR ) );
        assertNull( edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );

        final ArgumentCaptor<CreateNodeParams> createCaptor = ArgumentCaptor.forClass( CreateNodeParams.class );
        inOrder.verify( this.nodeService, Mockito.times( 2 ) ).create( createCaptor.capture() );
        assertEquals( "cms", createCaptor.getAllValues().get( 0 ).getName().toString() );
        assertEquals( APP_PATH, createCaptor.getAllValues().get( 0 ).getParent() );
        assertEquals( "cms", createCaptor.getAllValues().get( 1 ).getName().toString() );
    }

    @Test
    void persist_schema_without_resources_removes_the_persisted_schema()
    {
        Mockito.when( this.nodeService.nodeExists( CMS_PATH ) ).thenReturn( true );
        stubApplicationNode( new PropertyTree() );

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), Map.of() );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService ).delete( deleteCaptor.capture() );
        assertEquals( CMS_PATH, deleteCaptor.getValue().getNodePath() );
        Mockito.verify( this.nodeService, Mockito.never() ).create( Mockito.any( CreateNodeParams.class ) );
        // nothing on the application node to remove
        Mockito.verify( this.nodeService, Mockito.never() ).update( Mockito.any( UpdateNodeParams.class ) );
        Mockito.verify( this.nodeService ).refresh( RefreshMode.ALL );
    }

    @Test
    void persist_schema_failure_leaves_no_schema()
    {
        Mockito.when( this.nodeService.nodeExists( CMS_PATH ) ).thenReturn( true );
        final PropertyTree edited = stubApplicationNode( descriptorData( "old", null ) );

        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenAnswer( invocation -> {
            final CreateNodeParams params = invocation.getArgument( 0 );
            if ( "contenttype:mytype".equals( params.getName().toString() ) )
            {
                throw new RuntimeException( "node layer failure" );
            }
            return cmsNode();
        } );

        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "enonic.yaml", ByteSource.wrap( "app-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/cms.yaml", ByteSource.wrap( "cms".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "cms/content-types/mytype.yaml", ByteSource.wrap( "content-type".getBytes( StandardCharsets.UTF_8 ) ) );

        assertThrows( RuntimeException.class,
                      () -> this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources ) );

        // the old schema is removed up front and the half-written new one is removed on failure: no schema is left behind
        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 2 ) ).delete( deleteCaptor.capture() );
        assertEquals( List.of( CMS_PATH, CMS_PATH ), deleteCaptor.getAllValues().stream().map( DeleteNodeParams::getNodePath ).toList() );
        assertNull( edited.getString( SchemaNodePropertyNames.DESCRIPTOR ) );
        Mockito.verify( this.nodeService, Mockito.never() ).refresh( Mockito.any() );
    }

    @Test
    void delete_schema_removes_persisted_nodes()
    {
        Mockito.when( this.nodeService.nodeExists( CMS_PATH ) ).thenReturn( true );
        final PropertyTree edited = stubApplicationNode( descriptorData( "old", SchemaResourcePaths.SVG_MIME_TYPE ) );

        this.service.deleteApplicationSchema( ApplicationKey.from( "myBundle" ) );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService ).delete( deleteCaptor.capture() );
        assertEquals( CMS_PATH, deleteCaptor.getValue().getNodePath() );
        assertNull( edited.getString( SchemaNodePropertyNames.DESCRIPTOR ) );
        assertNull( edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
        Mockito.verify( this.nodeService, Mockito.never() ).create( Mockito.any( CreateNodeParams.class ) );
    }

    @Test
    void delete_schema_without_persisted_schema_is_noop()
    {
        stubApplicationNode( new PropertyTree() );

        this.service.deleteApplicationSchema( ApplicationKey.from( "myBundle" ) );

        Mockito.verify( this.nodeService, Mockito.never() ).delete( Mockito.any( DeleteNodeParams.class ) );
        Mockito.verify( this.nodeService, Mockito.never() ).update( Mockito.any( UpdateNodeParams.class ) );
    }

    @Test
    void create_application_node()
    {
        stubCreate();

        this.service.createApplicationNode( ApplicationKey.from( "myapp" ) );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService ).create( captor.capture() );

        assertEquals( "myapp", captor.getValue().getName().toString() );
        assertEquals( ApplicationRepoServiceImpl.APPLICATIONS_ROOT, captor.getValue().getParent() );
        assertNotNull( captor.getValue().getData().getInstant( "modifiedTime" ) );
        assertNull( captor.getValue().getData().getBinaryReference( ApplicationNodeTransformer.APPLICATION_BINARY_REF ) );
        assertEquals( 0, captor.getValue().getBinaryAttachments().getSize() );
    }

    @Test
    void has_application_descriptor()
    {
        Mockito.when( this.nodeService.getByPath( new NodePath( "/myapp" ) ) ).thenReturn(
            Node.create().id( new NodeId() ).name( "myapp" ).parentPath( NodePath.ROOT ).data( descriptorData( "yaml", null ) ).build() );
        Mockito.when( this.nodeService.getByPath( new NodePath( "/bare" ) ) ).thenReturn(
            Node.create().id( new NodeId() ).name( "bare" ).parentPath( NodePath.ROOT ).build() );

        assertTrue( this.service.hasApplicationDescriptor( ApplicationKey.from( "myapp" ) ) );
        assertFalse( this.service.hasApplicationDescriptor( ApplicationKey.from( "bare" ) ) );
        assertFalse( this.service.hasApplicationDescriptor( ApplicationKey.from( "other" ) ) );
    }

    @Test
    void has_cms_descriptor()
    {
        Mockito.when( this.nodeService.nodeExists( new NodePath( "/myapp/cms/cms" ) ) ).thenReturn( true );

        assertTrue( this.service.hasCmsDescriptor( ApplicationKey.from( "myapp" ) ) );
        assertFalse( this.service.hasCmsDescriptor( ApplicationKey.from( "other" ) ) );
    }

    @Test
    void upsert_descriptor_requires_the_application_node()
    {
        final IconUpdate icon = IconUpdate.KEEP;
        assertThrows( NodeNotFoundException.class,
                      () -> this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "yaml", icon ) );
        Mockito.verify( this.nodeService, Mockito.never() ).update( Mockito.any( UpdateNodeParams.class ) );
    }

    @Test
    void upsert_descriptor_writes_the_descriptor_with_icon()
    {
        final PropertyTree edited = stubUpdate( new PropertyTree() );

        this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "yaml", IconUpdate.replace(
            ByteSource.wrap( new byte[]{1, 2, 3} ), SchemaResourcePaths.PNG_MIME_TYPE ) );

        final ArgumentCaptor<UpdateNodeParams> captor = ArgumentCaptor.forClass( UpdateNodeParams.class );
        Mockito.verify( this.nodeService ).update( captor.capture() );
        Mockito.verify( this.nodeService, Mockito.never() ).create( Mockito.any( CreateNodeParams.class ) );

        assertEquals( new NodePath( "/myapp" ), captor.getValue().getPath() );
        assertEquals( "yaml", edited.getString( SchemaNodePropertyNames.DESCRIPTOR ) );
        assertEquals( PNG_ICON, edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertNotNull( captor.getValue().getBinaryAttachments().get( PNG_ICON ) );
        assertNull( captor.getValue().getBinaryAttachments().get( SVG_ICON ) );
        Mockito.verify( this.nodeService ).refresh( RefreshMode.ALL );
    }

    @Test
    void upsert_descriptor_updates_keeping_the_icon()
    {
        final PropertyTree edited = stubUpdate( descriptorData( "old", SchemaResourcePaths.SVG_MIME_TYPE ) );

        this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "new", IconUpdate.KEEP );

        final ArgumentCaptor<UpdateNodeParams> captor = ArgumentCaptor.forClass( UpdateNodeParams.class );
        Mockito.verify( this.nodeService ).update( captor.capture() );

        assertEquals( "new", edited.getString( SchemaNodePropertyNames.DESCRIPTOR ) );
        assertEquals( SVG_ICON, edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertEquals( 0, captor.getValue().getBinaryAttachments().getSize() );
    }

    @Test
    void upsert_descriptor_updates_removing_the_icon()
    {
        final PropertyTree edited = stubUpdate( descriptorData( "old", SchemaResourcePaths.SVG_MIME_TYPE ) );

        this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "new", IconUpdate.REMOVE );

        final ArgumentCaptor<UpdateNodeParams> captor = ArgumentCaptor.forClass( UpdateNodeParams.class );
        Mockito.verify( this.nodeService ).update( captor.capture() );

        assertEquals( "new", edited.getString( SchemaNodePropertyNames.DESCRIPTOR ) );
        assertNull( edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertEquals( 0, captor.getValue().getBinaryAttachments().getSize() );
    }

    @Test
    void upsert_descriptor_updates_replacing_an_svg_icon_by_a_png()
    {
        final PropertyTree edited = stubUpdate( descriptorData( "old", SchemaResourcePaths.SVG_MIME_TYPE ) );

        this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "new", IconUpdate.replace(
            ByteSource.wrap( new byte[]{1, 2, 3} ), SchemaResourcePaths.PNG_MIME_TYPE ) );

        final ArgumentCaptor<UpdateNodeParams> captor = ArgumentCaptor.forClass( UpdateNodeParams.class );
        Mockito.verify( this.nodeService ).update( captor.capture() );

        // the reference moves to the new name; the binary under the old name is no longer referenced and dropped by the update
        assertEquals( PNG_ICON, edited.getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertEquals( 1, captor.getValue().getBinaryAttachments().getSize() );
        assertNotNull( captor.getValue().getBinaryAttachments().get( PNG_ICON ) );
    }

    // the mime type names the binary, so an icon of another type is rejected before anything is written
    @Test
    void upsert_rejects_an_unsupported_icon_mime_type()
    {
        stubUpdate( new PropertyTree() );

        final IconUpdate icon = IconUpdate.replace( ByteSource.wrap( new byte[]{1} ), "image/jpeg" );
        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class,
                                                                 () -> this.service.upsertApplicationDescriptor( ApplicationKey.from( "myapp" ), "yaml", icon ) );
        assertEquals( "unsupported icon mime type: image/jpeg", exception.getMessage() );
        Mockito.verify( this.nodeService, Mockito.never() ).update( Mockito.any( UpdateNodeParams.class ) );
    }

    private static CreateNodeParams created( final ArgumentCaptor<CreateNodeParams> captor, final String path )
    {
        return captor.getAllValues()
            .stream()
            .filter( params -> path.equals( new NodePath( params.getParent(), params.getName() ).toString() ) )
            .findFirst()
            .orElseThrow();
    }

    private static PropertyTree descriptorData( final String descriptor, final String iconMimeType )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.DESCRIPTOR, descriptor );
        if ( iconMimeType != null )
        {
            data.setBinaryReference( SchemaNodePropertyNames.ICON, SchemaResourcePaths.appIconBinaryReference( iconMimeType ) );
        }
        return data;
    }

    private static Node applicationNode( final PropertyTree data )
    {
        return Node.create().id( new NodeId() ).name( "myBundle" ).parentPath( NodePath.ROOT ).data( data ).build();
    }

    /**
     * Stubs the application node {@code /myBundle} as existing with the given data, and applies the editor of an update to it.
     *
     * @return the data as edited by the update
     */
    private PropertyTree stubApplicationNode( final PropertyTree data )
    {
        final Node node = applicationNode( data );
        final EditableNode editable = new EditableNode( node );
        Mockito.when( this.nodeService.getByPath( APP_PATH ) ).thenReturn( node );
        Mockito.when( this.nodeService.nodeExists( APP_PATH ) ).thenReturn( true );
        Mockito.when( this.nodeService.update( Mockito.any( UpdateNodeParams.class ) ) ).thenAnswer( invocation -> {
            invocation.getArgument( 0, UpdateNodeParams.class ).getEditor().edit( editable );
            return node;
        } );
        return editable.data;
    }

    /**
     * Stubs the application node {@code /myapp} as existing and applies the editor of the update to a node holding the given data.
     *
     * @return the data as edited by the update
     */
    private PropertyTree stubUpdate( final PropertyTree data )
    {
        final NodePath appPath = new NodePath( "/myapp" );
        Mockito.when( this.nodeService.nodeExists( appPath ) ).thenReturn( true );

        final Node node = Node.create().id( new NodeId() ).name( "myapp" ).parentPath( NodePath.ROOT ).data( data ).build();
        final EditableNode editable = new EditableNode( node );
        Mockito.when( this.nodeService.update( Mockito.any( UpdateNodeParams.class ) ) ).thenAnswer( invocation -> {
            invocation.getArgument( 0, UpdateNodeParams.class ).getEditor().edit( editable );
            return node;
        } );
        return editable.data;
    }

    private Node stubCreate()
    {
        final Node cmsNode = cmsNode();
        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenReturn( cmsNode );
        return cmsNode;
    }

    private static Node cmsNode()
    {
        return Node.create().id( new NodeId() ).name( SchemaResourceNames.CMS_ROOT_NAME ).parentPath( APP_PATH ).build();
    }

    private AppInfo createApp()
    {
        var app = new AppInfo();
        app.name = "myBundle";
        return app;
    }
}

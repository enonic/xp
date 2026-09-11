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
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
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
        resources.put( "cms.yaml", ByteSource.wrap( "cms-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "content-types/mytype/mytype.yaml", ByteSource.wrap( "content-type".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "i18n/phrases/phrases_en.properties", ByteSource.wrap( "phrases".getBytes( StandardCharsets.UTF_8 ) ) );

        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        Mockito.verify( this.nodeService, Mockito.never() ).delete( Mockito.any( DeleteNodeParams.class ) );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 8 ) ).create( captor.capture() );

        final List<CreateNodeParams> created = captor.getAllValues();
        assertEquals( List.of( "/applications/myBundle/cms", "/applications/myBundle/cms/cms.yaml", "/applications/myBundle/cms/content-types",
                               "/applications/myBundle/cms/content-types/mytype", "/applications/myBundle/cms/content-types/mytype/mytype.yaml",
                               "/applications/myBundle/cms/i18n", "/applications/myBundle/cms/i18n/phrases",
                               "/applications/myBundle/cms/i18n/phrases/phrases_en.properties" ),
                      created.stream().map( params -> new NodePath( params.getParent(), params.getName() ).toString() ).toList() );

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
    void persist_schema_stores_icons_as_binaries()
    {
        final Map<String, ByteSource> resources =
            Map.of( "content-types/mytype/mytype.svg", ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );

        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.atLeastOnce() ).create( captor.capture() );

        final CreateNodeParams iconParams = captor.getAllValues()
            .stream()
            .filter( params -> "mytype.svg".equals( params.getName().toString() ) )
            .findFirst()
            .orElseThrow();

        assertEquals( SchemaResourcePaths.SVG_MIME_TYPE, iconParams.getData().getString( SchemaNodePropertyNames.MIME_TYPE ) );
        assertEquals( VirtualAppConstants.ICON_BINARY_REFERENCE,
                      iconParams.getData().getBinaryReference( SchemaNodePropertyNames.ICON ) );
        assertNull( iconParams.getData().getString( SchemaNodePropertyNames.RESOURCE ) );
        assertNotNull( iconParams.getBinaryAttachments().get( VirtualAppConstants.ICON_BINARY_REFERENCE ) );
    }

    @Test
    void persist_schema_replaces_existing_cms()
    {
        final NodePath cmsPath = new NodePath( "/applications/myBundle/cms" );
        Mockito.when( this.nodeService.nodeExists( cmsPath ) ).thenReturn( true );

        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), Map.of() );

        // the old schema is removed before the new one is written
        final InOrder inOrder = Mockito.inOrder( this.nodeService );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        inOrder.verify( this.nodeService ).delete( deleteCaptor.capture() );
        assertEquals( cmsPath, deleteCaptor.getValue().getNodePath() );

        final ArgumentCaptor<CreateNodeParams> createCaptor = ArgumentCaptor.forClass( CreateNodeParams.class );
        inOrder.verify( this.nodeService ).create( createCaptor.capture() );
        assertEquals( VirtualAppConstants.CMS_ROOT_NAME, createCaptor.getValue().getName().toString() );
        assertEquals( new NodePath( "/applications/myBundle" ), createCaptor.getValue().getParent() );
    }

    @Test
    void persist_schema_failure_leaves_no_schema()
    {
        final NodePath cmsPath = new NodePath( "/applications/myBundle/cms" );
        // the old schema exists before the call; the freshly created cms node exists at cleanup time
        Mockito.when( this.nodeService.nodeExists( cmsPath ) ).thenReturn( true );

        final Node cmsNode = cmsNode();
        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenAnswer( invocation -> {
            final CreateNodeParams params = invocation.getArgument( 0 );
            if ( "mytype.yaml".equals( params.getName().toString() ) )
            {
                throw new RuntimeException( "node layer failure" );
            }
            return cmsNode;
        } );

        final Map<String, ByteSource> resources =
            Map.of( "content-types/mytype/mytype.yaml", ByteSource.wrap( "content-type".getBytes( StandardCharsets.UTF_8 ) ) );

        assertThrows( RuntimeException.class,
                      () -> this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources ) );

        // the old schema is removed up front and the half-written new one is removed on failure: no schema is left behind
        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 2 ) ).delete( deleteCaptor.capture() );
        assertEquals( List.of( cmsPath, cmsPath ), deleteCaptor.getAllValues().stream().map( DeleteNodeParams::getNodePath ).toList() );
        Mockito.verify( this.nodeService, Mockito.never() ).refresh( Mockito.any() );
    }

    @Test
    void delete_schema_removes_cms()
    {
        final NodePath cmsPath = new NodePath( "/applications/myBundle/cms" );
        Mockito.when( this.nodeService.nodeExists( cmsPath ) ).thenReturn( true );

        this.service.deleteApplicationSchema( ApplicationKey.from( "myBundle" ) );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService ).delete( deleteCaptor.capture() );
        assertEquals( cmsPath, deleteCaptor.getValue().getNodePath() );
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
            .name( VirtualAppConstants.CMS_ROOT_NAME )
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

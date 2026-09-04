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
import com.enonic.xp.node.MoveNodeParams;
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
    void persist_schema_builds_staging_then_swaps()
    {
        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        resources.put( "cms.yaml", ByteSource.wrap( "cms-descriptor".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "content-types/mytype/mytype.yaml", ByteSource.wrap( "content-type".getBytes( StandardCharsets.UTF_8 ) ) );
        resources.put( "i18n/phrases/phrases_en.properties", ByteSource.wrap( "phrases".getBytes( StandardCharsets.UTF_8 ) ) );

        final Node stagingNode = stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources );

        Mockito.verify( this.nodeService, Mockito.never() ).delete( Mockito.any( DeleteNodeParams.class ) );

        final ArgumentCaptor<CreateNodeParams> captor = ArgumentCaptor.forClass( CreateNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 8 ) ).create( captor.capture() );

        final List<CreateNodeParams> created = captor.getAllValues();
        assertEquals( List.of( "/applications/myBundle/cms_staging", "/applications/myBundle/cms_staging/cms.yaml",
                               "/applications/myBundle/cms_staging/content-types", "/applications/myBundle/cms_staging/content-types/mytype",
                               "/applications/myBundle/cms_staging/content-types/mytype/mytype.yaml",
                               "/applications/myBundle/cms_staging/i18n", "/applications/myBundle/cms_staging/i18n/phrases",
                               "/applications/myBundle/cms_staging/i18n/phrases/phrases_en.properties" ),
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

        final ArgumentCaptor<MoveNodeParams> moveCaptor = ArgumentCaptor.forClass( MoveNodeParams.class );
        Mockito.verify( this.nodeService ).move( moveCaptor.capture() );
        assertEquals( stagingNode.id(), moveCaptor.getValue().getNodeId() );
        assertEquals( VirtualAppConstants.CMS_ROOT_NAME, moveCaptor.getValue().getNewNodeName().toString() );

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

        final Node stagingNode = stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), Map.of() );

        // the new schema is fully built (staging created) before the old one is deleted, then swapped in by rename
        final InOrder inOrder = Mockito.inOrder( this.nodeService );

        final ArgumentCaptor<CreateNodeParams> createCaptor = ArgumentCaptor.forClass( CreateNodeParams.class );
        inOrder.verify( this.nodeService ).create( createCaptor.capture() );
        assertEquals( ApplicationRepoServiceImpl.CMS_STAGING_NAME, createCaptor.getValue().getName().toString() );
        assertEquals( new NodePath( "/applications/myBundle" ), createCaptor.getValue().getParent() );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        inOrder.verify( this.nodeService ).delete( deleteCaptor.capture() );
        assertEquals( cmsPath, deleteCaptor.getValue().getNodePath() );

        final ArgumentCaptor<MoveNodeParams> moveCaptor = ArgumentCaptor.forClass( MoveNodeParams.class );
        inOrder.verify( this.nodeService ).move( moveCaptor.capture() );
        assertEquals( stagingNode.id(), moveCaptor.getValue().getNodeId() );
        assertEquals( VirtualAppConstants.CMS_ROOT_NAME, moveCaptor.getValue().getNewNodeName().toString() );
    }

    @Test
    void persist_schema_failure_keeps_existing_schema()
    {
        final NodePath cmsPath = new NodePath( "/applications/myBundle/cms" );
        final NodePath stagingPath = new NodePath( "/applications/myBundle/" + ApplicationRepoServiceImpl.CMS_STAGING_NAME );
        Mockito.when( this.nodeService.nodeExists( cmsPath ) ).thenReturn( true );

        final Node stagingNode = stagingNode();
        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenAnswer( invocation -> {
            final CreateNodeParams params = invocation.getArgument( 0 );
            if ( "mytype.yaml".equals( params.getName().toString() ) )
            {
                throw new RuntimeException( "node layer failure" );
            }
            return stagingNode;
        } );

        final Map<String, ByteSource> resources =
            Map.of( "content-types/mytype/mytype.yaml", ByteSource.wrap( "content-type".getBytes( StandardCharsets.UTF_8 ) ) );

        assertThrows( RuntimeException.class,
                      () -> this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), resources ) );

        // the previously persisted schema is untouched: only the staging node is cleaned up
        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService ).delete( deleteCaptor.capture() );
        assertEquals( stagingPath, deleteCaptor.getValue().getNodePath() );
        Mockito.verify( this.nodeService, Mockito.never() ).move( Mockito.any( MoveNodeParams.class ) );
    }

    @Test
    void persist_schema_removes_leftover_staging()
    {
        final NodePath stagingPath = new NodePath( "/applications/myBundle/" + ApplicationRepoServiceImpl.CMS_STAGING_NAME );
        Mockito.when( this.nodeService.nodeExists( stagingPath ) ).thenReturn( true );

        stubCreate();

        this.service.persistApplicationSchema( ApplicationKey.from( "myBundle" ), Map.of() );

        final InOrder inOrder = Mockito.inOrder( this.nodeService );

        final ArgumentCaptor<DeleteNodeParams> deleteCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        inOrder.verify( this.nodeService ).delete( deleteCaptor.capture() );
        assertEquals( stagingPath, deleteCaptor.getValue().getNodePath() );

        inOrder.verify( this.nodeService ).create( Mockito.any( CreateNodeParams.class ) );
        inOrder.verify( this.nodeService ).move( Mockito.any( MoveNodeParams.class ) );
    }

    private Node stubCreate()
    {
        final Node stagingNode = stagingNode();
        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenReturn( stagingNode );
        return stagingNode;
    }

    private static Node stagingNode()
    {
        return Node.create()
            .id( new NodeId() )
            .name( ApplicationRepoServiceImpl.CMS_STAGING_NAME )
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

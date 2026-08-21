package com.enonic.xp.core.impl.schema;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.NamespaceNotFoundException;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.resource.ResourceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SchemaServiceImplTest
{
    private NodeService nodeService;

    private ApplicationService applicationService;

    private SchemaServiceImpl service;

    @BeforeEach
    void initService()
    {
        nodeService = mock( NodeService.class );
        applicationService = mock( ApplicationService.class );

        final NamespaceAppService namespaceAppService = new NamespaceAppService( nodeService );

        this.service = new SchemaServiceImpl( nodeService, mock( ResourceService.class ), applicationService, namespaceAppService,
                                              mock( SchemaAuditLogSupport.class ) );
    }

    @Test
    void create_namespace()
    {
        final Node appNode = Node.create().id( NodeId.from( "app-node" ) ).name( "app-node" ).parentPath( NodePath.ROOT ).build();
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenReturn( appNode );

        final Namespace result = NamespaceContext.createAdminContext()
            .callWith( () -> this.service.createNamespace( CreateNamespaceParams.create().key( appKey ).build() ) );

        assertEquals( appKey, result.getKey() );
    }

    @Test
    void create_namespace_without_admin()
    {
        final Node appNode = Node.create().id( NodeId.from( "app-node" ) ).parentPath( NodePath.ROOT ).build();
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenReturn( appNode );

        assertThrows( ForbiddenAccessException.class,
                      () -> this.service.createNamespace( CreateNamespaceParams.create().key( appKey ).build() ) );
    }

    @Test
    void update_namespace()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );
        final NodePath appPath = new NodePath( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( appKey.toString() ) );

        final PropertyTree data = new PropertyTree();
        data.setString( "description", "updated description" );

        when( nodeService.nodeExists( appPath ) ).thenReturn( true );
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn(
            Node.create().id( NodeId.from( "app-node" ) ).name( appKey.toString() ).parentPath( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT )
                .data( data ).build() );

        final Namespace result = NamespaceContext.createAdminContext()
            .callWith( () -> this.service.updateNamespace(
                UpdateNamespaceParams.create().key( appKey ).description( "updated description" ).build() ) );

        assertEquals( appKey, result.getKey() );
        assertEquals( "updated description", result.getDescription() );
    }

    @Test
    void update_namespace_not_found()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        assertThrows( NamespaceNotFoundException.class, () -> NamespaceContext.createAdminContext()
            .callWith( () -> this.service.updateNamespace( UpdateNamespaceParams.create().key( appKey ).build() ) ) );
    }

    @Test
    void update_namespace_without_admin()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        assertThrows( ForbiddenAccessException.class,
                      () -> this.service.updateNamespace( UpdateNamespaceParams.create().key( appKey ).build() ) );
    }

    @Test
    void delete_namespace()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        final DeleteNodeResult result = DeleteNodeResult.create()
            .add( new DeleteNodeResult.Result( NodeId.from( "nodeid" ), NodeVersionId.from( "nodeversionid" ) ) )
            .build();
        when( nodeService.delete( argThat( argument -> new NodePath( "/app1" ).equals( argument.getNodePath() ) ) ) ).thenReturn( result );

        assertTrue( NamespaceContext.createAdminContext().callWith( () -> this.service.deleteNamespace( appKey ) ) );
    }

    @Test
    void delete_namespace_without_admin()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        final DeleteNodeResult result = DeleteNodeResult.create()
            .add( new DeleteNodeResult.Result( NodeId.from( "nodeid" ), NodeVersionId.from( "nodeversionid" ) ) )
            .build();
        when( nodeService.delete( argThat( argument -> new NodePath( "/app1" ).equals( argument.getNodePath() ) ) ) ).thenReturn( result );

        assertThrows( ForbiddenAccessException.class, () -> this.service.deleteNamespace( appKey ) );
    }

    @Test
    void get_namespace()
    {
        final ApplicationKey key = ApplicationKey.from( "app1" );

        final PropertyTree data = new PropertyTree();
        data.setString( "description", "my namespace" );

        final NodePath appPath = new NodePath( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( key.toString() ) );
        when( nodeService.getByPath( appPath ) ).thenReturn(
            Node.create().id( NodeId.from( "app-node" ) ).name( key.toString() ).parentPath( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT )
                .data( data ).build() );

        final Namespace result = this.service.getNamespace( key );

        assertNotNull( result );
        assertEquals( key, result.getKey() );
        assertEquals( "my namespace", result.getDescription() );
    }

    @Test
    void get_namespace_not_found()
    {
        assertNull( this.service.getNamespace( ApplicationKey.from( "app1" ) ) );
    }

    @Test
    void list_application_keys()
    {
        final ApplicationKey installedKey = ApplicationKey.from( "installed.app" );
        final Application installedApp = mock( Application.class );
        when( installedApp.getKey() ).thenReturn( installedKey );
        when( applicationService.getInstalledApplications() ).thenReturn( Applications.from( installedApp ) );

        final NodeIds ids = NodeIds.from( NodeId.from( "namespace-app-id" ) );

        when( nodeService.list( isA( ListNodesParams.class ) ) ).thenReturn( ListNodesResult.create()
                                                                                 .addEntry( new NodeListEntry(
                                                                                     NodeId.from( "namespace-app-id" ),
                                                                                     new NodePath( "/namespace.app" ),
                                                                                     Instant.EPOCH ) )
                                                                                 .build() );

        when( nodeService.getByIds( ids ) ).thenReturn(
            Nodes.from( Node.create().id( new NodeId() ).name( "namespace.app" ).parentPath( NodePath.ROOT ).build() ) );

        final ApplicationKeys result = this.service.listApplicationKeys();

        assertEquals( ApplicationKeys.from( installedKey, ApplicationKey.from( "namespace.app" ) ), result );
    }

    @Test
    void list_namespaces()
    {
        final NodeId namespaceNodeId = NodeId.from( "namespace-app-id" );

        final NodeIds ids = NodeIds.from( namespaceNodeId );

        when( nodeService.list( isA( ListNodesParams.class ) ) ).thenReturn( ListNodesResult.create()
                                                                                 .addEntry( new NodeListEntry( namespaceNodeId,
                                                                                                               new NodePath( "/app1" ),
                                                                                                               Instant.EPOCH ) )
                                                                                 .build() );

        final PropertyTree data = new PropertyTree();
        data.setString( "description", "my namespace" );

        when( nodeService.getByIds( ids ) ).thenReturn(
            Nodes.from( Node.create().id( new NodeId() ).name( "app1" ).parentPath( NodePath.ROOT ).data( data ).build() ) );

        final List<Namespace> result = this.service.listNamespaces();
        assertNotNull( result );
        assertEquals( 1, result.size() );
        assertEquals( "app1", result.get( 0 ).getKey().toString() );
        assertEquals( "my namespace", result.get( 0 ).getDescription() );
    }
}
package com.enonic.xp.core.node;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.node.CreateRootNodeCommand;
import com.enonic.xp.repo.impl.node.FindNodeVersionsCommand;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.security.acl.Permission.CREATE;
import static com.enonic.xp.security.acl.Permission.DELETE;
import static com.enonic.xp.security.acl.Permission.MODIFY;
import static com.enonic.xp.security.acl.Permission.PUBLISH;
import static com.enonic.xp.security.acl.Permission.READ;
import static com.enonic.xp.security.acl.Permission.WRITE_PERMISSIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class ApplyNodePermissionsCommandTest
    extends AbstractNodeTest
{
    private static final IdProviderKey USK = IdProviderKey.system();

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void testApplyPermissionsWithOverwrite()
        throws Exception
    {
        runAs( PrincipalKey.ofAnonymous(), this::applyPermissionsWithOverwrite );
    }

    @Test
    void with_children()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        final Node childNode = createNode( CreateNodeParams.create().name( "my-node1" ).parent( createdNode.path() ).build() );
        final Node grandChildNode = createNode( CreateNodeParams.create().name( "my-node2" ).parent( childNode.path() ).build() );

        pushNodes( WS_OTHER, createdNode.id() );
        pushNodes( WS_OTHER, childNode.id() );

        refresh();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( createdNode.id() )
                                                                                    .overwriteChildPermissions( true )
                                                                                    .addBranches(
                                                                                        Branches.from( ContentConstants.BRANCH_MASTER ) )
                                                                                    .permissions( AccessControlList.create()
                                                                                                      .add( AccessControlEntry.create()
                                                                                                                .allowAll()
                                                                                                                .principal(
                                                                                                                    PrincipalKey.from(
                                                                                                                        "user:my-provider:my-user" ) )
                                                                                                                .build() )
                                                                                                      .build() )
                                                                                    .build() );

        assertEquals( 3, result.getBranchResults().size() );

        assertEquals( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ),
                      result.getResult( createdNode.id(), ContentConstants.BRANCH_MASTER ) );
        assertEquals( result.getResult( childNode.id(), ContentConstants.BRANCH_DRAFT ),
                      result.getResult( childNode.id(), ContentConstants.BRANCH_MASTER ) );

        assertEquals( 1, result.getBranchResults().get( grandChildNode.id() ).size() );
    }

    @Test
    void modified()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        final Node childNode = createNode( CreateNodeParams.create().name( "my-node1" ).parent( createdNode.path() ).build() );

        pushNodes( WS_OTHER, createdNode.id() );
        pushNodes( WS_OTHER, childNode.id() );

        refresh();

        updateNode( UpdateNodeParams.create().id( createdNode.id() ).editor( toBeEdited -> {
            toBeEdited.data.addString( "newField", "fisk" );
        } ).build() );

        refresh();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( createdNode.id() )
                                                                                    .overwriteChildPermissions( true )
                                                                                    .addBranches(
                                                                                        Branches.from( ContentConstants.BRANCH_MASTER ) )
                                                                                    .permissions( AccessControlList.create()
                                                                                                      .add( AccessControlEntry.create()
                                                                                                                .allowAll()
                                                                                                                .principal(
                                                                                                                    PrincipalKey.from(
                                                                                                                        "user:my-provider:my-user" ) )
                                                                                                                .build() )
                                                                                                      .build() )
                                                                                    .build() );

        assertEquals( 2, result.getBranchResults().size() );

        assertNotEquals( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ),
                         result.getResult( createdNode.id(), ContentConstants.BRANCH_MASTER ) );
        assertEquals( result.getResult( childNode.id(), ContentConstants.BRANCH_DRAFT ),
                      result.getResult( childNode.id(), ContentConstants.BRANCH_MASTER ) );
    }

    @Test
    void switched()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        final Node childNode = createNode( CreateNodeParams.create().name( "my-node1" ).parent( createdNode.path() ).build() );

        pushNodes( WS_OTHER, createdNode.id() );
        pushNodes( WS_OTHER, childNode.id() );

        refresh();

        moveNode( childNode.id(), NodePath.ROOT );
        moveNode( createdNode.id(), NodePath.create( NodePath.ROOT, "my-node1" ).build() );

        refresh();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( childNode.id() )
                                                                                    .overwriteChildPermissions( true )
                                                                                    .addBranches(
                                                                                        Branches.from( ContentConstants.BRANCH_MASTER ) )
                                                                                    .permissions( AccessControlList.create()
                                                                                                      .add( AccessControlEntry.create()
                                                                                                                .allowAll()
                                                                                                                .principal(
                                                                                                                    PrincipalKey.from(
                                                                                                                        "user:my-provider:my-user" ) )
                                                                                                                .build() )
                                                                                                      .build() )
                                                                                    .build() );

        assertEquals( 2, result.getBranchResults().size() );

        assertNotEquals( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ),
                         result.getResult( createdNode.id(), ContentConstants.BRANCH_MASTER ) );
        assertNotEquals( result.getResult( childNode.id(), ContentConstants.BRANCH_DRAFT ),
                         result.getResult( childNode.id(), ContentConstants.BRANCH_MASTER ) );
    }

    @Test
    void from_master_to_draft()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        final Node childNode = createNode( CreateNodeParams.create().name( "my-node1" ).parent( createdNode.path() ).build() );

        pushNodes( WS_OTHER, createdNode.id() );
        pushNodes( WS_OTHER, childNode.id() );

        refresh();

        final ApplyNodePermissionsResult result = ContextBuilder.from( ContextAccessor.current() )
            .branch( ContentConstants.BRANCH_MASTER )
            .build()
            .callWith( () -> nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                               .nodeId( createdNode.id() )
                                                               .overwriteChildPermissions( true )
                                                               .addBranches( Branches.from( ContentConstants.BRANCH_DRAFT ) )
                                                               .permissions( AccessControlList.create()
                                                                                 .add( AccessControlEntry.create()
                                                                                           .allowAll()
                                                                                           .principal( PrincipalKey.from(
                                                                                               "user:my-provider:my-user" ) )
                                                                                           .build() )
                                                                                 .build() )
                                                               .build() ) );

        assertEquals( 2, result.getBranchResults().size() );

        assertEquals( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ),
                      result.getResult( createdNode.id(), ContentConstants.BRANCH_MASTER ) );
        assertEquals( result.getResult( childNode.id(), ContentConstants.BRANCH_DRAFT ),
                      result.getResult( childNode.id(), ContentConstants.BRANCH_MASTER ) );
    }

    @Test
    void node_not_exist()
        throws Exception
    {
        assertThrows( NodeNotFoundException.class, () -> nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                           .nodeId( NodeId.from( "id12" ) )
                                                                                           .addBranches( Branches.from(
                                                                                               ContentConstants.BRANCH_MASTER ) )
                                                                                           .permissions( AccessControlList.create()
                                                                                                             .add(
                                                                                                                 AccessControlEntry.create()
                                                                                                                     .allowAll()
                                                                                                                     .principal(
                                                                                                                         PrincipalKey.from(
                                                                                                                             "user:my-provider:my-user" ) )
                                                                                                                     .build() )
                                                                                                             .build() )
                                                                                           .build() ) );
    }

    @Test
    void has_no_write_permissions()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create()
                                                 .name( "my-node" )
                                                 .parent( NodePath.ROOT )
                                                 .permissions( AccessControlList.create()
                                                                   .add( AccessControlEntry.create()
                                                                             .principal( ContextAccessor.current()
                                                                                             .getAuthInfo()
                                                                                             .getUser()
                                                                                             .getKey() )
                                                                             .allowAll()
                                                                             .deny( WRITE_PERMISSIONS )
                                                                             .build() )
                                                                   .build() )
                                                 .build() );

        refresh();

        final ApplyPermissionsListener listener = mock( ApplyPermissionsListener.class );
        final ApplyNodePermissionsResult result = nodeService.applyPermissions(
            ApplyNodePermissionsParams.create()
                .nodeId( createdNode.id() )
                .permissions( AccessControlList.create()

                                  .add( AccessControlEntry.create()
                                            .allowAll()
                                            .principal( PrincipalKey.from( "user:my-provider:my-user" ) )
                                            .build() ).build() )
                .applyPermissionsListener( listener )
                .build() );

        verify( listener, times( 1 ) ).notEnoughRights( 1 );

        assertEquals( 1, result.getBranchResults().size() );
        assertNull( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ) );
    }

    private void applyPermissionsWithOverwrite()
    {
        final PrincipalKey user1 = PrincipalKey.ofUser( USK, "user1" );
        final PrincipalKey user2 = PrincipalKey.ofUser( USK, "user2" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( USK, "group1" );

        final AccessControlList permissions = AccessControlList.of(
            AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ, WRITE_PERMISSIONS ).build(),
            AccessControlEntry.create().principal( user1 ).allow( READ, MODIFY ).build(),
            AccessControlEntry.create().principal( group1 ).allow( READ, CREATE, DELETE, MODIFY ).build() );

        CreateRootNodeCommand.create()
            .params( CreateRootNodeParams.create()
                         .permissions( AccessControlList.create()
                                           .add( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() )
                                           .build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        final Node topNode =
            createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).permissions( permissions ).build() );

        final Node child1_1 = createNode( CreateNodeParams.create().name( "child1_1" ).parent( topNode.path() ).build() );

        final Node child1_2 = createNode( CreateNodeParams.create().name( "child1_2" ).parent( topNode.path() ).build() );

        final AccessControlList child1_1_1Permissions = AccessControlList.of(
            AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ, WRITE_PERMISSIONS ).build(),
            AccessControlEntry.create().principal( user1 ).allow( READ, MODIFY ).build(),
            AccessControlEntry.create().principal( user2 ).allow( READ, CREATE, DELETE, MODIFY, PUBLISH ).build() );
        final Node child1_1_1 = createNode(
            CreateNodeParams.create().name( "child1_1_1" ).parent( child1_1.path() ).permissions( child1_1_1Permissions ).build() );

        final Node child1_2_1 = createNode( CreateNodeParams.create().name( "child1_2_1" ).parent( child1_2.path() ).build() );

        final Node child1_2_2 = createNode( CreateNodeParams.create().name( "child1_2_2" ).parent( child1_2.path() ).build() );

        refresh();

        final ApplyNodePermissionsParams params = ApplyNodePermissionsParams.create()
            .nodeId( topNode.id() )
            .permissions( topNode.getPermissions() )
            .overwriteChildPermissions( true )
            .applyPermissionsListener( mock( ApplyPermissionsListener.class ) )
            .build();

        final ApplyNodePermissionsResult updateNodes = nodeService.applyPermissions( params );

        refresh();

        assertEquals( 6, updateNodes.getSucceedNodes().getSize() );

        final Node topNodeUpdated = getNodeById( topNode.id() );
        assertEquals( permissions, topNodeUpdated.getPermissions() );

        final Node child1_1Updated = getNodeById( child1_1.id() );
        assertEquals( permissions, child1_1Updated.getPermissions() );

        assertVersions( child1_1Updated );
        assertTrue( child1_1.getTimestamp().isBefore( child1_1_1.getTimestamp() ) );

        final Node child1_2Updated = getNodeById( child1_2.id() );
        assertEquals( permissions, child1_2Updated.getPermissions() );

        final Node child1_1_1Updated = getNodeById( child1_1_1.id() );
        assertEquals( permissions, child1_1_1Updated.getPermissions() );

        final Node child1_2_1Updated = getNodeById( child1_2_1.id() );
        assertEquals( permissions, child1_2_1Updated.getPermissions() );

        final Node child1_2_2Updated = getNodeById( child1_2_2.id() );
        assertEquals( permissions, child1_2_2Updated.getPermissions() );
    }

    private void assertVersions( final Node node )
    {
        final NodeVersionQuery query = NodeVersionQuery.create()
            .size( 100 )
            .from( 0 )
            .nodeId( node.id() )
            .addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) )
            .build();

        final NodeVersionQueryResult versions =
            FindNodeVersionsCommand.create().query( query ).searchService( this.searchService ).build().execute();

        assertEquals( 2, versions.getHits() );
        final Iterator<NodeVersionMetadata> iterator = versions.getNodeVersionsMetadata().iterator();
        assertTrue( iterator.next().getTimestamp().isAfter( iterator.next().getTimestamp() ) );
    }

    private void runAs( final PrincipalKey principal, final Runnable runnable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authInfo = context.getAuthInfo();
        ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( authInfo ).principals( principal, PrincipalKey.ofGroup( USK, "group1" ) ).build() )
            .build()
            .runWith( runnable );
    }
}

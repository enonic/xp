package com.enonic.xp.core.node;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.ApplyNodePermissionsListener;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.ApplyPermissionsScope;
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
import com.enonic.xp.repository.CreateBranchParams;
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
import static com.enonic.xp.security.acl.Permission.READ_PERMISSIONS;
import static com.enonic.xp.security.acl.Permission.WRITE_PERMISSIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


class ApplyNodePermissionsCommandTest
    extends AbstractNodeTest
{
    private static final IdProviderKey USK = IdProviderKey.system();

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void testApplyPermissionsWithOverwrite()
    {
        runAs( PrincipalKey.ofAnonymous(), this::applyPermissionsWithOverwrite );
    }

    @Test
    void with_children()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        final Node childNode = createNode( CreateNodeParams.create().name( "my-node1" ).parent( createdNode.path() ).build() );
        final Node grandChildNode = createNode( CreateNodeParams.create().name( "my-node2" ).parent( childNode.path() ).build() );

        pushNodes( WS_OTHER, createdNode.id() );
        pushNodes( WS_OTHER, childNode.id() );

        refresh();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( createdNode.id() )
                                                                                    .scope( ApplyPermissionsScope.TREE )
                                                                                    .addBranches( Branches.from( WS_DEFAULT, WS_OTHER ) )
                                                                                    .removePermissions( AccessControlList.create()
                                                                                                            .add(
                                                                                                                AccessControlEntry.create()
                                                                                                                    .allowAll()
                                                                                                                    .principal(
                                                                                                                        PrincipalKey.from(
                                                                                                                            "user:my-provider:my-user" ) )
                                                                                                                    .build() )
                                                                                                            .build() )
                                                                                    .build() );

        assertEquals( 3, result.getResults().size() );

        assertEquals( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ).nodeVersionId(),
                      result.getResult( createdNode.id(), ContentConstants.BRANCH_MASTER ).nodeVersionId() );
        assertEquals( result.getResult( childNode.id(), ContentConstants.BRANCH_DRAFT ).nodeVersionId(),
                      result.getResult( childNode.id(), ContentConstants.BRANCH_MASTER ).nodeVersionId() );

        assertEquals( 1, result.getResults().get( grandChildNode.id() ).size() );
    }

    @Test
    void only_children()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        final Node childNode = createNode( CreateNodeParams.create().name( "my-node1" ).parent( createdNode.path() ).build() );
        final Node grandChildNode = createNode( CreateNodeParams.create().name( "my-node2" ).parent( childNode.path() ).build() );

        pushNodes( WS_OTHER, createdNode.id() );
        pushNodes( WS_OTHER, childNode.id() );

        refresh();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( createdNode.id() )
                                                                                    .scope( ApplyPermissionsScope.SUBTREE )
                                                                                    .addBranches( Branches.from( WS_DEFAULT, WS_OTHER ) )
                                                                                    .permissions( AccessControlList.create()
                                                                                                      .add( AccessControlEntry.create()
                                                                                                                .allowAll()
                                                                                                                .principal(
                                                                                                                    PrincipalKey.from(
                                                                                                                        "user:my-provider:my-wwuser" ) )
                                                                                                                .build() )
                                                                                                      .build() )
                                                                                    .build() );

        assertEquals( 2, result.getResults().size() );

        assertEquals( result.getResult( childNode.id(), ContentConstants.BRANCH_DRAFT ).nodeVersionId(),
                      result.getResult( childNode.id(), ContentConstants.BRANCH_MASTER ).nodeVersionId() );

        assertEquals( 1, result.getResults().get( grandChildNode.id() ).size() );
    }

    @Test
    void modified()
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
                                                                                    .scope( ApplyPermissionsScope.TREE )
                                                                                    .addBranches( Branches.from( WS_DEFAULT, WS_OTHER ) )
                                                                                    .addPermissions( AccessControlList.create()
                                                                                                         .add( AccessControlEntry.create()
                                                                                                                   .allowAll()
                                                                                                                   .principal(
                                                                                                                       PrincipalKey.from(
                                                                                                                           "user:my-provider:my-user" ) )
                                                                                                                   .build() )
                                                                                                         .build() )
                                                                                    .build() );

        assertEquals( 2, result.getResults().size() );

        assertNotEquals( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ).nodeVersionId(),
                         result.getResult( createdNode.id(), ContentConstants.BRANCH_MASTER ).nodeVersionId() );
        assertEquals( result.getResult( childNode.id(), ContentConstants.BRANCH_DRAFT ).nodeVersionId(),
                      result.getResult( childNode.id(), ContentConstants.BRANCH_MASTER ).nodeVersionId() );
    }

    @Test
    void switched()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        final Node childNode = createNode( CreateNodeParams.create().name( "my-node1" ).parent( createdNode.path() ).build() );

        pushNodes( WS_OTHER, createdNode.id() );
        pushNodes( WS_OTHER, childNode.id() );

        refresh();

        moveNode( childNode.id(), NodePath.ROOT );
        moveNode( createdNode.id(), NodePath.create( NodePath.ROOT ).addElement( "my-node1" ).build() );

        refresh();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( childNode.id() )
                                                                                    .scope( ApplyPermissionsScope.TREE )
                                                                                    .addBranches( Branches.from( WS_DEFAULT, WS_OTHER ) )
                                                                                    .addPermissions( AccessControlList.create()
                                                                                                         .add( AccessControlEntry.create()
                                                                                                                   .allowAll()
                                                                                                                   .principal(
                                                                                                                       PrincipalKey.from(
                                                                                                                           "user:my-provider:my-user" ) )
                                                                                                                   .build() )
                                                                                                         .build() )
                                                                                    .build() );

        assertEquals( 2, result.getResults().size() );

        assertNotEquals( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ).nodeVersionId(),
                         result.getResult( createdNode.id(), ContentConstants.BRANCH_MASTER ).nodeVersionId() );
        assertNotEquals( result.getResult( childNode.id(), ContentConstants.BRANCH_DRAFT ).nodeVersionId(),
                         result.getResult( childNode.id(), ContentConstants.BRANCH_MASTER ).nodeVersionId() );
    }

    @Test
    void from_master_to_draft()
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
                                                               .scope( ApplyPermissionsScope.TREE )
                                                               .addBranches( Branches.from( WS_OTHER, WS_DEFAULT ) )
                                                               .addPermissions( AccessControlList.create()
                                                                                    .add( AccessControlEntry.create()
                                                                                              .allowAll()
                                                                                              .principal( PrincipalKey.from(
                                                                                                  "user:my-provider:my-user" ) )
                                                                                              .build() )
                                                                                    .build() )
                                                               .build() ) );

        assertEquals( 2, result.getResults().size() );

        assertEquals( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ).nodeVersionId(),
                      result.getResult( createdNode.id(), ContentConstants.BRANCH_MASTER ).nodeVersionId() );
        assertEquals( result.getResult( childNode.id(), ContentConstants.BRANCH_DRAFT ).nodeVersionId(),
                      result.getResult( childNode.id(), ContentConstants.BRANCH_MASTER ).nodeVersionId() );
    }

    @Test
    void node_not_exist()
    {
        assertThrows( NodeNotFoundException.class, () -> nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                           .nodeId( NodeId.from( "id12" ) )
                                                                                           .addBranches(
                                                                                               Branches.from( WS_DEFAULT, WS_OTHER ) )
                                                                                           .addPermissions( AccessControlList.create()
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

        final ApplyNodePermissionsListener listener = mock( ApplyNodePermissionsListener.class );
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

        assertEquals( 1, result.getResults().size() );
        assertNull( result.getResult( createdNode.id(), ContentConstants.BRANCH_DRAFT ).permissions() );
    }

    @Test
    void add_and_remove()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        refresh();

        final PrincipalKey principal = PrincipalKey.from( "user:my-provider:my-user" );

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( createdNode.id() )
                                                                                    .scope( ApplyPermissionsScope.TREE )
                                                                                    .addBranches( Branches.from( WS_DEFAULT, WS_OTHER ) )
                                                                                    .addPermissions( AccessControlList.create()
                                                                                                         .add( AccessControlEntry.create()
                                                                                                                   .allowAll()
                                                                                                                   .principal( principal )
                                                                                                                   .build() )
                                                                                                         .build() )
                                                                                    .removePermissions( AccessControlList.create()
                                                                                                            .add(
                                                                                                                AccessControlEntry.create()
                                                                                                                    .allow( READ )
                                                                                                                    .principal( principal )
                                                                                                                    .build() )
                                                                                                            .build() )
                                                                                    .build() );

        assertEquals( 1, result.getResults().size() );
        assertFalse( result.getResults().get( createdNode.id() ).get( 0 ).permissions().isAllowedFor( principal, READ ) );
        assertTrue( result.getResults()
                        .get( createdNode.id() )
                        .get( 0 ).permissions()
                        .isAllowedFor( principal, MODIFY, DELETE, CREATE, PUBLISH, WRITE_PERMISSIONS, READ_PERMISSIONS ) );

    }

    @Test
    void add_to_existing()
    {
        final PrincipalKey principal = PrincipalKey.from( "user:my-provider:my-user" );

        final Node createdNode = createNode( CreateNodeParams.create()
                                                 .name( "my-node" )
                                                 .parent( NodePath.ROOT )
                                                 .permissions( AccessControlList.create()
                                                                   .add( AccessControlEntry.create()
                                                                             .principal( principal )
                                                                             .allow( READ )
                                                                             .build() )
                                                                   .add( AccessControlEntry.create()
                                                                             .principal( TEST_DEFAULT_USER.getKey() )
                                                                             .allowAll()
                                                                             .build() )
                                                                   .build() )
                                                 .build() );

        refresh();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( createdNode.id() )
                                                                                    .scope( ApplyPermissionsScope.TREE )
                                                                                    .addBranches( Branches.from( WS_DEFAULT, WS_OTHER ) )
                                                                                    .addPermissions( AccessControlList.create()
                                                                                                         .add( AccessControlEntry.create()
                                                                                                                   .allow( MODIFY )
                                                                                                                   .principal( principal )
                                                                                                                   .build() )
                                                                                                         .build() )
                                                                                    .build() );

        assertEquals( 1, result.getResults().size() );
        assertTrue( result.getResults().get( createdNode.id() ).get( 0 ).permissions().isAllowedFor( principal, READ, MODIFY ) );
        assertFalse( result.getResults()
                         .get( createdNode.id() )
                         .get( 0 ).permissions()
                         .isAllowedFor( principal, DELETE, CREATE, PUBLISH, WRITE_PERMISSIONS, READ_PERMISSIONS ) );

    }

    @Test
    void remove_all_on_empty()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        refresh();

        final PrincipalKey principal = PrincipalKey.from( "user:my-provider:my-user" );

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( createdNode.id() )
                                                                                    .scope( ApplyPermissionsScope.TREE )
                                                                                    .addBranches( Branches.from(
                                                                                        ContextAccessor.current().getBranch() ) )
                                                                                    .addPermissions( AccessControlList.create()
                                                                                                         .add( AccessControlEntry.create()
                                                                                                                   .allowAll()
                                                                                                                   .principal( principal )
                                                                                                                   .build() )
                                                                                                         .build() )
                                                                                    .removePermissions( AccessControlList.create()
                                                                                                            .add(
                                                                                                                AccessControlEntry.create()
                                                                                                                    .principal( principal )
                                                                                                                    .build() )
                                                                                                            .build() )
                                                                                    .build() );

        assertEquals( 1, result.getResults().size() );
        assertFalse( result.getResults().get( createdNode.id() ).get( 0 ).permissions().contains( principal ) );

    }

    @Test
    void set_empty_permissions()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        refresh();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                    .nodeId( createdNode.id() )
                                                                                    .scope( ApplyPermissionsScope.TREE )
                                                                                    .addBranches( Branches.from(
                                                                                        ContextAccessor.current().getBranch() ) )
                                                                                    .permissions( AccessControlList.empty() )
                                                                                    .build() );

        assertEquals( 1, result.getResults().size() );
        assertTrue( result.getResults().get( createdNode.id() ).get( 0 ).permissions().isEmpty() );
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
            .scope( ApplyPermissionsScope.TREE )
            .applyPermissionsListener( mock( ApplyNodePermissionsListener.class ) )
            .build();

        final ApplyNodePermissionsResult updateNodes = nodeService.applyPermissions( params );

        refresh();

        assertEquals( 6, updateNodes.getResults().size() );

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

        assertEquals( 2, versions.getNodeVersionMetadatas().getSize() );
        final Iterator<NodeVersionMetadata> iterator = versions.getNodeVersionMetadatas().iterator();
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

    @Test
    void apply_permissions_three_branches_with_shared_versions()
    {
        // Create a third branch
        final Branch thirdBranch = Branch.from( "third-branch" );
        ctxDefaultAdmin().callWith( () -> {
            repositoryService.createBranch( CreateBranchParams.from( thirdBranch.getValue() ) );
            return null;
        } );

        // Create node in draft
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        // Push to master and third branch - all three have the same NodeVersionId
        pushNodes( WS_OTHER, createdNode.id() );
        pushNodes( thirdBranch, createdNode.id() );

        refresh();

        final PrincipalKey newPrincipal = PrincipalKey.from( "user:my-provider:new-user" );

        // Apply permissions from draft context to all three branches
        // Since all branches share the same NodeVersionId, the permission change should be created once and reused
        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                     .nodeId( createdNode.id() )
                                                                                     .addBranches( Branches.from( WS_DEFAULT, WS_OTHER, thirdBranch ) )
                                                                                     .addPermissions( AccessControlList.create()
                                                                                                          .add( AccessControlEntry.create()
                                                                                                                    .allow( READ, MODIFY )
                                                                                                                    .principal( newPrincipal )
                                                                                                                    .build() )
                                                                                                          .build() )
                                                                                     .build() );

        assertEquals( 1, result.getResults().size() );

        // All branches should have the same result (same NodeVersionId after applying permissions)
        assertEquals( result.getResult( createdNode.id(), WS_DEFAULT ).nodeVersionId(),
                      result.getResult( createdNode.id(), WS_OTHER ).nodeVersionId() );
        assertEquals( result.getResult( createdNode.id(), WS_DEFAULT ).nodeVersionId(),
                      result.getResult( createdNode.id(), thirdBranch ).nodeVersionId() );

        // All should have the new permission
        assertTrue( result.getResult( createdNode.id(), WS_DEFAULT ).permissions().isAllowedFor( newPrincipal, READ, MODIFY ) );
    }

    @Test
    void apply_permissions_three_branches_cached_version_from_non_first_branch()
    {
        // This test covers the bug where cached applied versions used branches.first() as origin
        // instead of the actual branch where the version was created

        // Create third and fourth branches
        final Branch thirdBranch = Branch.from( "third-branch-2" );
        final Branch fourthBranch = Branch.from( "fourth-branch" );
        ctxDefaultAdmin().callWith( () -> {
            repositoryService.createBranch( CreateBranchParams.from( thirdBranch.getValue() ) );
            repositoryService.createBranch( CreateBranchParams.from( fourthBranch.getValue() ) );
            return null;
        } );

        // Create node in draft
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        // Push to master - draft and master have version A
        pushNodes( WS_OTHER, createdNode.id() );

        // Update in draft (add some data to create a new version)
        updateNode( UpdateNodeParams.create().id( createdNode.id() ).editor( editableNode -> {
            editableNode.data.addString( "version", "B" );
        } ).build() );

        // Push to third and fourth - third and fourth have version B (same as draft)
        pushNodes( thirdBranch, createdNode.id() );
        pushNodes( fourthBranch, createdNode.id() );

        refresh();

        // Now: master has version A, draft/third/fourth have version B
        // Apply permissions with order: [master, draft, third, fourth]
        // - master (version A) gets new permissions first, creates new version, cached with origin=master
        // - draft (version B) gets new permissions, creates new version, cached with origin=draft
        // - third (version B) finds cached version from draft, should push with origin=draft (not master!)
        // - fourth (version B) same as third

        final PrincipalKey newPrincipal = PrincipalKey.from( "user:my-provider:multi-branch-user" );

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                     .nodeId( createdNode.id() )
                                                                                     .addBranches( Branches.from( WS_OTHER, WS_DEFAULT, thirdBranch, fourthBranch ) )
                                                                                     .addPermissions( AccessControlList.create()
                                                                                                          .add( AccessControlEntry.create()
                                                                                                                    .allow( READ )
                                                                                                                    .principal( newPrincipal )
                                                                                                                    .build() )
                                                                                                          .build() )
                                                                                     .build() );

        assertEquals( 1, result.getResults().size() );

        // master had version A - different result
        assertNotEquals( result.getResult( createdNode.id(), WS_OTHER ).nodeVersionId(),
                         result.getResult( createdNode.id(), WS_DEFAULT ).nodeVersionId() );

        // draft, third, fourth all had version B - same result
        assertEquals( result.getResult( createdNode.id(), WS_DEFAULT ).nodeVersionId(),
                      result.getResult( createdNode.id(), thirdBranch ).nodeVersionId() );
        assertEquals( result.getResult( createdNode.id(), WS_DEFAULT ).nodeVersionId(),
                      result.getResult( createdNode.id(), fourthBranch ).nodeVersionId() );

        // All should have the new permission
        assertTrue( result.getResult( createdNode.id(), WS_OTHER ).permissions().isAllowedFor( newPrincipal, READ ) );
        assertTrue( result.getResult( createdNode.id(), WS_DEFAULT ).permissions().isAllowedFor( newPrincipal, READ ) );
        assertTrue( result.getResult( createdNode.id(), thirdBranch ).permissions().isAllowedFor( newPrincipal, READ ) );
        assertTrue( result.getResult( createdNode.id(), fourthBranch ).permissions().isAllowedFor( newPrincipal, READ ) );
    }
}

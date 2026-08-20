package com.enonic.xp.perftest.content;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.DuplicateNodeResult;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

/**
 * Whole-tree node operations on the simplest big shape: one parent with {@value #CHILDREN} direct children, one branch.
 * <p>
 * Every operation is a single shot over the whole tree, so each benchmark is one full walk. The index is settled - untimed - before
 * every invocation, so a measurement covers the operation alone, never the ground the previous one left behind.
 */
@BenchmarkMode( Mode.SingleShotTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Warmup( iterations = 1 )
@Measurement( iterations = 5 )
@Fork( 1 )
public class NodeTreeOperationsBenchmark
{
    private static final int CHILDREN = Integer.getInteger( "ptest.tree.children", 10_000 );

    /** Builds one parent with {@link #CHILDREN} children under the content root, refresh disabled for speed, settled at the end. */
    private static Node buildTree( final Bootstrap bs, final String name )
    {
        bs.setRefreshInterval( "-1" );
        final Node tree = bs.callInDraftContext( () -> {
            final Node parent = bs.nodeService.create(
                CreateNodeParams.create().data( new PropertyTree() ).name( name ).parent( ContentConstants.CONTENT_ROOT_PATH ).build() );
            for ( int i = 0; i < CHILDREN; i++ )
            {
                bs.nodeService.create(
                    CreateNodeParams.create().data( new PropertyTree() ).name( "child-" + i ).parent( parent.path() ).build() );
            }
            return parent;
        } );
        bs.refresh();
        bs.setRefreshInterval( "1s" );
        bs.setStoreThrottleType( "merge" );
        return tree;
    }

    @State( Scope.Benchmark )
    public static class MoveState
    {
        Bootstrap bs;

        NodeId treeId;

        NodePath targetA;

        NodePath targetB;

        boolean atA = true;

        @Setup( Level.Trial )
        public void setUp()
            throws Exception
        {
            bs = new Bootstrap();
            bs.start();
            targetA = bs.callInDraftContext( () -> bs.nodeService.create(
                CreateNodeParams.create().data( new PropertyTree() ).name( "target-a" ).parent( ContentConstants.CONTENT_ROOT_PATH ).build() ) ).path();
            targetB = bs.callInDraftContext( () -> bs.nodeService.create(
                CreateNodeParams.create().data( new PropertyTree() ).name( "target-b" ).parent( ContentConstants.CONTENT_ROOT_PATH ).build() ) ).path();
            final Node tree = buildTree( bs, "tree" );
            treeId = tree.id();
            bs.callInDraftContext( () -> bs.nodeService.move(
                MoveNodeParams.create().nodeId( treeId ).newParentPath( targetA ).build() ) );
            bs.refresh();
        }

        @Setup( Level.Invocation )
        public void settle()
        {
            bs.refresh();
        }

        NodePath nextTarget()
        {
            atA = !atA;
            return atA ? targetA : targetB;
        }

        @TearDown( Level.Trial )
        public void tearDown()
        {
            bs.stop();
        }
    }

    /** One move of the tree parent, back and forth between two targets - every child follows. */
    @Benchmark
    public MoveNodeResult move( final MoveState s )
    {
        final NodePath target = s.nextTarget();
        return s.bs.callInDraftContext(
            () -> s.bs.nodeService.move( MoveNodeParams.create().nodeId( s.treeId ).newParentPath( target ).build() ) );
    }

    @State( Scope.Benchmark )
    public static class DuplicateState
    {
        Bootstrap bs;

        NodeId treeId;

        @Setup( Level.Trial )
        public void setUp()
            throws Exception
        {
            bs = new Bootstrap();
            bs.start();
            treeId = buildTree( bs, "tree" ).id();
        }

        @Setup( Level.Invocation )
        public void settle()
        {
            bs.refresh();
        }

        @TearDown( Level.Trial )
        public void tearDown()
        {
            bs.stop();
        }
    }

    /** One duplication of the whole tree - every invocation leaves a new copy behind, so the corpus grows as it would in production. */
    @Benchmark
    @Measurement( iterations = 3 )
    public DuplicateNodeResult duplicate( final DuplicateState s )
    {
        return s.bs.callInDraftContext(
            () -> s.bs.nodeService.duplicate( DuplicateNodeParams.create().nodeId( s.treeId ).includeChildren( true ).build() ) );
    }

    @State( Scope.Benchmark )
    public static class ApplyPermissionsState
    {
        Bootstrap bs;

        NodeId treeId;

        AccessControlList aclA;

        AccessControlList aclB;

        boolean atA;

        @Setup( Level.Trial )
        public void setUp()
            throws Exception
        {
            bs = new Bootstrap();
            bs.start();
            treeId = buildTree( bs, "tree" ).id();
            // the operating role keeps full access in both variants, so every invocation stays permitted
            aclA = AccessControlList.of(
                AccessControlEntry.create().principal( RoleKeys.CONTENT_MANAGER_ADMIN ).allowAll().build(),
                AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allow( Permission.READ ).build() );
            aclB = AccessControlList.of(
                AccessControlEntry.create().principal( RoleKeys.CONTENT_MANAGER_ADMIN ).allowAll().build(),
                AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allow( Permission.READ, Permission.MODIFY ).build() );
        }

        @Setup( Level.Invocation )
        public void settle()
        {
            bs.refresh();
        }

        AccessControlList nextPermissions()
        {
            atA = !atA;
            return atA ? aclA : aclB;
        }

        @TearDown( Level.Trial )
        public void tearDown()
        {
            bs.stop();
        }
    }

    /** One permission change over the whole tree, alternating between two lists so every invocation writes a real change. */
    @Benchmark
    public ApplyNodePermissionsResult applyPermissions( final ApplyPermissionsState s )
    {
        final AccessControlList permissions = s.nextPermissions();
        return s.bs.callInDraftContext( () -> s.bs.nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                     .nodeId( s.treeId )
                                                                                     .permissions( permissions )
                                                                                     .scope( ApplyPermissionsScope.TREE )
                                                                                     .build() ) );
    }

    @State( Scope.Benchmark )
    public static class DeleteState
    {
        Bootstrap bs;

        NodeId treeId;

        int rebuild;

        @Setup( Level.Trial )
        public void setUp()
            throws Exception
        {
            bs = new Bootstrap();
            bs.start();
        }

        /** A deletion consumes its tree, so every invocation gets a fresh one - built and settled outside the measurement. */
        @Setup( Level.Invocation )
        public void rebuildTree()
        {
            treeId = buildTree( bs, "tree-" + rebuild++ ).id();
        }

        @TearDown( Level.Trial )
        public void tearDown()
        {
            bs.stop();
        }
    }

    /** One deletion of the whole tree. */
    @Benchmark
    @Measurement( iterations = 3 )
    public DeleteNodeResult delete( final DeleteState s )
    {
        return s.bs.callInDraftContext( () -> s.bs.nodeService.delete( DeleteNodeParams.create().nodeId( s.treeId ).build() ) );
    }
}

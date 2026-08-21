package com.enonic.xp.perftest.content;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openjdk.jmh.annotations.AuxCounters;
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

@BenchmarkMode( Mode.SingleShotTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Warmup( iterations = 1 )
@Measurement( iterations = 5 )
@Fork( 1 )
public class NodeTreeOperationsBenchmark
{
    private static final int CHILDREN = Integer.getInteger( "ptest.tree.children", 10_000 );

    private static final int TREE_NODES = CHILDREN + 1;

    private static final com.sun.management.ThreadMXBean THREADS =
        (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();

    @State( Scope.Thread )
    @AuxCounters( AuxCounters.Type.EVENTS )
    public static class Measured
    {
        public long ops;

        public long nodes;

        public long allocKiB;

        public long peakLiveKiB;

        @Setup( Level.Iteration )
        public void reset()
        {
            ops = 0;
            nodes = 0;
            allocKiB = 0;
            peakLiveKiB = 0;
        }

        private static long liveSetBytes()
        {
            long live = 0;
            for ( final MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans() )
            {
                if ( pool.getType() == MemoryType.HEAP )
                {
                    final MemoryUsage afterCollection = pool.getCollectionUsage();
                    if ( afterCollection != null )
                    {
                        live += afterCollection.getUsed();
                    }
                }
            }
            return live;
        }

        <T> T measure( final int nodeCount, final Callable<T> operation )
            throws Exception
        {
            ops++;
            nodes += nodeCount;

            final long liveBefore = liveSetBytes();
            final AtomicLong peakLive = new AtomicLong( liveBefore );
            final Thread watcher = new Thread( () -> {
                while ( !Thread.currentThread().isInterrupted() )
                {
                    peakLive.accumulateAndGet( liveSetBytes(), Math::max );
                    try
                    {
                        TimeUnit.MILLISECONDS.sleep( 20 );
                    }
                    catch ( InterruptedException e )
                    {
                        return;
                    }
                }
            }, "live-set-watcher" );
            watcher.setDaemon( true );

            final long allocBefore = THREADS.getCurrentThreadAllocatedBytes();
            watcher.start();
            try
            {
                return operation.call();
            }
            finally
            {
                allocKiB += ( THREADS.getCurrentThreadAllocatedBytes() - allocBefore ) / 1024;
                watcher.interrupt();
                watcher.join();
                peakLiveKiB += Math.max( 0, peakLive.get() - liveBefore ) / 1024;
            }
        }
    }

    private static void settle( final Bootstrap bs )
    {
        bs.refresh();
        System.gc();
    }

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
        public void settleForInvocation()
        {
            settle( bs );
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

    @Benchmark
    public MoveNodeResult move( final MoveState s, final Measured measured )
        throws Exception
    {
        final NodePath target = s.nextTarget();
        return measured.measure( TREE_NODES, () -> s.bs.callInDraftContext(
            () -> s.bs.nodeService.move( MoveNodeParams.create().nodeId( s.treeId ).newParentPath( target ).build() ) ) );
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
        public void settleForInvocation()
        {
            settle( bs );
        }

        @TearDown( Level.Trial )
        public void tearDown()
        {
            bs.stop();
        }
    }

    @Benchmark
    @Measurement( iterations = 3 )
    public DuplicateNodeResult duplicate( final DuplicateState s, final Measured measured )
        throws Exception
    {
        return measured.measure( TREE_NODES, () -> s.bs.callInDraftContext(
            () -> s.bs.nodeService.duplicate( DuplicateNodeParams.create().nodeId( s.treeId ).includeChildren( true ).build() ) ) );
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
            aclA = AccessControlList.of(
                AccessControlEntry.create().principal( RoleKeys.CONTENT_MANAGER_ADMIN ).allowAll().build(),
                AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allow( Permission.READ ).build() );
            aclB = AccessControlList.of(
                AccessControlEntry.create().principal( RoleKeys.CONTENT_MANAGER_ADMIN ).allowAll().build(),
                AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allow( Permission.READ, Permission.MODIFY ).build() );
        }

        @Setup( Level.Invocation )
        public void settleForInvocation()
        {
            settle( bs );
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

    @Benchmark
    public ApplyNodePermissionsResult applyPermissions( final ApplyPermissionsState s, final Measured measured )
        throws Exception
    {
        final AccessControlList permissions = s.nextPermissions();
        return measured.measure( TREE_NODES, () -> s.bs.callInDraftContext( () -> s.bs.nodeService.applyPermissions(
            ApplyNodePermissionsParams.create()
                .nodeId( s.treeId )
                .permissions( permissions )
                .scope( ApplyPermissionsScope.TREE )
                .build() ) ) );
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

        @Setup( Level.Invocation )
        public void rebuildTree()
        {
            treeId = buildTree( bs, "tree-" + rebuild++ ).id();
            settle( bs );
        }

        @TearDown( Level.Trial )
        public void tearDown()
        {
            bs.stop();
        }
    }

    @Benchmark
    @Measurement( iterations = 3 )
    public DeleteNodeResult delete( final DeleteState s, final Measured measured )
        throws Exception
    {
        return measured.measure( TREE_NODES,
            () -> s.bs.callInDraftContext( () -> s.bs.nodeService.delete( DeleteNodeParams.create().nodeId( s.treeId ).build() ) ) );
    }
}

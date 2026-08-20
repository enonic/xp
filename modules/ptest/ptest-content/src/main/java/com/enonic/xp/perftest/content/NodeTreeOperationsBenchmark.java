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

    /** What one whole-tree operation covers: the parent and every child. */
    private static final int TREE_NODES = CHILDREN + 1;

    private static final com.sun.management.ThreadMXBean THREADS =
        (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();

    /**
     * What one operation costs, reported next to the time: the nodes it covers, what it allocates, and how far the live set grows while
     * it runs.
     * <p>
     * The two memory figures answer different questions. Allocation is churn - every node this operation rewrites is read, rebuilt and
     * re-indexed, and that traffic is the same however the walk is organized. The live set is what the operation *holds*: a walk that
     * keeps the whole subtree in memory at once shows here, a walk that keeps a stride at a time does not.
     * <p>
     * Both are measured close to the operation rather than through a GC profiler: the embedded search server allocates on its own
     * threads throughout, and the corpus a benchmark rebuilds between invocations allocates too, both of which drown out the operation
     * in a whole-process figure.
     */
    @State( Scope.Thread )
    @AuxCounters( AuxCounters.Type.EVENTS )
    public static class Measured
    {
        /**
         * The operations counted, so the report can divide by it. JMH sums an event counter over the iterations of a run rather than
         * averaging it, so every counter here is a total and only a ratio of two of them means anything.
         */
        public long ops;

        /** The nodes the operations covered, so every figure can be read per node as well as per operation. */
        public long nodes;

        /** What the operation allocates on the thread that runs it. */
        public long allocKiB;

        /** How far the live set grows above where it stood when the operation started. */
        public long peakLiveKiB;

        @Setup( Level.Iteration )
        public void reset()
        {
            ops = 0;
            nodes = 0;
            allocKiB = 0;
            peakLiveKiB = 0;
        }

        /**
         * The heap that survived the most recent collection. Reading it repeatedly while an operation runs, rather than once at the end,
         * is what makes a peak out of it - the operation's own garbage is collected as it goes.
         */
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

    /**
     * Readies the repository for one measured invocation: the index settles, and the heap is collected so that the live set the
     * operation is measured against holds the corpus alone rather than whatever the previous invocation left uncollected. Both are
     * untimed - single-shot timing covers the benchmark method only.
     */
    private static void settle( final Bootstrap bs )
    {
        bs.refresh();
        System.gc();
    }

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

    /** One move of the tree parent, back and forth between two targets - every child follows. */
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

    /** One duplication of the whole tree - every invocation leaves a new copy behind, so the corpus grows as it would in production. */
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
            // the operating role keeps full access in both variants, so every invocation stays permitted
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

    /** One permission change over the whole tree, alternating between two lists so every invocation writes a real change. */
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

        /** A deletion consumes its tree, so every invocation gets a fresh one - built and settled outside the measurement. */
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

    /** One deletion of the whole tree. */
    @Benchmark
    @Measurement( iterations = 3 )
    public DeleteNodeResult delete( final DeleteState s, final Measured measured )
        throws Exception
    {
        return measured.measure( TREE_NODES,
            () -> s.bs.callInDraftContext( () -> s.bs.nodeService.delete( DeleteNodeParams.create().nodeId( s.treeId ).build() ) ) );
    }
}

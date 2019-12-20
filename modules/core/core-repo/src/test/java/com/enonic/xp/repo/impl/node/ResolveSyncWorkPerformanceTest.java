package com.enonic.xp.repo.impl.node;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.repo.impl.version.TestQueryType;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class ResolveSyncWorkPerformanceTest
    extends AbstractNodeTest
{
    protected static final Repository MY_REPO = Repository.create().
        id( RepositoryId.from( "myrepo" ) ).
        branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
        build();

    private static final int NODE_SIZE = 22000;

    Context CONTEXT = ContextBuilder.create().
        branch( ContentConstants.BRANCH_DRAFT ).
        repositoryId( MY_REPO.getId() ).
        authInfo( NodeConstants.NODE_SU_AUTH_INFO ).
        build();

    Context CONTEXT_MASTER = ContextBuilder.create().
        branch( ContentConstants.BRANCH_MASTER ).
        repositoryId( MY_REPO.getId() ).
        authInfo( NodeConstants.NODE_SU_AUTH_INFO ).
        build();

    @BeforeEach
    public void setUp()
        throws Exception
    {
        CONTEXT.callWith( () -> {
            if ( repositoryService.get( MY_REPO.getId() ) == null )
            {
                createRepository( MY_REPO );
            }
            return this.createDefaultRootNode();
        } );
    }

    //    @Disabled
//    @Test
    private void clearAndInit()
        throws Exception
    {
        CONTEXT.callWith( () -> {

            Node rootNode = getNodeByPath( NodePath.create( NodePath.ROOT, "rootNode" ).build() );

            if ( rootNode != null )
            {
                doDeleteNode( rootNode.id() );
            }

            refresh();

            rootNode = createNode( CreateNodeParams.create().
                name( "rootNode" ).
                parent( NodePath.ROOT ).
                build(), false );

            createNodes( rootNode, NODE_SIZE, 1, 1 );

            return refresh();
        } );
    }

    //    @Disabled
    @Test
    public void testReferencePerformance()
        throws Exception
    {
        CONTEXT.callWith( () -> {

            Node rootNode = getNodeByPath( NodePath.create( NodePath.ROOT, "rootNode" ).build() );

            if ( rootNode == null )
            {
                clearAndInit();
                rootNode = getNodeByPath( NodePath.create( NodePath.ROOT, "rootNode" ).build() );
            }

            final ResolveSyncWorkCommand.Builder command = ResolveSyncWorkCommand.create().
                nodeId( rootNode.id() ).
                target( CONTEXT_MASTER.getBranch() ).
                indexServiceInternal( this.indexServiceInternal ).
                storageService( this.storageService ).
                searchService( this.searchService );

            int expected = NODE_SIZE + 1;

            //warm up
//            run( command, TestQueryType.BRANCHES_IN_VERSIONS, 50, expected );
            run( command, TestQueryType.IN_MEMORY, 50, expected );
            run( command, TestQueryType.COMPOSITE, 50, expected );
            run( command, TestQueryType.SORTED_TERMS, 50, expected );
            run( command, TestQueryType.RARE, 50, expected );

            Thread.sleep( 15000 );

            System.out.println( "============================================" );
            System.out.println( "------------------IN MEMORY-----------------" );
            run( command, TestQueryType.IN_MEMORY, 50, expected );

            Thread.sleep( 5000 );

            System.out.println( "============================================" );
            System.out.println( "------------------COMPOSITE-----------------" );
            run( command, TestQueryType.COMPOSITE, 50, expected );

            Thread.sleep( 5000 );

            System.out.println( "============================================" );
            System.out.println( "------------------RARE TERMS-----------------" );
            run( command, TestQueryType.RARE, 50, expected );

            Thread.sleep( 5000 );

            System.out.println( "============================================" );
            System.out.println( "------------------SORTED TERMS-----------------" );
            run( command, TestQueryType.SORTED_TERMS, 50, expected );

            Thread.sleep( 5000 );

        /*    System.out.println( "============================================" );
            System.out.println( "------------------BRANCHES IN VERSIONS-----------------" );
            run( command, TestQueryType.BRANCHES_IN_VERSIONS, 50, expected );*/

            publish( NODE_SIZE / 2 );

            expected = NODE_SIZE / 2;

            Thread.sleep( 15000 );

            System.out.println( "============================================" );
            System.out.println( "------------------SORTED TERMS-----------------" );
            run( command, TestQueryType.SORTED_TERMS, 50, expected );

            Thread.sleep( 5000 );

            System.out.println( "============================================" );
            System.out.println( "------------------COMPOSITE-----------------" );
            run( command, TestQueryType.COMPOSITE, 50, expected );

            Thread.sleep( 5000 );

            System.out.println( "============================================" );
            System.out.println( "------------------RARE TERMS-----------------" );
            run( command, TestQueryType.RARE, 50, expected );

            Thread.sleep( 5000 );

            System.out.println( "============================================" );
            System.out.println( "------------------IN MEMORY-----------------" );
            run( command, TestQueryType.IN_MEMORY, 50, expected );

            Thread.sleep( 5000 );

        /*    System.out.println( "============================================" );
            System.out.println( "------------------BRANCHES IN VERSIONS-----------------" );
            run( command, TestQueryType.BRANCHES_IN_VERSIONS, 50, expected );
*/
            publish( NODE_SIZE );

            expected = 0;

            Thread.sleep( 15000 );

            System.out.println( "============================================" );
            System.out.println( "------------------SORTED TERMS-----------------" );
            run( command, TestQueryType.SORTED_TERMS, 50, expected );

            Thread.sleep( 5000 );

            System.out.println( "============================================" );
            System.out.println( "------------------COMPOSITE-----------------" );
            run( command, TestQueryType.COMPOSITE, 50, expected );

            Thread.sleep( 5000 );

            System.out.println( "============================================" );
            System.out.println( "------------------RARE TERMS-----------------" );
            run( command, TestQueryType.RARE, 50, expected );

            Thread.sleep( 5000 );

            System.out.println( "============================================" );
            System.out.println( "------------------IN MEMORY-----------------" );
            run( command, TestQueryType.IN_MEMORY, 50, expected );

            Thread.sleep( 5000 );

           /* System.out.println( "============================================" );
            System.out.println( "------------------BRANCHES IN VERSIONS-----------------" );
            run( command, TestQueryType.BRANCHES_IN_VERSIONS, 50, expected );*/

            return 1;
        } );
    }

    void publish( final int number )
    {
        Node rootNode = getNodeByPath( NodePath.create( NodePath.ROOT, "rootNode" ).build() );

        final FindNodesByParentResult findNodesByParentResult = nodeService.findByParent( FindNodesByParentParams.create().
            recursive( true ).
            parentPath( NodePath.create( "/rootNode" ).
                build() ).
            build() );

        NodeIds.Builder nodesToPublish = NodeIds.create();

        int i = 0;

        for ( NodeId nodeId : findNodesByParentResult.getNodeIds() )
        {
            if ( i < number )
            {
                nodesToPublish.add( nodeId );
            }
            i++;
        }

        final PushNodesResult pushNodesResult = nodeService.push( NodeIds.create().
            add( rootNode.id() ).
            addAll( nodesToPublish.build() ).
            build(), CONTEXT_MASTER.getBranch() );

        nodeService.refresh( RefreshMode.ALL );

        System.out.println( "------------------Published: " + pushNodesResult.getSuccessful().getSize() + "-----------------" );

    }

    void run( ResolveSyncWorkCommand.Builder command, TestQueryType type, int times, int expected )
    {

        Duration total = Duration.ofSeconds( 0 );

        for ( int i = 0; i < times; i++ )
        {

            final Stopwatch started = Stopwatch.createStarted();

            final ResolveSyncWorkResult resolvedNodes = command.
                testQueryType( type ).
                build().
                execute();

            started.stop();

            org.junit.jupiter.api.Assertions.assertEquals( expected, resolvedNodes.getSize() );

//            System.out.println( "Executed " + resolvedNodes.getSize() + " in " + started.toString() + " ms" );

            total = total.plus( started.elapsed() );
        }
        System.out.println( "------------------Average: " + total.toMillis() / times + "-----------------" );
    }

}

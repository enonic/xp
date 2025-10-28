package com.enonic.xp.core.impl.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.CleanUpAuditLogListener;
import com.enonic.xp.audit.CleanUpAuditLogParams;
import com.enonic.xp.audit.CleanUpAuditLogResult;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.core.impl.audit.serializer.AuditLogSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeHits;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repository.RepositoryService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditLogServiceImplTest
{
    private NodeService nodeService;

    private AuditLogServiceImpl auditLogService;

    private LogAuditLogParams auditLogParams;

    private AuditLogConfig config;

    @BeforeEach
    void setUp()
    {
        PropertyTree data = new PropertyTree();
        data.setString( "a", "b" );
        data.setBoolean( "c", false );

        auditLogParams = LogAuditLogParams.create().
            type( "testType" ).
            source( "testSource" ).
            objectUris( AuditLogUris.from( "a:b:c", "d:e:f" ) ).
            data( data ).
            build();

        CreateNodeParams createNodeParams = AuditLogSerializer.toCreateNodeParams( auditLogParams ).
            setNodeId( new NodeId() ).
            build();

        Node node = Node.create().
            id( createNodeParams.getNodeId() ).
            data( createNodeParams.getData() ).
            build();

        nodeService = mock( NodeService.class );
        when( nodeService.create( any( CreateNodeParams.class ) ) ).thenReturn( node );
        when( nodeService.getById( any( NodeId.class ) ) ).thenReturn( node );
        when( nodeService.getByIds( any( NodeIds.class ) ) ).thenReturn( Nodes.from( node ) );
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).
            thenReturn( FindNodesByQueryResult.create().
                addNodeHit( NodeHit.create().
                    nodeId( node.id() ).
                    build() ).
                totalHits( 1 ).
                build() );
        IndexService indexService = mock( IndexService.class );
        when( indexService.isMaster() ).thenReturn( true );
        when( indexService.waitForYellowStatus() ).thenReturn( true );
        RepositoryService repositoryService = mock( RepositoryService.class );

        config = mock( AuditLogConfig.class );
        when( config.isEnabled() ).thenReturn( true );
        when( config.isOutputLogs() ).thenReturn( true );

        auditLogService = new AuditLogServiceImpl( config, nodeService );
        AuditLogRepoInitializer.create().
            setIndexService( indexService ).
            setRepositoryService( repositoryService ).
            build().
            initialize();
    }

    @Test
    void log_no_parameters()
    {
        assertThrows( NullPointerException.class, () -> {
            auditLogService.log( LogAuditLogParams.create().build() );
        } );
    }

    @Test
    void log_with_only_type()
    {
        AuditLog log = auditLogService.log( LogAuditLogParams.create().type( "test" ).build() );
        assertLog( log );
    }

    @Test
    void get_by_id()
    {
        AuditLog log = auditLogService.get( new AuditLogId() );
        assertLog( log );
    }

    @Test
    void find_no_filter()
    {
        FindAuditLogResult result = auditLogService.find( FindAuditLogParams.create().build() );
        assertEquals( 1, result.getHits().getSize() );
        assertEquals( 1, result.getTotal() );
        assertLog( result.getHits().first() );
    }

    @Test
    void find()
    {
        FindAuditLogResult result = auditLogService.find( FindAuditLogParams.create().
            type( auditLogParams.getType() ).
            build() );
        assertEquals( 1, result.getHits().getSize() );
        assertEquals( 1, result.getTotal() );
        assertLog( result.getHits().first() );
    }

    @Test
    void cleanUpOneEmpty()
    {
        when( nodeService.delete( any() ) ).thenAnswer( AuditLogServiceImplTest::answerDeleted );

        when( config.ageThreshold() ).thenReturn( "PT1s" );

        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create().build() );

        final CleanUpAuditLogListener listener = mock( CleanUpAuditLogListener.class );

        final CleanUpAuditLogResult result = auditLogService.cleanUp( CleanUpAuditLogParams.create().listener( listener ).
            build() );

        assertEquals( 0, result.getDeleted() );
        verify( listener, times( 0 ) ).start( anyInt() );
        verify( listener, times( 0 ) ).processed();
        verify( listener, times( 0 ) ).finished();
    }

    @Test
    void cleanUpOneBatch()
    {
        when( nodeService.delete( any() ) ).thenAnswer( AuditLogServiceImplTest::answerDeleted );

        when( config.ageThreshold() ).thenReturn( "PT1s" );

        final FindNodesByQueryResult.Builder queryResult = FindNodesByQueryResult.create().totalHits( 3 );

        createHits( 3 ).forEach( queryResult::addNodeHit );

        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).
            thenReturn( queryResult.build() ).
            thenReturn( FindNodesByQueryResult.create().build() );

        final CleanUpAuditLogListener listener = mock( CleanUpAuditLogListener.class );

        final CleanUpAuditLogResult result = auditLogService.cleanUp( CleanUpAuditLogParams.create().
            listener( listener ).
            build() );

        assertEquals( 3, result.getDeleted() );
        verify( listener, times( 1 ) ).start( 10_000 );
        verify( listener, times( 3 ) ).processed();
        verify( listener, times( 1 ) ).finished();
    }

    @Test
    void cleanUpMultipleBatch()
    {
        when( nodeService.delete( any() ) ).thenAnswer( AuditLogServiceImplTest::answerDeleted );

        when( config.ageThreshold() ).thenReturn( "PT1s" );

        final FindNodesByQueryResult.Builder queryResult1 = FindNodesByQueryResult.create().totalHits( 10500 );
        createHits( 10000 ).forEach( queryResult1::addNodeHit );

        final FindNodesByQueryResult.Builder queryResult2 = FindNodesByQueryResult.create().totalHits( 10500 );
        createHits( 500 ).forEach( queryResult2::addNodeHit );

        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).
            thenReturn( queryResult1.build() ).
            thenReturn( queryResult2.build() ).
            thenReturn( FindNodesByQueryResult.create().build() );

        final CleanUpAuditLogListener listener = mock( CleanUpAuditLogListener.class );

        final CleanUpAuditLogResult result = auditLogService.cleanUp( CleanUpAuditLogParams.create().
            listener( listener ).
            build() );

        assertEquals( 10500, result.getDeleted() );
        verify( listener, times( 1 ) ).start( 10_000 );
        verify( listener, times( 10_500 ) ).processed();
        verify( listener, times( 1 ) ).finished();
    }

    private NodeHits createHits( final int number )
    {
        final NodeHits.Builder hits = NodeHits.create();
        for ( int i = 1; i <= number; i++ )
        {
            hits.add( NodeHit.create().nodeId( NodeId.from( "node-id-" + i ) ).build() );
        }
        return hits.build();
    }

    private void assertLog( AuditLog log )
    {
        assertNotNull( log.getId() );
        assertNotNull( log.getType() );
        assertEquals( auditLogParams.getType(), log.getType() );
        assertNotNull( log.getTime() );
        assertEquals( auditLogParams.getTime(), log.getTime() );
        assertNotNull( log.getSource() );
        assertEquals( auditLogParams.getSource(), log.getSource() );
        assertNotNull( log.getUser() );
        assertEquals( auditLogParams.getUser(), log.getUser() );
        assertNotNull( log.getObjectUris() );
        assertEquals( 2, log.getObjectUris().getSize() );
        assertEquals( auditLogParams.getObjectUris(), log.getObjectUris() );
        assertNotNull( log.getData() );
        assertEquals( auditLogParams.getData(), log.getData() );
    }

    private static DeleteNodeResult answerDeleted( InvocationOnMock answer )
    {
        return DeleteNodeResult.create()
            .nodeIds( NodeIds.from( answer.getArgument( 0, DeleteNodeParams.class ).getNodeId() ) )
            .build();
    }
}

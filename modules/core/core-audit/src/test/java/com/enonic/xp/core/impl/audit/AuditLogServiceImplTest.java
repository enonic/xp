package com.enonic.xp.core.impl.audit;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import com.enonic.xp.node.EnumerateNodesParams;
import com.enonic.xp.node.EnumerateNodesResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEnumerationEntry;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repository.internal.InternalRepositoryService;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.security.PrincipalKey;

import static java.util.Objects.requireNonNullElse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

        auditLogParams = LogAuditLogParams.create()
            .type( "testType" )
            .source( "testSource" )
            .objectUris( AuditLogUris.from( "a:b:c", "d:e:f" ) )
            .data( data )
            .build();

        CreateNodeParams createNodeParams = AuditLogSerializer.toCreateNodeParams( auditLogParams ).setNodeId( new NodeId() ).build();

        Node node = Node.create().id( createNodeParams.getNodeId() ).data( createNodeParams.getData() ).build();

        nodeService = mock( NodeService.class );
        when( nodeService.create( any( CreateNodeParams.class ) ) ).thenReturn( node );
        when( nodeService.getById( any( NodeId.class ) ) ).thenReturn( node );
        when( nodeService.getByIds( any( NodeIds.class ) ) ).thenReturn( Nodes.from( node ) );
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn(
            FindNodesByQueryResult.create().addNodeHit( NodeHit.create().nodeId( node.id() ).build() ).totalHits( 1 ).build() );
        IndexService indexService = mock( IndexService.class );
        when( indexService.isMaster() ).thenReturn( true );
        when( indexService.waitForYellowStatus() ).thenReturn( true );
        InternalRepositoryService repositoryService = mock( InternalRepositoryService.class );

        config = mock( AuditLogConfig.class );
        when( config.isEnabled() ).thenReturn( true );
        when( config.isOutputLogs() ).thenReturn( true );

        auditLogService = new AuditLogServiceImpl( config, nodeService );
        AuditLogRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();
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
        FindAuditLogResult result = auditLogService.find( FindAuditLogParams.create().type( auditLogParams.getType() ).build() );
        assertEquals( 1, result.getHits().getSize() );
        assertEquals( 1, result.getTotal() );
        assertLog( result.getHits().first() );
    }

    @Test
    void cleanUpRequiresAdmin()
    {
        assertThrows( ForbiddenAccessException.class, () -> auditLogService.cleanUp( CleanUpAuditLogParams.create().build() ) );
        verifyNoInteractions( nodeService );
    }

    @Test
    void cleanUpOneEmpty()
    {
        when( config.ageThreshold() ).thenReturn( "PT1s" );

        when( nodeService.enumerate( any( EnumerateNodesParams.class ) ) ).thenReturn( EnumerateNodesResult.create().build() );

        final CleanUpAuditLogListener listener = mock( CleanUpAuditLogListener.class );

        final CleanUpAuditLogResult result = AuditLogContext.createAdminContext().callWith( () -> auditLogService.cleanUp( CleanUpAuditLogParams.create().listener( listener ).build() ) );

        assertEquals( 0, result.getDeleted() );
        verify( listener, times( 1 ) ).resolved( 0 );
        verify( listener, times( 0 ) ).start( anyInt() );
        verify( listener, times( 0 ) ).recordsDeleted( anyInt() );
        verify( listener, times( 0 ) ).finished();
    }

    @Test
    void cleanUpOneBatch()
    {
        when( nodeService.delete( any() ) ).thenAnswer( AuditLogServiceImplTest::answerDeleted );

        when( config.ageThreshold() ).thenReturn( "PT1s" );

        when( nodeService.enumerate( any( EnumerateNodesParams.class ) ) ).thenReturn(
            createBatch( 3, Instant.now().minusSeconds( 60 ), null ) );

        final CleanUpAuditLogListener listener = mock( CleanUpAuditLogListener.class );

        final CleanUpAuditLogResult result = AuditLogContext.createAdminContext().callWith( () -> auditLogService.cleanUp( CleanUpAuditLogParams.create().listener( listener ).build() ) );

        assertEquals( 3, result.getDeleted() );
        verify( listener, times( 1 ) ).resolved( 3 );
        verify( listener, times( 1 ) ).start( 10_000 );
        verify( listener, times( 3 ) ).recordsDeleted( 1 );
        verify( listener, times( 1 ) ).finished();
    }

    @Test
    void cleanUpMultipleBatch()
    {
        when( nodeService.delete( any() ) ).thenAnswer( AuditLogServiceImplTest::answerDeleted );

        when( config.ageThreshold() ).thenReturn( "PT1s" );

        when( nodeService.enumerate( any( EnumerateNodesParams.class ) ) ).thenReturn(
                createBatch( 10000, Instant.now().minusSeconds( 60 ), "/node-10000", 10_500 ) )
            .thenReturn( createBatch( 500, Instant.now().minusSeconds( 60 ), null, 500 ) );

        final CleanUpAuditLogListener listener = mock( CleanUpAuditLogListener.class );

        final CleanUpAuditLogResult result = AuditLogContext.createAdminContext().callWith( () -> auditLogService.cleanUp( CleanUpAuditLogParams.create().listener( listener ).build() ) );

        assertEquals( 10500, result.getDeleted() );
        verify( listener, times( 1 ) ).resolved( 10_500 );
        verify( listener, times( 1 ) ).resolved( anyInt() );
        verify( listener, times( 1 ) ).start( 10_000 );
        verify( listener, times( 10_500 ) ).recordsDeleted( 1 );
        verify( listener, times( 1 ) ).finished();
    }

    @Test
    void cleanUpBoundsTheEnumerationByTheThreshold()
    {
        when( nodeService.delete( any() ) ).thenAnswer( AuditLogServiceImplTest::answerDeleted );

        when( config.ageThreshold() ).thenReturn( "PT1H" );

        final Instant oldestAccepted = Instant.now().minus( Duration.ofHours( 1 ) ).minusSeconds( 5 );

        when( nodeService.enumerate( any( EnumerateNodesParams.class ) ) ).thenReturn(
            createBatch( 2, Instant.now().minus( Duration.ofHours( 2 ) ), null ) );

        final CleanUpAuditLogResult result = AuditLogContext.createAdminContext().callWith( () -> auditLogService.cleanUp( CleanUpAuditLogParams.create().build() ) );

        assertEquals( 2, result.getDeleted() );

        final ArgumentCaptor<EnumerateNodesParams> params = ArgumentCaptor.forClass( EnumerateNodesParams.class );
        verify( nodeService ).enumerate( params.capture() );
        assertTrue( params.getValue().getModifiedBefore().isAfter( oldestAccepted ) );
        assertTrue( params.getValue().getModifiedBefore().isBefore( Instant.now() ) );
    }

    private EnumerateNodesResult createBatch( final int number, final Instant timestamp, final String cursor )
    {
        return createBatch( number, timestamp, cursor, number );
    }

    private EnumerateNodesResult createBatch( final int number, final Instant timestamp, final String cursor, final int remaining )
    {
        final EnumerateNodesResult.Builder batch = EnumerateNodesResult.create().cursor( cursor ).remaining( remaining );
        for ( int i = 1; i <= number; i++ )
        {
            batch.addEntry( new NodeEnumerationEntry( NodeId.from( "node-id-" + i ), new NodePath( "/node-" + i ), timestamp, NodeVersionId.from( "version-" + i ) ) );
        }
        return batch.build();
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
        assertEquals( requireNonNullElse( auditLogParams.getUser(), PrincipalKey.ofAnonymous() ), log.getUser() );
        assertNotNull( log.getObjectUris() );
        assertEquals( 2, log.getObjectUris().getSize() );
        assertEquals( auditLogParams.getObjectUris(), log.getObjectUris() );
        assertNotNull( log.getData() );
        assertEquals( auditLogParams.getData(), log.getData() );
    }

    private static DeleteNodeResult answerDeleted( InvocationOnMock answer )
    {
        return DeleteNodeResult.create()
            .add( new DeleteNodeResult.Result( answer.getArgument( 0, DeleteNodeParams.class ).getNodeId(),
                                               NodeVersionId.from( "nodeversionid" ) ) )
            .build();
    }
}

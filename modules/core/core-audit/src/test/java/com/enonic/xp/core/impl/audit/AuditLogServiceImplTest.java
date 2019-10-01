package com.enonic.xp.core.impl.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.core.impl.audit.serializer.AuditLogSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repository.RepositoryService;

import static org.junit.jupiter.api.Assertions.*;

public class AuditLogServiceImplTest
{

    private AuditLogServiceImpl auditLogService;

    private LogAuditLogParams auditLogParams;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setString( "a", "b" );
        data.setBoolean( "c", false );

        auditLogParams = LogAuditLogParams.create().
            type( "testType" ).
            source( "testSource" ).
            message( "testMessage" ).
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

        NodeService nodeService = Mockito.mock( NodeService.class );
        Mockito.when( nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenReturn( node );
        Mockito.when( nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( node );
        Mockito.when( nodeService.getByIds( Mockito.any( NodeIds.class ) ) ).thenReturn( Nodes.from( node ) );
        Mockito.when( nodeService.findByQuery( Mockito.any( NodeQuery.class ) ) ).
            thenReturn( FindNodesByQueryResult.create().
                addNodeHit( NodeHit.create().
                    nodeId( node.id() ).
                    build() ).
                totalHits( 1 ).
                hits( 1 ).
                build() );
        IndexService indexService = Mockito.mock( IndexService.class );
        Mockito.when( indexService.isMaster() ).thenReturn( true );
        RepositoryService repositoryService = Mockito.mock( RepositoryService.class );

        AuditLogConfig config = Mockito.mock( AuditLogConfig.class );
        Mockito.when( config.isEnabled() ).thenReturn( true );
        Mockito.when( config.isOutputLogs() ).thenReturn( true );

        auditLogService = new AuditLogServiceImpl();
        auditLogService.setNodeService( nodeService );
        auditLogService.setIndexService( indexService );
        auditLogService.setRepositoryService( repositoryService );
        auditLogService.setConfig( config );
        auditLogService.initialize();
    }

    @Test
    public void log_no_parameters()
    {
        assertThrows( NullPointerException.class, () -> {
            auditLogService.log( LogAuditLogParams.create().build() );
        } );
    }

    @Test
    public void log_with_only_type()
    {
        AuditLog log = auditLogService.log( LogAuditLogParams.create().type( "test" ).build() );
        assertLog( log );
    }

    @Test
    public void get_by_id()
    {
        AuditLog log = auditLogService.get( new AuditLogId() );
        assertLog( log );
    }

    @Test
    public void find_no_filter()
    {
        FindAuditLogResult result = auditLogService.find( FindAuditLogParams.create().build() );
        assertEquals( 1, result.getCount() );
        assertEquals( 1, result.getTotal() );
        assertLog( result.getHits().first() );
    }

    @Test
    public void find()
    {
        FindAuditLogResult result = auditLogService.find( FindAuditLogParams.create().
            type( auditLogParams.getType() ).
            build() );
        assertEquals( 1, result.getCount() );
        assertEquals( 1, result.getTotal() );
        assertLog( result.getHits().first() );
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
        assertNotNull( log.getMessage() );
        assertEquals( auditLogParams.getMessage(), log.getMessage() );
        assertNotNull( log.getObjectUris() );
        assertEquals( 2, log.getObjectUris().getSize() );
        assertEquals( auditLogParams.getObjectUris(), log.getObjectUris() );
        assertNotNull( log.getData() );
        assertEquals( auditLogParams.getData(), log.getData() );
    }
}
package com.enonic.xp.core.impl.auditlog;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.auditlog.FindAuditLogParams;
import com.enonic.xp.auditlog.FindAuditLogResult;
import com.enonic.xp.core.impl.auditlog.serializer.AuditLogSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;

import static org.junit.Assert.*;

public class AuditLogServiceImplTest
{

    private NodeService nodeService;

    private AuditLogServiceImpl auditLogService;

    private AuditLogParams auditLogParams;

    @Before
    public void setUp()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setString( "a", "b" );
        data.setBoolean( "c", false );

        auditLogParams = AuditLogParams.create().
            type( "testType" ).
            source( "testSource" ).
            message( "testMessage" ).
            objectUris( ImmutableSet.<URI>builder().add( URI.create( "a:b:c" ), URI.create( "d:e:f" ) ).build() ).
            data( data ).
            build();

        CreateNodeParams createNodeParams = AuditLogSerializer.toCreateNodeParams( auditLogParams ).
            setNodeId( new NodeId() ).
            build();

        Node node = Node.create().
            id( createNodeParams.getNodeId() ).
            data( createNodeParams.getData() ).
            build();

        nodeService = Mockito.mock( NodeService.class );
        Mockito.when( this.nodeService.create( Mockito.any( CreateNodeParams.class ) ) ).thenReturn( node );
        Mockito.when( this.nodeService.getById( Mockito.any( NodeId.class ) ) ).thenReturn( node );
        Mockito.when( this.nodeService.getByIds( Mockito.any( NodeIds.class ) ) ).thenReturn( Nodes.from( node ) );
        Mockito.when( this.nodeService.findByQuery( Mockito.any( NodeQuery.class ) ) ).
            thenReturn( FindNodesByQueryResult.create().
                addNodeHit( NodeHit.create().
                    nodeId( node.id() ).
                    build() ).
                totalHits( 1 ).
                hits( 1 ).
                build() );

        auditLogService = new AuditLogServiceImpl();
        auditLogService.setNodeService( nodeService );

        AuditLogConfig config = Mockito.mock( AuditLogConfig.class );
        Mockito.when( config.enabled() ).thenReturn( true );
        Mockito.when( config.outputLogs() ).thenReturn( true );
        auditLogService.setConfig( config );
    }

    @Test(expected = NullPointerException.class)
    public void log_no_parameters()
    {
        auditLogService.log( AuditLogParams.create().build() );
    }

    @Test
    public void log_with_only_type()
    {
        AuditLog log = auditLogService.log( AuditLogParams.create().type( "test" ).build() );
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
        assertEquals( 0, result.getCount() );
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
        assertEquals( 2, log.getObjectUris().size() );
        assertEquals( auditLogParams.getObjectUris(), log.getObjectUris() );
        assertNotNull( log.getData() );
        assertEquals( auditLogParams.getData(), log.getData() );
    }
}